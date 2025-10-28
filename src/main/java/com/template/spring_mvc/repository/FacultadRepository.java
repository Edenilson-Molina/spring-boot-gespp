package com.template.spring_mvc.repository;

import com.template.spring_mvc.model.Facultad;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FacultadRepository extends JpaRepository<Facultad, Long> {
    Optional<Facultad> findByNombre(String nombre);
}
