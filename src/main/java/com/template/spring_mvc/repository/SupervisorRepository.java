package com.template.spring_mvc.repository;

import com.template.spring_mvc.model.Supervisor;
import com.template.spring_mvc.model.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SupervisorRepository extends JpaRepository<Supervisor, Long> {
    List<Supervisor> findByEmpresa(Empresa empresa);
}
