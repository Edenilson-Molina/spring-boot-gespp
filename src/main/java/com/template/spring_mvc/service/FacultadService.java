package com.template.spring_mvc.service;

import com.template.spring_mvc.model.Facultad;
import com.template.spring_mvc.repository.FacultadRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FacultadService {
    private final FacultadRepository facultadRepository;

    public FacultadService(FacultadRepository facultadRepository) {
        this.facultadRepository = facultadRepository;
    }

    public List<Facultad> findAll() {
        return facultadRepository.findAll();
    }
}
