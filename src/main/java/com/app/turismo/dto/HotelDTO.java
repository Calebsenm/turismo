package com.app.turismo.dto;

public class HotelDTO {
    private Long hotel_id;
    private String nombre;
    private Double tarifaAdulto;
    private Double tarifaNino;

    // Getters y setters
    public Long getHotel_id() {
        return hotel_id;
    }

    public void setHotel_id(Long hotel_id) {
        this.hotel_id = hotel_id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Double getTarifaAdulto() {
        return tarifaAdulto;
    }

    public void setTarifaAdulto(Double tarifaAdulto) {
        this.tarifaAdulto = tarifaAdulto;
    }

    public Double getTarifaNino() {
        return tarifaNino;
    }

    public void setTarifaNino(Double tarifaNino) {
        this.tarifaNino = tarifaNino;
    }
}
