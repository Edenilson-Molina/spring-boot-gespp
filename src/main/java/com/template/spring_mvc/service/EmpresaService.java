package com.template.spring_mvc.service;

import com.template.spring_mvc.model.Empresa;
import com.template.spring_mvc.repository.EmpresaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EmpresaService {
    private final EmpresaRepository empresaRepository;

    public EmpresaService(EmpresaRepository empresaRepository) {
        this.empresaRepository = empresaRepository;
    }

    @Transactional(readOnly = true)
    public List<Empresa> findAll() {
        return empresaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Empresa> findById(Long id) {
        return empresaRepository.findById(id);
    }

    @Transactional
    public Empresa save(Empresa empresa) {
        return empresaRepository.save(empresa);
    }

    @Transactional
    public void deleteById(Long id) {
        empresaRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public boolean existsByNombre(String nombre, Long excludeId) {
        return empresaRepository.findByNombre(nombre)
                .filter(existing -> excludeId == null || !existing.getId().equals(excludeId))
                .isPresent();
    }
}
