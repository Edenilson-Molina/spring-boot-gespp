package com.template.spring_mvc.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "ofertas")
@Getter
@Setter
@ToString(exclude = {"supervisor"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Oferta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "El nombre es requerido")
    private String nombre;

    @Column(length = 1000)
    @Size(max = 1000, message = "La descripción no debe exceder 1000 caracteres")
    private String descripcion;

    @Column(nullable = false)
    @NotNull
    @Min(value = 0, message = "Ocupados no puede ser negativo")
    private Integer ocupados = 0;

    @Column(nullable = false)
    @NotNull(message = "Las vacantes son requeridas")
    @Min(value = 0, message = "Las vacantes no pueden ser negativas")
    private Integer vacantes;

    @Column(nullable = false)
    @NotNull(message = "El estado activo es requerido")
    private Boolean activo = true;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "supervisor_id", nullable = false)
    @NotNull(message = "El supervisor es requerido")
    private Supervisor supervisor;

    @PrePersist
    public void prePersist() {
        if (ocupados == null) {
            ocupados = 0;
        }
    }
}
