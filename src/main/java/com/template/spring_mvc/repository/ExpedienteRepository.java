package com.template.spring_mvc.repository;

import com.template.spring_mvc.model.Expediente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExpedienteRepository extends JpaRepository<Expediente, Long> {
    List<Expediente> findByEstudianteId(Long estudianteId);
    boolean existsByEstudianteIdAndCarreraId(Long estudianteId, Long carreraId);
}
