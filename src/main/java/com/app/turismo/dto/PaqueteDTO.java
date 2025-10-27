package com.app.turismo.dto;

import java.time.LocalDate;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PaqueteDTO {
    @JsonProperty("paquete_id")
    public Long paqueteId;
    @JsonProperty("usuario_id")
    public Long usuarioId;
    @JsonProperty("destino_id")
    public Long destinoId;
    public String origen;
    public LocalDate fechaInicio;
    public LocalDate fechaFin;
    public Double costoTotal;
    public String nombre;
    public String descripcion;
    public Integer numAdultos;
    public Integer numNinos;
    public String tipoPaquete;
    public List<com.app.turismo.model.HotelEntity> hoteles;
    public List<com.app.turismo.model.TransporteEntity> transportes;
    public List<com.app.turismo.model.ActividadEntity> actividades;
}
