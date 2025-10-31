package com.template.spring_mvc.repository;

import com.template.spring_mvc.model.Oferta;
import com.template.spring_mvc.model.Supervisor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OfertaRepository extends JpaRepository<Oferta, Long> {
    List<Oferta> findBySupervisor(Supervisor supervisor);
}
