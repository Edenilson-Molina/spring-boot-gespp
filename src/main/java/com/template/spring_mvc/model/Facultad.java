package com.template.spring_mvc.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "facultades")
@Getter
@Setter
@ToString(exclude = {"carreras"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Facultad {
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

    @OneToMany(mappedBy = "facultad", cascade = CascadeType.ALL, orphanRemoval = false, fetch = FetchType.LAZY)
    private Set<Carrera> carreras = new HashSet<>();
}
