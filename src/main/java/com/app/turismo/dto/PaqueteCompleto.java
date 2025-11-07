package com.app.turismo.dto;

import com.app.turismo.model.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

/**
 * DTO que encapsula todos los datos necesarios para generar un PDF de un paquete turístico.
 * Incluye información del paquete, usuario, destino y todos los servicios asociados.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaqueteCompleto {
    
    private PaqueteEntity paquete;
    private UsuarioEntity usuario;
    private DestinoEntity destino;
    private Set<HotelEntity> hoteles;
    private Set<TransporteEntity> transportes;
    private Set<ActividadEntity> actividades;
}
