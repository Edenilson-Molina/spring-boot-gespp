package com.template.spring_mvc.service;

import com.template.spring_mvc.model.Oferta;
import com.template.spring_mvc.repository.OfertaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class OfertaService {
    private final OfertaRepository ofertaRepository;

    public OfertaService(OfertaRepository ofertaRepository) {
        this.ofertaRepository = ofertaRepository;
    }

    @Transactional(readOnly = true)
    public List<Oferta> findAll() {
        return ofertaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Oferta> findById(Long id) {
        return ofertaRepository.findById(id);
    }

    @Transactional
    public Oferta save(Oferta oferta) {
        return ofertaRepository.save(oferta);
    }

    @Transactional
    public void deleteById(Long id) {
        ofertaRepository.deleteById(id);
    }
}
