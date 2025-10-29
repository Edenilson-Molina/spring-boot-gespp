package com.template.spring_mvc.repository;

import com.template.spring_mvc.model.Estudiante;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {
    boolean existsByCarnet(String carnet);
    boolean existsByDui(String dui);
}
