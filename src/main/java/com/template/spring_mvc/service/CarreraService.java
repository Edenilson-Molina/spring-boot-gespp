package com.template.spring_mvc.service;

import com.template.spring_mvc.model.Carrera;
import com.template.spring_mvc.repository.CarreraRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CarreraService {
    private final CarreraRepository carreraRepository;

    public CarreraService(CarreraRepository carreraRepository) {
        this.carreraRepository = carreraRepository;
    }

    @Transactional(readOnly = true)
    public List<Carrera> findAll() {
        return carreraRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Carrera> findById(Long id) {
        return carreraRepository.findById(id);
    }

    @Transactional
    public Carrera save(Carrera carrera) {
        return carreraRepository.save(carrera);
    }

    @Transactional
    public void deleteById(Long id) {
        carreraRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public boolean existsByNombreAndFacultad(String nombre, Long facultadId, Long excludeId) {
        return carreraRepository.findByNombreAndFacultadId(nombre, facultadId)
                .filter(existing -> excludeId == null || !existing.getId().equals(excludeId))
                .isPresent();
    }
}
