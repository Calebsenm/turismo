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
    private DestinoEntity destino;

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
}
