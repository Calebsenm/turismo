package com.app.turismo.pdf;

import com.app.turismo.dto.PaqueteCompleto;
import com.app.turismo.exception.PdfGenerationException;
import com.app.turismo.model.*;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Set;

/**
 * Componente responsable de la generación técnica del documento PDF.
 * Utiliza iText 7 para crear documentos PDF con formato profesional.
 */
@Component
@Slf4j
public class PdfGenerator {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final NumberFormat CURRENCY_FORMATTER = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));
    private static final DeviceRgb COLOR_AZUL_TITULO = new DeviceRgb(46, 134, 171); // #2E86AB

    /**
     * Crea un documento PDF completo con toda la información del paquete turístico.
     *
     * @param paqueteCompleto Datos completos del paquete
     * @return Array de bytes con el contenido del PDF
     * @throws PdfGenerationException si ocurre un error durante la generación
     */
    public byte[] crearDocumentoPdf(PaqueteCompleto paqueteCompleto) {
        log.info("Iniciando generación de documento PDF para paquete ID: {}", 
                paqueteCompleto.getPaquete() != null ? paqueteCompleto.getPaquete().getPaquete_id() : "desconocido");
        
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc, PageSize.A4);
            
            // Configurar márgenes
            document.setMargins(50, 50, 50, 50);

            log.debug("Agregando contenido al documento PDF");
            
            // Agregar contenido al documento
            agregarEncabezado(document);
            agregarInformacionPaquete(document, paqueteCompleto.getPaquete());
            agregarDetallesDestino(document, paqueteCompleto.getDestino());
            agregarHoteles(document, paqueteCompleto.getHoteles());
            agregarTransportes(document, paqueteCompleto.getTransportes());
            agregarActividades(document, paqueteCompleto.getActividades());
            agregarPiePagina(document, pdfDoc);

            document.close();
            
            log.info("PDF generado exitosamente, tamaño: {} bytes", baos.size());
            return baos.toByteArray();
            
        } catch (Exception e) {
            log.error("Error al generar PDF para paquete ID {}: {}", 
                    paqueteCompleto.getPaquete() != null ? paqueteCompleto.getPaquete().getPaquete_id() : "desconocido",
                    e.getMessage(), e);
            throw new PdfGenerationException("Error interno al generar el PDF", e);
        }
    }

    /**
     * Agrega el encabezado del documento con título y fecha de generación.
     */
    private void agregarEncabezado(Document document) {
        // Título principal
        Paragraph titulo = new Paragraph("PAQUETE TURÍSTICO")
                .setFontSize(24)
                .setBold()
                .setFontColor(COLOR_AZUL_TITULO)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(10);
        document.add(titulo);

        // Fecha de generación
        String fechaActual = java.time.LocalDate.now().format(DATE_FORMATTER);
        Paragraph fecha = new Paragraph("Fecha de generación: " + fechaActual)
                .setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);
        document.add(fecha);

        // Línea separadora
        document.add(new Paragraph("\n"));
    }

    /**
     * Agrega la información básica del paquete turístico.
     */
    private void agregarInformacionPaquete(Document document, PaqueteEntity paquete) {
        // Título de sección
        agregarTituloSeccion(document, "Información del Paquete");

        // Tabla con información del paquete
        Table table = new Table(UnitValue.createPercentArray(new float[]{30, 70}))
                .useAllAvailableWidth()
                .setMarginBottom(15);

        agregarFilaTabla(table, "Nombre:", paquete.getNombre() != null ? paquete.getNombre() : "No especificado");
        agregarFilaTabla(table, "Descripción:", paquete.getDescripcion() != null ? paquete.getDescripcion() : "No especificado");
        agregarFilaTabla(table, "Origen:", paquete.getOrigen() != null ? paquete.getOrigen() : "No especificado");
        agregarFilaTabla(table, "Tipo de Paquete:", paquete.getTipoPaquete() != null ? paquete.getTipoPaquete() : "No especificado");
        
        String fechaInicio = paquete.getFechaInicio() != null ? paquete.getFechaInicio().format(DATE_FORMATTER) : "No especificado";
        String fechaFin = paquete.getFechaFin() != null ? paquete.getFechaFin().format(DATE_FORMATTER) : "No especificado";
        agregarFilaTabla(table, "Fechas:", fechaInicio + " - " + fechaFin);
        
        agregarFilaTabla(table, "Adultos:", String.valueOf(paquete.getNumAdultos() != null ? paquete.getNumAdultos() : 0));
        agregarFilaTabla(table, "Niños:", String.valueOf(paquete.getNumNinos() != null ? paquete.getNumNinos() : 0));
        
        String costoTotal = paquete.getCostoTotal() != null ? CURRENCY_FORMATTER.format(paquete.getCostoTotal()) : "No especificado";
        agregarFilaTabla(table, "Costo Total:", costoTotal);

        document.add(table);
    }

    /**
     * Agrega los detalles del destino turístico.
     */
    private void agregarDetallesDestino(Document document, DestinoEntity destino) {
        if (destino == null) {
            return;
        }

        agregarTituloSeccion(document, "Destino");

        Table table = new Table(UnitValue.createPercentArray(new float[]{30, 70}))
                .useAllAvailableWidth()
                .setMarginBottom(15);

        agregarFilaTabla(table, "Nombre:", destino.getNombre() != null ? destino.getNombre() : "No especificado");
        agregarFilaTabla(table, "Descripción:", destino.getDescripcion() != null ? destino.getDescripcion() : "No especificado");
        agregarFilaTabla(table, "Ubicación:", destino.getUbicacion() != null ? destino.getUbicacion() : "No especificado");

        document.add(table);
    }

    /**
     * Agrega la lista de hoteles incluidos en el paquete.
     */
    private void agregarHoteles(Document document, Set<HotelEntity> hoteles) {
        if (hoteles == null || hoteles.isEmpty()) {
            return;
        }

        agregarTituloSeccion(document, "Hoteles Incluidos");

        for (HotelEntity hotel : hoteles) {
            Table table = new Table(UnitValue.createPercentArray(new float[]{30, 70}))
                    .useAllAvailableWidth()
                    .setMarginBottom(10);

            agregarFilaTabla(table, "Nombre:", hotel.getNombre() != null ? hotel.getNombre() : "No especificado");
            
            String tarifaAdulto = hotel.getTarifaAdulto() != null ? CURRENCY_FORMATTER.format(hotel.getTarifaAdulto()) : "No especificado";
            agregarFilaTabla(table, "Tarifa Adulto:", tarifaAdulto);
            
            String tarifaNino = hotel.getTarifaNino() != null ? CURRENCY_FORMATTER.format(hotel.getTarifaNino()) : "No especificado";
            agregarFilaTabla(table, "Tarifa Niño:", tarifaNino);

            document.add(table);
        }
    }

    /**
     * Agrega la lista de transportes incluidos en el paquete.
     */
    private void agregarTransportes(Document document, Set<TransporteEntity> transportes) {
        if (transportes == null || transportes.isEmpty()) {
            return;
        }

        agregarTituloSeccion(document, "Transportes Incluidos");

        for (TransporteEntity transporte : transportes) {
            Table table = new Table(UnitValue.createPercentArray(new float[]{30, 70}))
                    .useAllAvailableWidth()
                    .setMarginBottom(10);

            agregarFilaTabla(table, "Tipo:", transporte.getTipo() != null ? transporte.getTipo() : "No especificado");
            agregarFilaTabla(table, "Empresa:", transporte.getEmpresa() != null ? transporte.getEmpresa() : "No especificado");
            
            String precio = transporte.getPrecio() != null ? CURRENCY_FORMATTER.format(transporte.getPrecio()) : "No especificado";
            agregarFilaTabla(table, "Precio:", precio);

            document.add(table);
        }
    }

    /**
     * Agrega la lista de actividades incluidas en el paquete.
     */
    private void agregarActividades(Document document, Set<ActividadEntity> actividades) {
        if (actividades == null || actividades.isEmpty()) {
            return;
        }

        agregarTituloSeccion(document, "Actividades Incluidas");

        for (ActividadEntity actividad : actividades) {
            Table table = new Table(UnitValue.createPercentArray(new float[]{30, 70}))
                    .useAllAvailableWidth()
                    .setMarginBottom(10);

            agregarFilaTabla(table, "Nombre:", actividad.getNombre() != null ? actividad.getNombre() : "No especificado");
            agregarFilaTabla(table, "Descripción:", actividad.getDescripcion() != null ? actividad.getDescripcion() : "No especificado");
            
            String precio = actividad.getPrecio() != null ? CURRENCY_FORMATTER.format(actividad.getPrecio()) : "No especificado";
            agregarFilaTabla(table, "Precio:", precio);

            document.add(table);
        }
    }

    /**
     * Agrega el pie de página con numeración de páginas.
     */
    private void agregarPiePagina(Document document, PdfDocument pdfDoc) {
        int numberOfPages = pdfDoc.getNumberOfPages();
        
        for (int i = 1; i <= numberOfPages; i++) {
            document.showTextAligned(
                    new Paragraph("Página " + i + " de " + numberOfPages)
                            .setFontSize(10),
                    PageSize.A4.getWidth() / 2,
                    30,
                    i,
                    TextAlignment.CENTER,
                    com.itextpdf.layout.properties.VerticalAlignment.BOTTOM,
                    0
            );
        }
    }

    /**
     * Método auxiliar para agregar títulos de sección.
     */
    private void agregarTituloSeccion(Document document, String titulo) {
        Paragraph tituloSeccion = new Paragraph(titulo)
                .setFontSize(16)
                .setBold()
                .setFontColor(COLOR_AZUL_TITULO)
                .setMarginTop(15)
                .setMarginBottom(10);
        document.add(tituloSeccion);
    }

    /**
     * Método auxiliar para agregar filas a una tabla.
     */
    private void agregarFilaTabla(Table table, String etiqueta, String valor) {
        Cell celdaEtiqueta = new Cell()
                .add(new Paragraph(etiqueta).setBold())
                .setBackgroundColor(new DeviceRgb(240, 240, 240));
        
        Cell celdaValor = new Cell()
                .add(new Paragraph(valor));
        
        table.addCell(celdaEtiqueta);
        table.addCell(celdaValor);
    }
}
