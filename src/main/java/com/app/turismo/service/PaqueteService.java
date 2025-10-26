package com.app.turismo.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.text.NumberFormat;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.turismo.model.*;
import com.app.turismo.repository.*;
// import com.itextpdf.kernel.geom.PageSize;
// import com.itextpdf.kernel.pdf.PdfDocument;
// import com.itextpdf.kernel.pdf.PdfWriter;
// import com.itextpdf.layout.Document;
// import com.itextpdf.layout.element.Paragraph;
// import com.itextpdf.layout.properties.TextAlignment;
import com.app.turismo.dto.PaqueteDTO;
import java.util.stream.Collectors;

@Service
public class PaqueteService {
    // Método de mapeo de entidad a DTO
    private PaqueteDTO mapToDTO(PaqueteEntity entity) {
        PaqueteDTO dto = new PaqueteDTO();
        dto.usuarioId = entity.getUsuario() != null ? entity.getUsuario().getUser_id() : null;
        dto.destinoId = entity.getDestino() != null ? entity.getDestino().getDestino_id() : null;
        dto.origen = entity.getOrigen();
        dto.fechaInicio = entity.getFechaInicio();
        dto.fechaFin = entity.getFechaFin();
        dto.costoTotal = entity.getCostoTotal();
        dto.nombre = entity.getNombre();
        dto.descripcion = entity.getDescripcion();
        dto.numAdultos = entity.getNumAdultos();
        dto.numNinos = entity.getNumNinos();
        dto.tipoPaquete = entity.getTipoPaquete();
        // Convertir listas de entidades a listas de IDs
        dto.hoteles = entity.getHoteles() != null ? entity.getHoteles().stream().map(h -> h.getHotel_id()).toList()
                : null;
        dto.transportes = entity.getTransportes() != null
                ? entity.getTransportes().stream().map(t -> t.getTransporte_id()).toList()
                : null;
        dto.actividades = entity.getActividades() != null
                ? entity.getActividades().stream().map(a -> a.getActividad_id()).toList()
                : null;
        return dto;
    }

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
    @Transactional(readOnly = true)
    public List<PaqueteDTO> listarPaquetes() {
        return paqueteRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca un paquete por su ID
     */
    @Transactional(readOnly = true)
    public Optional<PaqueteDTO> buscarPaquetePorId(Long id) {
        return paqueteRepository.findById(id).map(this::mapToDTO);
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
                    // Si necesitas recalcular el costo, agrega la lógica aquí
                    return paqueteRepository.save(paquete);
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
