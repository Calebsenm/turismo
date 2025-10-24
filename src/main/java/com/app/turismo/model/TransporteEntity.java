package com.app.turismo.model;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private DestinoEntity destino;

    @Column(name = "tipo", nullable = false)
    private String tipo;

    @Column(name = "empresa", nullable = false)
    private String empresa;
    
    @Column(name = "precio", nullable = false)
    private Double precio;

}
