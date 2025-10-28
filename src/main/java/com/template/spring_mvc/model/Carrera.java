package com.template.spring_mvc.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "carreras")
@Getter
@Setter
@ToString(exclude = {"facultad"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Carrera {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "El nombre es requerido")
    private String nombre;

    @Column(nullable = false)
    @NotNull(message = "El estado activo es requerido")
    private Boolean activo = true;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "facultad_id", nullable = false)
    @NotNull(message = "La facultad es requerida")
    private Facultad facultad;
}
