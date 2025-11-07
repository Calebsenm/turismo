package com.app.turismo.dto;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;

public class PaqueteDTO {

    @JsonProperty("paquete_id")
    public Long paqueteId;

    @JsonProperty("usuario_id")
    @NotNull(message = "El usuario_id no puede ser nulo.")
    public Long usuarioId;

    @JsonProperty("destino_id")
    @NotNull(message = "El destino_id no puede ser nulo.")
    public Long destinoId;

    @NotBlank(message = "El origen no puede estar vacío.")
    public String origen;

    @NotNull(message = "La fecha_inicio es obligatoria.")
    @JsonProperty("fecha_inicio")
    public LocalDate fechaInicio;

    @NotNull(message = "La fecha_fin es obligatoria.")
    @JsonProperty("fecha_fin")
    public LocalDate fechaFin;

    @NotNull(message = "El costo_total es obligatorio.")
    @PositiveOrZero(message = "El costo_total no puede ser negativo.")
    @JsonProperty("costo_total")
    public Double costoTotal;

    @NotBlank(message = "El nombre no puede estar vacío.")
    public String nombre;

    @NotBlank(message = "La descripción no puede estar vacía.")
    public String descripcion;

    @NotNull(message = "El número de adultos es obligatorio.")
    @Min(value = 1, message = "Debe haber al menos un adulto.")
    @JsonProperty("num_adultos")
    public Integer numAdultos;

    @NotNull(message = "El número de niños es obligatorio.")
    @Min(value = 0, message = "El número de niños no puede ser negativo.")
    @JsonProperty("num_ninos")
    public Integer numNinos;

    @NotBlank(message = "El tipo_paquete es obligatorio.")
    @JsonProperty("tipo_paquete")
    public String tipoPaquete;

    @JsonProperty("hoteles")
    public List<com.app.turismo.model.HotelEntity> hoteles;

    @JsonProperty("transportes")
    public List<com.app.turismo.model.TransporteEntity> transportes;

    @JsonProperty("actividades")
    public List<com.app.turismo.model.ActividadEntity> actividades;
}
