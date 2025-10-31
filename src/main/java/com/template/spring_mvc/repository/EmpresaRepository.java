package com.template.spring_mvc.repository;

import com.template.spring_mvc.model.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmpresaRepository extends JpaRepository<Empresa, Long> {
    Optional<Empresa> findByNombre(String nombre);
    boolean existsByNombre(String nombre);
}
