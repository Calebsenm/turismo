package com.app.turismo.pdf;

import com.app.turismo.dto.PaqueteCompleto;
import com.app.turismo.exception.PdfGenerationException;
import com.app.turismo.model.*;
import com.app.turismo.repository.PaqueteRepository;
import com.app.turismo.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Servicio responsable de la generación de PDFs de paquetes turísticos.
 * Maneja la lógica de negocio para validar acceso, obtener datos y generar el documento.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PdfService {

    private final PaqueteRepository paqueteRepository;
    private final UsuarioRepository usuarioRepository;
    private final PdfGenerator pdfGenerator;

    /**
     * Genera un PDF para un paquete turístico específico.
     * Valida que el usuario tenga acceso al paquete antes de generar el documento.
     *
     * @param paqueteId ID del paquete turístico
     * @param userEmail Email del usuario que solicita el PDF
     * @return Array de bytes con el contenido del PDF generado
     * @throws RuntimeException si el paquete no existe o el usuario no tiene acceso
     * @throws PdfGenerationException si ocurre un error durante la generación del PDF
     */
    @Transactional(readOnly = true)
    public byte[] generarPdfPaquete(Long paqueteId, String userEmail) {
        log.info("Iniciando generación de PDF para paquete ID: {} solicitado por usuario: {}", paqueteId, userEmail);

        try {
            // Validar acceso del usuario al paquete
            validarAccesoPaquete(paqueteId, userEmail);

            // Obtener todos los datos necesarios para el PDF
            PaqueteCompleto paqueteCompleto = obtenerDatosCompletos(paqueteId);

            // Generar el documento PDF
            byte[] pdfBytes = pdfGenerator.crearDocumentoPdf(paqueteCompleto);

            log.info("PDF generado exitosamente para paquete ID: {} (tamaño: {} bytes)", paqueteId, pdfBytes.length);
            return pdfBytes;
            
        } catch (PdfGenerationException e) {
            log.error("Error en la generación del PDF para paquete ID {}: {}", paqueteId, e.getMessage());
            throw e;
        } catch (RuntimeException e) {
            log.error("Error al procesar solicitud de PDF para paquete ID {}: {}", paqueteId, e.getMessage());
            throw e;
        }
    }

    /**
     * Valida que el usuario tenga permisos para acceder al paquete.
     * Un usuario puede acceder si es el propietario del paquete o si es administrador.
     *
     * @param paqueteId ID del paquete
     * @param userEmail Email del usuario
     * @throws RuntimeException si el paquete no existe o el usuario no tiene acceso
     */
    public void validarAccesoPaquete(Long paqueteId, String userEmail) {
        // Buscar el paquete
        PaqueteEntity paquete = paqueteRepository.findById(paqueteId)
                .orElseThrow(() -> {
                    log.error("Paquete no encontrado con ID: {}", paqueteId);
                    return new RuntimeException("Paquete no encontrado");
                });

        // Buscar el usuario
        UsuarioEntity usuario = usuarioRepository.findByEmail(userEmail)
                .orElseThrow(() -> {
                    log.error("Usuario no encontrado con email: {}", userEmail);
                    return new RuntimeException("Usuario no encontrado");
                });

        // Verificar si el usuario es el propietario del paquete o es administrador
        boolean esPropietario = paquete.getUsuario() != null 
                && paquete.getUsuario().getUser_id().equals(usuario.getUser_id());
        boolean esAdmin = "admin".equalsIgnoreCase(usuario.getUserType());

        if (!esPropietario && !esAdmin) {
            log.error("Usuario {} no tiene permisos para acceder al paquete {}", userEmail, paqueteId);
            throw new RuntimeException("No tiene permisos para acceder a este paquete");
        }

        log.debug("Acceso validado para usuario {} al paquete {}", userEmail, paqueteId);
    }

    /**
     * Obtiene todos los datos necesarios para generar el PDF del paquete.
     * Incluye información del paquete, usuario, destino y todos los servicios asociados.
     *
     * @param paqueteId ID del paquete
     * @return PaqueteCompleto con todos los datos necesarios
     * @throws RuntimeException si el paquete no existe
     */
    @Transactional(readOnly = true)
    public PaqueteCompleto obtenerDatosCompletos(Long paqueteId) {
        log.debug("Obteniendo datos completos para paquete ID: {}", paqueteId);

        // Obtener el paquete con todas sus relaciones cargadas
        PaqueteEntity paquete = paqueteRepository.findById(paqueteId)
                .orElseThrow(() -> {
                    log.error("Paquete no encontrado con ID: {}", paqueteId);
                    return new RuntimeException("Paquete no encontrado");
                });

        // Inicializar las colecciones lazy para evitar LazyInitializationException
        org.hibernate.Hibernate.initialize(paquete.getHoteles());
        org.hibernate.Hibernate.initialize(paquete.getTransportes());
        org.hibernate.Hibernate.initialize(paquete.getActividades());

        // Crear el DTO con todos los datos
        PaqueteCompleto paqueteCompleto = new PaqueteCompleto();
        paqueteCompleto.setPaquete(paquete);
        paqueteCompleto.setUsuario(paquete.getUsuario());
        paqueteCompleto.setDestino(paquete.getDestino());
        paqueteCompleto.setHoteles(paquete.getHoteles());
        paqueteCompleto.setTransportes(paquete.getTransportes());
        paqueteCompleto.setActividades(paquete.getActividades());

        log.debug("Datos completos obtenidos para paquete ID: {}", paqueteId);
        return paqueteCompleto;
    }
}
