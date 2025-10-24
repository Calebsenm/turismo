package com.app.turismo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
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

    @OneToMany(mappedBy = "destino")
    private List<PaqueteEntity> paquetes;

    @OneToMany(mappedBy = "destino")
    private List<HotelEntity> hoteles;

    @OneToMany(mappedBy = "destino")
    private List<ActividadEntity> actividades;
}