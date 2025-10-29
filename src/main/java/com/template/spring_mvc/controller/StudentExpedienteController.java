package com.template.spring_mvc.controller;

import com.template.spring_mvc.dto.ExpedienteCreateDto;
import com.template.spring_mvc.model.*;
import com.template.spring_mvc.service.*;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/expediente")
public class StudentExpedienteController {

    private final ExpedienteService expedienteService;
    private final EstudianteService estudianteService;
    private final CarreraService carreraService;
    private final OfertaService ofertaService;

    public StudentExpedienteController(ExpedienteService expedienteService,
                                       EstudianteService estudianteService,
                                       CarreraService carreraService,
                                       OfertaService ofertaService) {
        this.expedienteService = expedienteService;
        this.estudianteService = estudianteService;
        this.carreraService = carreraService;
        this.ofertaService = ofertaService;
    }

    @PreAuthorize("hasRole('ESTUDIANTE')")
    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("expediente", new ExpedienteCreateDto());
        model.addAttribute("carreras", carreraService.findAll());
        model.addAttribute("ofertas", ofertaService.findAll());
        return "expediente/form";
    }

    @PreAuthorize("hasRole('ESTUDIANTE')")
    @PostMapping
    public String crear(@Valid @ModelAttribute("expediente") ExpedienteCreateDto dto,
                        BindingResult result,
                        Model model,
                        @AuthenticationPrincipal UserDetails principal) {
        if (dto.getFechaInicio() != null && dto.getFechaFin() != null && dto.getFechaFin().isBefore(dto.getFechaInicio())) {
            result.addError(new FieldError("expediente", "fechaFin", "La fecha fin no puede ser anterior a la fecha inicio"));
        }

        if (result.hasErrors()) {
            model.addAttribute("carreras", carreraService.findAll());
            model.addAttribute("ofertas", ofertaService.findAll());
            return "expediente/form";
        }

        Estudiante estudiante = estudianteService.findByUserEmail(principal.getUsername())
                .orElseThrow(() -> new IllegalStateException("Estudiante no encontrado para el usuario actual"));

        Carrera carrera = carreraService.findById(dto.getCarreraId())
                .orElseThrow(() -> new IllegalArgumentException("Carrera no encontrada"));
        Oferta oferta = ofertaService.findById(dto.getOfertaId())
                .orElseThrow(() -> new IllegalArgumentException("Oferta no encontrada"));

        Expediente expediente = new Expediente();
        expediente.setEstudiante(estudiante);
        expediente.setCarrera(carrera);
        expediente.setOferta(oferta);
        expediente.setFechaInicio(dto.getFechaInicio());
        expediente.setFechaFin(dto.getFechaFin());
        expediente.setEstado("Registrado");
        expediente.setActivo(true);

        expediente = expedienteService.save(expediente);
        return "redirect:/expediente/" + expediente.getId();
    }

    @PreAuthorize("hasAnyRole('ESTUDIANTE','ADMIN')")
    @GetMapping("/{id}")
    public String ver(@PathVariable Long id, Model model) {
        Expediente expediente = expedienteService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Expediente no encontrado"));
        model.addAttribute("expediente", expediente);
        return "expediente/show";
    }
}
