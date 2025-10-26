package com.app.turismo.dto;

public class ActividadDTO {
    private Long actividad_id;
    private String nombre;
    private String descripcion;
    private Double precio;

    // Getters y setters
    public Long getActividad_id() {
        return actividad_id;
    }

    public void setActividad_id(Long actividad_id) {
        this.actividad_id = actividad_id;
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

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }
}
