package com.template.spring_mvc.service;

import com.template.spring_mvc.model.Estudiante;
import com.template.spring_mvc.repository.EstudianteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EstudianteService {

    private final EstudianteRepository estudianteRepository;

    public EstudianteService(EstudianteRepository estudianteRepository) {
        this.estudianteRepository = estudianteRepository;
    }

    @Transactional(readOnly = true)
    public List<Estudiante> findAll() {
        return estudianteRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Estudiante> findById(Long id) {
        return estudianteRepository.findById(id);
    }

    @Transactional
    public Estudiante save(Estudiante estudiante) {
        return estudianteRepository.save(estudiante);
    }

    @Transactional
    public void deleteById(Long id) {
        estudianteRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public boolean existsByCarnet(String carnet) {
        return estudianteRepository.existsByCarnet(carnet);
    }

    @Transactional(readOnly = true)
    public boolean existsByDui(String dui) {
        return estudianteRepository.existsByDui(dui);
    }
}
