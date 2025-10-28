package com.template.spring_mvc.service;

import com.template.spring_mvc.model.Carrera;
import com.template.spring_mvc.repository.CarreraRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CarreraService {
    private final CarreraRepository carreraRepository;

    public CarreraService(CarreraRepository carreraRepository) {
        this.carreraRepository = carreraRepository;
    }

    public List<Carrera> findAll() {
        return carreraRepository.findAll();
    }

    public Optional<Carrera> findById(Long id) {
        return carreraRepository.findById(id);
    }

    public Carrera save(Carrera carrera) {
        return carreraRepository.save(carrera);
    }

    public void deleteById(Long id) {
        carreraRepository.deleteById(id);
    }
}
