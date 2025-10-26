package com.app.turismo.service;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.turismo.model.*;
import com.app.turismo.repository.*;
import com.app.turismo.dto.PaqueteDTO;
import java.util.stream.Collectors;

@Service
public class PaqueteService {

    @Autowired
    private PaqueteRepository paqueteRepository;

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private ActividadRepository actividadRepository;

    @Autowired
    private TransporteRepository transporteRepository;

    @Autowired
    private DestinoRepository destinoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Lista todos los paquetes registrados
     */
    public List<PaqueteDTO> listarPaquetes() {
        return paqueteRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca un paquete por su ID
     */
    public Optional<PaqueteDTO> buscarPaquetePorId(Long id) {
        return paqueteRepository.findById(id)
                .map(this::mapToDTO);
    }

    // Método para mapear PaqueteEntity a PaqueteDTO
    private PaqueteDTO mapToDTO(PaqueteEntity entity) {
        PaqueteDTO dto = new PaqueteDTO();
        if (entity.getUsuario() != null)
            dto.usuarioId = entity.getUsuario().getUser_id();
        if (entity.getDestino() != null)
            dto.destinoId = entity.getDestino().getDestino_id();
        dto.origen = entity.getOrigen();
        dto.fechaInicio = entity.getFechaInicio();
        dto.fechaFin = entity.getFechaFin();
        dto.costoTotal = entity.getCostoTotal();
        dto.nombre = entity.getNombre();
        dto.descripcion = entity.getDescripcion();
        dto.numAdultos = entity.getNumAdultos();
        dto.numNinos = entity.getNumNinos();
        dto.tipoPaquete = entity.getTipoPaquete();
        if (entity.getHoteles() != null)
            dto.hoteles = entity.getHoteles().stream().map(HotelEntity::getHotel_id).collect(Collectors.toList());
        if (entity.getTransportes() != null)
            dto.transportes = entity.getTransportes().stream().map(TransporteEntity::getTransporte_id)
                    .collect(Collectors.toList());
        if (entity.getActividades() != null)
            dto.actividades = entity.getActividades().stream().map(ActividadEntity::getActividad_id)
                    .collect(Collectors.toList());
        return dto;
    }

    /**
     * Guarda un nuevo paquete turístico, calculando automáticamente su costo total
     * y el tipo de transporte según el origen y el destino.
     */
    public PaqueteEntity guardarPaquete(PaqueteEntity paquete) {
        // Asociar destino persistente
        if (paquete.getDestino() != null && paquete.getDestino().getDestino_id() != null) {
            DestinoEntity destino = destinoRepository.findById(paquete.getDestino().getDestino_id())
                    .orElseThrow(() -> new RuntimeException("Destino no encontrado"));
            paquete.setDestino(destino);
        }
        // Asociar usuario persistente
        if (paquete.getUsuario() != null && paquete.getUsuario().getUser_id() != null) {
            UsuarioEntity usuario = usuarioRepository.findById(paquete.getUsuario().getUser_id())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            paquete.setUsuario(usuario);
        }
        // Asociar hoteles persistentes
        if (paquete.getHoteles() != null) {
            List<HotelEntity> hoteles = paquete.getHoteles().stream()
                    .map(h -> hotelRepository.findById(h.getHotel_id())
                            .orElseThrow(() -> new RuntimeException("Hotel no encontrado")))
                    .toList();
            paquete.setHoteles(hoteles);
        }
        // Asociar transportes persistentes
        if (paquete.getTransportes() != null) {
            List<TransporteEntity> transportes = paquete.getTransportes().stream()
                    .map(t -> transporteRepository.findById(t.getTransporte_id())
                            .orElseThrow(() -> new RuntimeException("Transporte no encontrado")))
                    .toList();
            paquete.setTransportes(transportes);
        }
        // Asociar actividades persistentes
        if (paquete.getActividades() != null) {
            List<ActividadEntity> actividades = paquete.getActividades().stream()
                    .map(a -> actividadRepository.findById(a.getActividad_id())
                            .orElseThrow(() -> new RuntimeException("Actividad no encontrada")))
                    .toList();
            paquete.setActividades(actividades);
        }

        // 🔹 Calcular la duración del viaje
        long dias = ChronoUnit.DAYS.between(paquete.getFechaInicio(), paquete.getFechaFin());
        if (dias <= 0) {
            dias = 1; // mínimo un día
        }

        // 🔹 Obtener los hoteles del destino
        List<HotelEntity> hoteles = hotelRepository.findByDestino(paquete.getDestino());
        double costoHotelAdulto = hoteles.stream().mapToDouble(HotelEntity::getTarifaAdulto).average().orElse(0);
        double costoHotelNino = hoteles.stream().mapToDouble(HotelEntity::getTarifaNino).average().orElse(0);

        // 🔹 Costo de actividades (promedio)
        double costoActividades = actividadRepository.findByDestino(paquete.getDestino())
                .stream()
                .mapToDouble(ActividadEntity::getPrecio)
                .average()
                .orElse(0);

        // 🔹 Determinar tipo de transporte automáticamente
        String tipoTransporte = determinarTipoTransporte(paquete.getOrigen(), paquete.getDestino().getUbicacion());

        // 🔹 Buscar transporte adecuado
        double costoTransporte = transporteRepository.findByDestino(paquete.getDestino())
                .stream()
                .filter(t -> t.getTipo().equalsIgnoreCase(tipoTransporte))
                .mapToDouble(TransporteEntity::getPrecio)
                .average()
                .orElse(calcularCostoBase(tipoTransporte)); // usa valor base si no hay transporte registrado

        // 🔹 Calcular total
        double total = ((paquete.getNumAdultos() * costoHotelAdulto)
                + (paquete.getNumNinos() * costoHotelNino)
                + (costoActividades * (paquete.getNumAdultos() + paquete.getNumNinos()))
                + costoTransporte) * dias;

        paquete.setCostoTotal(total);
        paquete.setTipoPaquete(tipoTransporte); // guardamos tipo de transporte como referencia

        return paqueteRepository.save(paquete);
    }

    /**
     * Determina el tipo de transporte más adecuado según el origen y el destino.
     */
    private String determinarTipoTransporte(String origen, String destinoUbicacion) {
        if (origen == null || destinoUbicacion == null)
            return "terrestre";

        origen = origen.toLowerCase();
        destinoUbicacion = destinoUbicacion.toLowerCase();

        // 🔸 Si están en el mismo departamento o región
        if (origen.contains("córdoba") && destinoUbicacion.contains("córdoba"))
            return "terrestre";
        if (origen.contains("sucre") && destinoUbicacion.contains("sucre"))
            return "terrestre";
        if (origen.contains("antioquia") && destinoUbicacion.contains("antioquia"))
            return "terrestre";

        // 🔸 Si el destino es costero y el origen no
        if ((destinoUbicacion.contains("coveñas") || destinoUbicacion.contains("cartagena")
                || destinoUbicacion.contains("capurganá"))
                && !origen.contains("costa")) {
            return "marítimo";
        }

        // 🔸 Por defecto si están lejos → aéreo
        return "aéreo";
    }

    /**
     * Define costos base por tipo de transporte (si no hay datos en DB)
     */
    private double calcularCostoBase(String tipo) {
        switch (tipo.toLowerCase()) {
            case "aéreo":
                return 400000;
            case "marítimo":
                return 180000;
            default:
                return 90000;
        }
    }

    /**
     * Actualiza un paquete existente
     */
    public PaqueteEntity actualizarPaquete(Long id, PaqueteEntity paqueteActualizado) {
        return paqueteRepository.findById(id)
                .map(paquete -> {
                    paquete.setNombre(paqueteActualizado.getNombre());
                    paquete.setDescripcion(paqueteActualizado.getDescripcion());
                    paquete.setFechaInicio(paqueteActualizado.getFechaInicio());
                    paquete.setFechaFin(paqueteActualizado.getFechaFin());
                    paquete.setDestino(paqueteActualizado.getDestino());
                    paquete.setOrigen(paqueteActualizado.getOrigen());
                    paquete.setNumAdultos(paqueteActualizado.getNumAdultos());
                    paquete.setNumNinos(paqueteActualizado.getNumNinos());
                    return guardarPaquete(paquete); // recalcula todo
                })
                .orElseThrow(() -> new RuntimeException("Paquete no encontrado con id: " + id));
    }

    /**
     * Elimina un paquete por ID
     */
    public void eliminarPaquete(Long id) {
        if (!paqueteRepository.existsById(id)) {
            throw new RuntimeException("No existe un paquete con el id: " + id);
        }
        paqueteRepository.deleteById(id);
    }
}
