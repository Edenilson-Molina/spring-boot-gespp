package com.template.spring_mvc.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "informes",
       uniqueConstraints = {@UniqueConstraint(columnNames = {"expediente_id", "mes"})})
@Getter
@Setter
@ToString(exclude = {"expediente"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Informe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "expediente_id", nullable = false)
    @NotNull
    private Expediente expediente;

    // Mes del informe en formato "YYYY-MM"
    @Column(nullable = false, length = 7)
    @NotBlank
    private String mes;

    // Ruta al archivo PDF almacenado en disco, servida por /uploads/**
    @Column(nullable = false, length = 500)
    @NotBlank
    private String archivoPath;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstadoInforme estado = EstadoInforme.PENDIENTE_VERIFICACION;

    @Size(max = 500)
    private String comentario;

    public enum EstadoInforme {
        PENDIENTE_VERIFICACION,
        RECHAZADO,
        OBSERVADO,
        APROBADO
    }
}
