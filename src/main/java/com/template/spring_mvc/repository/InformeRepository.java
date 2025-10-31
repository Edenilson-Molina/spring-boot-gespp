package com.template.spring_mvc.repository;

import com.template.spring_mvc.model.Informe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InformeRepository extends JpaRepository<Informe, Long> {
    List<Informe> findByExpedienteIdOrderByMesAsc(Long expedienteId);
    Optional<Informe> findByExpedienteIdAndMes(Long expedienteId, String mes);
    boolean existsByExpedienteIdAndMes(Long expedienteId, String mes);
}
