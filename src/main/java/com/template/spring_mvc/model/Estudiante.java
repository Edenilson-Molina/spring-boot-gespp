package com.template.spring_mvc.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;

@Entity
@Table(name = "estudiantes")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"user"})
public class Estudiante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @NotBlank
    @Column(unique = true, nullable = false, length = 30)
    private String carnet;

    @NotNull
    private LocalDate fechaNacimiento;

    // DUI requerido solo si es >= 18; validado en el controlador
    @Size(max = 9)
    @Column(unique = true)
    private String dui;

    @Column(nullable = false)
    private Boolean activo = true;

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
}
