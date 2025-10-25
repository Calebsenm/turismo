package com.app.turismo.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

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

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private Double tarifaAdulto;

    @Column(nullable = false)
    private Double tarifaNino;

    @ManyToOne
    @JoinColumn(name = "destino_id", referencedColumnName = "destino_id")
    @JsonBackReference
    @JsonIgnoreProperties({ "hoteles", "paquetes" }) // evita recursión
    private DestinoEntity destino;
}
