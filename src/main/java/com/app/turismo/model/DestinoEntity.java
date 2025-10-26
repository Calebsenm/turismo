package com.app.turismo.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Entity
@Table(name = "destino")
public class DestinoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long destino_id;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "descripcion", nullable = false)
    private String descripcion;

    @Column(name = "ubicacion", nullable = false)
    private String ubicacion;

    @OneToMany(mappedBy = "destino", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference // 🔹 Muy importante
    private List<ActividadEntity> actividades;

    @OneToMany(mappedBy = "destino", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<HotelEntity> hoteles;

    @OneToMany(mappedBy = "destino", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<PaqueteEntity> paquetes;

    @OneToMany(mappedBy = "destino", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<TransporteEntity> transportes;

    public DestinoEntity() {
        this.actividades = new java.util.ArrayList<>();
        this.hoteles = new java.util.ArrayList<>();
        this.paquetes = new java.util.ArrayList<>();
        this.transportes = new java.util.ArrayList<>();
    }
}
