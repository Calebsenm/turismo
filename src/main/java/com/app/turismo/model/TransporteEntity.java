package com.app.turismo.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "transporte")
public class TransporteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transporte_id;

    @ManyToOne
    @JoinColumn(name = "destino_id", referencedColumnName = "destino_id")
    @JsonBackReference // 🔹 Evita bucles al devolver el JSON
    private DestinoEntity destino;

    @Column(name = "tipo", nullable = false)
    private String tipo;

    @Column(name = "empresa", nullable = false)
    private String empresa;

    @Column(name = "precio", nullable = false)
    private Double precio;
}
