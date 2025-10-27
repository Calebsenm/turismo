package com.app.turismo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "paquete")
public class PaqueteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paquete_id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private UsuarioEntity usuario;

    @ManyToOne
    @JoinColumn(name = "destino_id", referencedColumnName = "destino_id")
    @com.fasterxml.jackson.annotation.JsonBackReference // Evita recursión infinita
    private DestinoEntity destino;

    // 🔹 Nueva columna: ciudad de origen
    @Column(name = "origen", nullable = false)
    private String origen;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    @Column(name = "costo_total", nullable = false)
    private Double costoTotal;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "descripcion", nullable = false)
    private String descripcion;

    // 🔹 Campos adicionales para personas
    @Column(name = "num_adultos", nullable = false)
    private Integer numAdultos;

    @Column(name = "num_ninos", nullable = false)
    private Integer numNinos;

    @Column(name = "tipo_paquete")
    private String tipoPaquete; // "familiar", "romántico", "grupal", etc.

    @ManyToMany
    @JoinTable(name = "paquete_hotel", joinColumns = @JoinColumn(name = "paquete_id"), inverseJoinColumns = @JoinColumn(name = "hotel_id"))
    private java.util.Set<HotelEntity> hoteles = new java.util.HashSet<>();

    @ManyToMany
    @JoinTable(name = "paquete_transporte", joinColumns = @JoinColumn(name = "paquete_id"), inverseJoinColumns = @JoinColumn(name = "transporte_id"))
    private java.util.Set<TransporteEntity> transportes = new java.util.HashSet<>();

    @ManyToMany
    @JoinTable(name = "paquete_actividad", joinColumns = @JoinColumn(name = "paquete_id"), inverseJoinColumns = @JoinColumn(name = "actividad_id"))
    private java.util.Set<ActividadEntity> actividades = new java.util.HashSet<>();

    public java.util.Set<HotelEntity> getHoteles() {
        return hoteles;
    }

    public void setHoteles(java.util.Set<HotelEntity> hoteles) {
        this.hoteles = hoteles;
    }

    public java.util.Set<TransporteEntity> getTransportes() {
        return transportes;
    }

    public void setTransportes(java.util.Set<TransporteEntity> transportes) {
        this.transportes = transportes;
    }

    public java.util.Set<ActividadEntity> getActividades() {
        return actividades;
    }

    public void setActividades(java.util.Set<ActividadEntity> actividades) {
        this.actividades = actividades;
    }
}
