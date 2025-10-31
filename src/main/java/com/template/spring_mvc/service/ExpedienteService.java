package com.template.spring_mvc.service;

import com.template.spring_mvc.model.Expediente;
import com.template.spring_mvc.repository.ExpedienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ExpedienteService {

    private final ExpedienteRepository expedienteRepository;

    public ExpedienteService(ExpedienteRepository expedienteRepository) {
        this.expedienteRepository = expedienteRepository;
    }

    @Transactional(readOnly = true)
    public List<Expediente> findAll() { return expedienteRepository.findAll(); }

    @Transactional(readOnly = true)
    public Optional<Expediente> findById(Long id) { return expedienteRepository.findById(id); }

    @Transactional
    public Expediente save(Expediente expediente) { return expedienteRepository.save(expediente); }

    @Transactional
    public void deleteById(Long id) { expedienteRepository.deleteById(id); }

    @Transactional(readOnly = true)
    public List<Expediente> findByEstudianteId(Long estudianteId) {
        return expedienteRepository.findByEstudianteId(estudianteId);
    }

    @Transactional(readOnly = true)
    public boolean existsByEstudianteAndCarrera(Long estudianteId, Long carreraId) {
        return expedienteRepository.existsByEstudianteIdAndCarreraId(estudianteId, carreraId);
    }
}
