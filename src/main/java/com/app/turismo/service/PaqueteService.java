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
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
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
            dto.hoteles = entity.getHoteles().stream()
                .filter(java.util.Objects::nonNull)
                .map(HotelEntity::getHotel_id)
                .collect(Collectors.toList());
        if (entity.getTransportes() != null)
            dto.transportes = entity.getTransportes().stream()
                .filter(java.util.Objects::nonNull)
                .map(TransporteEntity::getTransporte_id)
                .collect(Collectors.toList());
        if (entity.getActividades() != null)
            dto.actividades = entity.getActividades().stream()
                .filter(java.util.Objects::nonNull)
                .map(ActividadEntity::getActividad_id)
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

    /**
     * Genera un archivo PDF para la cotización de un paquete específico.
     * @param paqueteId El ID del paquete a exportar.
     * @return Un arreglo de bytes que representa el archivo PDF.
     * @throws IOException Si ocurre un error durante la generación del PDF.
     */
    @Transactional(readOnly = true)
    public byte[] generarPdfPaquete(Long paqueteId) throws IOException {
        PaqueteEntity paquete = paqueteRepository.findById(paqueteId)
                .orElseThrow(() -> new RuntimeException("Paquete no encontrado con id: " + paqueteId));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.A4);

        // Formateador de moneda
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));
        currencyFormatter.setMaximumFractionDigits(0);

        // Título
        document.add(new Paragraph("Cotización de Paquete Turístico")
                .setTextAlignment(TextAlignment.CENTER)
                .setBold()
                .setFontSize(20));

        // Detalles del paquete
        document.add(new Paragraph("\n"));
        document.add(new Paragraph("Nombre del Paquete: " + paquete.getNombre()).setFontSize(14));
        document.add(new Paragraph("Descripción: " + paquete.getDescripcion()));
        document.add(new Paragraph("Origen: " + paquete.getOrigen()));
        if (paquete.getDestino() != null) {
            document.add(new Paragraph("Destino: " + paquete.getDestino().getNombre()));
        }
        document.add(new Paragraph("Fechas: " + paquete.getFechaInicio() + " al " + paquete.getFechaFin()));
        document.add(new Paragraph("Viajeros: " + paquete.getNumAdultos() + " Adultos, " + paquete.getNumNinos() + " Niños"));

        // Desglose de servicios
        document.add(new Paragraph("\nServicios Incluidos:").setBold());

        // Hoteles
        if (paquete.getHoteles() != null && !paquete.getHoteles().isEmpty()) {
            paquete.getHoteles().forEach(hotel -> 
                document.add(new Paragraph(" - Hospedaje: " + hotel.getNombre()))
            );
        }

        // Transportes
        if (paquete.getTransportes() != null && !paquete.getTransportes().isEmpty()) {
            paquete.getTransportes().forEach(transporte -> 
                document.add(new Paragraph(" - Transporte: " + transporte.getTipo() + " (" + transporte.getEmpresa() + ")"))
            );
        }

        // Actividades
        if (paquete.getActividades() != null && !paquete.getActividades().isEmpty()) {
            paquete.getActividades().forEach(actividad -> 
                document.add(new Paragraph(" - Actividad: " + actividad.getNombre()))
            );
        }

        // Total
        document.add(new Paragraph("\nCosto Total del Paquete: " + currencyFormatter.format(paquete.getCostoTotal()))
                .setTextAlignment(TextAlignment.RIGHT).setBold().setFontSize(16));

        document.close();
        return baos.toByteArray();
    }
}
