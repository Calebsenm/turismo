package com.app.turismo.dto;

public class TransporteDTO {
    private Long transporte_id;
    private String empresa;
    private String tipo;
    private Double precio;
    private Long destinoId; // Campo para recibir el ID del destino desde el frontend

    // Getters y setters
    public Long getTransporte_id() {
        return transporte_id;
    }

    public void setTransporte_id(Long transporte_id) {
        this.transporte_id = transporte_id;
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public Long getDestinoId() {
        return destinoId;
    }

    public void setDestinoId(Long destinoId) {
        this.destinoId = destinoId;
    }
}