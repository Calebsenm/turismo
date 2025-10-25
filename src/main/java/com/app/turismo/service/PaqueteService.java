package com.app.turismo.service;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.turismo.model.*;
import com.app.turismo.repository.*;

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

    /**
     * Lista todos los paquetes registrados
     */
    public List<PaqueteEntity> listarPaquetes() {
        return paqueteRepository.findAll();
    }

    /**
     * Busca un paquete por su ID
     */
    public Optional<PaqueteEntity> buscarPaquetePorId(Long id) {
        return paqueteRepository.findById(id);
    }

    /**
     * Guarda un nuevo paquete turístico, calculando automáticamente su costo total
     * y el tipo de transporte según el origen y el destino.
     */
    public PaqueteEntity guardarPaquete(PaqueteEntity paquete) {

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
