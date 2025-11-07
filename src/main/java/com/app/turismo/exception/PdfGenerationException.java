package com.app.turismo.exception;

/**
 * Excepción personalizada para errores durante la generación de documentos PDF.
 * Se lanza cuando ocurre un error técnico al crear o procesar un documento PDF.
 */
public class PdfGenerationException extends ApiException {
    
    /**
     * Constructor con mensaje de error.
     *
     * @param message Descripción del error ocurrido durante la generación del PDF
     */
    public PdfGenerationException(String message) {
        super(message);
    }
    
    /**
     * Constructor con mensaje de error y causa raíz.
     *
     * @param message Descripción del error ocurrido durante la generación del PDF
     * @param cause Excepción original que causó el error
     */
    public PdfGenerationException(String message, Throwable cause) {
        super(message);
        initCause(cause);
    }
}
