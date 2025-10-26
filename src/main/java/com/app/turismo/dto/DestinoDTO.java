package com.app.turismo.dto;

import java.util.List;

public class DestinoDTO {
    private Long destino_id;
    private String nombre;
    private String descripcion;
    private String ubicacion;
    private List<ActividadDTO> actividades;
    private List<HotelDTO> hoteles;
    private List<TransporteDTO> transportes;
    // No incluir paquetes ni transportes para evitar recursión

    // Getters y setters
    public Long getDestino_id() {
        return destino_id;
    }

    public void setDestino_id(Long destino_id) {
        this.destino_id = destino_id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public List<ActividadDTO> getActividades() {
        return actividades;
    }

    public void setActividades(List<ActividadDTO> actividades) {
        this.actividades = actividades;
    }

    public List<HotelDTO> getHoteles() {
        return hoteles;
    }

    public void setHoteles(List<HotelDTO> hoteles) {
        this.hoteles = hoteles;
    }

    public List<TransporteDTO> getTransportes() {
        return transportes;
    }

    public void setTransportes(List<TransporteDTO> transportes) {
        this.transportes = transportes;
    }
}
