package com.app.turismo.controller;

import com.app.turismo.config.Jwt.JwtService;
import com.app.turismo.exception.PdfGenerationException;
import com.app.turismo.pdf.PdfService;
import com.app.turismo.service.PaqueteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para la generación de PDFs de paquetes turísticos.
 * Proporciona endpoints para descargar documentos PDF con información completa de paquetes.
 */
@RestController
@RequestMapping("/api/paquetes")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Slf4j
public class PdfController {

    private final PdfService pdfService;
    private final PaqueteService paqueteService;
    private final JwtService jwtService;

    /**
     * Genera y descarga un PDF con la información completa de un paquete turístico.
     * Requiere autenticación JWT y valida que el usuario tenga permisos para acceder al paquete.
     *
     * @param id ID del paquete turístico
     * @param authHeader Token JWT en formato "Bearer {token}"
     * @return ResponseEntity con el PDF como array de bytes o error apropiado
     */
    @GetMapping("/{id}/pdf")
    public ResponseEntity<?> generarPdfPaquete(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        log.info("Solicitud de generación de PDF recibida para paquete ID: {}", id);
        
        try {
            // Validar que el header de autorización esté presente
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.warn("Intento de acceso sin token JWT para paquete ID: {}", id);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Token de autenticación requerido");
            }

            // Extraer email del token JWT
            String token = authHeader.substring(7); // Remover "Bearer "
            String userEmail = null;
            try {
                userEmail = jwtService.extractUsername(token);
            } catch (Exception e) {
                log.warn("Error al extraer email del token para paquete ID {}: {}", id, e.getMessage());
            }
            
            if (userEmail == null || userEmail.isEmpty()) {
                log.warn("Token JWT inválido para paquete ID: {}", id);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Token de autenticación inválido");
            }

            log.debug("Usuario autenticado: {} solicitando PDF para paquete ID: {}", userEmail, id);

            // Generar el PDF
            byte[] pdfBytes = pdfService.generarPdfPaquete(id, userEmail);

            // Configurar headers de respuesta para descarga de PDF
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "paquete_" + id + ".pdf");
            headers.setContentLength(pdfBytes.length);

            log.info("PDF descargado exitosamente para paquete ID: {} por usuario: {}", id, userEmail);
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

        } catch (PdfGenerationException e) {
            // Manejar errores específicos de generación de PDF
            log.error("Error en la generación del PDF para paquete ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno al generar el PDF");
                    
        } catch (RuntimeException e) {
            // Manejar errores específicos del servicio
            String errorMessage = e.getMessage();
            log.error("Error al procesar solicitud de PDF para paquete ID {}: {}", id, errorMessage);

            if (errorMessage.contains("no encontrado")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Paquete no encontrado");
            } else if (errorMessage.contains("permisos")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("No tiene permisos para acceder a este paquete");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error interno al generar el PDF");
            }
        } catch (Exception e) {
            // Manejar errores inesperados
            log.error("Error inesperado al generar PDF para paquete ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno al generar el PDF");
        }
    }
}
