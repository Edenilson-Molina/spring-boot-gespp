package com.template.spring_mvc.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Entity
@Table(name = "expedientes")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"estudiante", "carrera", "oferta"})
public class Expediente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @NotNull
    private LocalDate fechaInicio;

    @NotNull
    private LocalDate fechaFin;

    @Column(nullable = false, length = 20)
    private String estado = "Registrado"; // Registrado, En curso, Suspendido, Finalizado, Cancelado

    @Column(nullable = false)
    private Boolean activo = true;

    @ManyToOne(optional = false)
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Estudiante estudiante;

    @ManyToOne(optional = false)
    @JoinColumn(name = "carrera_id", nullable = false)
    private Carrera carrera;

    @ManyToOne(optional = false)
    @JoinColumn(name = "oferta_id", nullable = false)
    private Oferta oferta;
}
