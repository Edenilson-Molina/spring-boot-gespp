package com.template.spring_mvc.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
public class RegistroEstudianteDto {
    // Datos de Usuario
    @NotBlank
    @Size(max = 100)
    private String nombres;

    @NotBlank
    @Size(max = 100)
    private String apellidos;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 6, max = 100)
    private String password;

    @NotBlank
    @Size(min = 6, max = 100)
    private String confirmPassword;

    // Datos de Estudiante
    @NotBlank
    @Size(max = 30)
    private String carnet;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaNacimiento;

    @Size(max = 9)
    private String dui; // requerido si >=18, validado en controlador
}
