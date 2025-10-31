package com.template.spring_mvc.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "supervisores")
@Getter
@Setter
@ToString(exclude = {"empresa", "ofertas"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Supervisor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "El nombre es requerido")
    private String nombre;

    @Column(nullable = false)
    @NotBlank(message = "El apellido es requerido")
    private String apellido;

    @Column(length = 500)
    @Size(max = 500, message = "La descripción no debe exceder 500 caracteres")
    private String descripcion;

    @Column(nullable = false)
    @NotNull(message = "El estado activo es requerido")
    private Boolean activo = true;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "empresa_id", nullable = false)
    @NotNull(message = "La empresa es requerida")
    private Empresa empresa;

    @OneToMany(mappedBy = "supervisor", cascade = CascadeType.ALL, orphanRemoval = false, fetch = FetchType.LAZY)
    private Set<Oferta> ofertas = new HashSet<>();
}
