package com.template.spring_mvc.repository;

import com.template.spring_mvc.model.Carrera;
import com.template.spring_mvc.model.Facultad;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CarreraRepository extends JpaRepository<Carrera, Long> {
    List<Carrera> findByFacultad(Facultad facultad);
    Optional<Carrera> findByNombre(String nombre);
}
