package com.app.turismo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "hotel")
public class HotelEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long hotel_id;

    @ManyToOne
    @JoinColumn(name = "destino_id", referencedColumnName = "destino_id")
    private DestinoEntity destino;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "direccion", nullable = false)
    private String direccion;

    // 💰 Tarifa base para adulto
    @Column(name = "tarifa_adulto", nullable = false)
    private Double tarifaAdulto;

    // 💰 Tarifa especial para niño (<8 años)
    @Column(name = "tarifa_nino", nullable = false)
    private Double tarifaNino;

    @Column(name = "acomodacion", nullable = false)
    private String acomodacion;

    @Column(name = "disponibilidad", nullable = false)
    private Boolean disponibilidad = true;
}
