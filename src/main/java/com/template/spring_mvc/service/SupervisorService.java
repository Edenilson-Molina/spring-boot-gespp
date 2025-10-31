package com.template.spring_mvc.service;

import com.template.spring_mvc.model.Supervisor;
import com.template.spring_mvc.repository.SupervisorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SupervisorService {
    private final SupervisorRepository supervisorRepository;

    public SupervisorService(SupervisorRepository supervisorRepository) {
        this.supervisorRepository = supervisorRepository;
    }

    @Transactional(readOnly = true)
    public List<Supervisor> findAll() {
        return supervisorRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Supervisor> findById(Long id) {
        return supervisorRepository.findById(id);
    }

    @Transactional
    public Supervisor save(Supervisor supervisor) {
        return supervisorRepository.save(supervisor);
    }

    @Transactional
    public void deleteById(Long id) {
        supervisorRepository.deleteById(id);
    }
}
