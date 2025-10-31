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
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String nuevo(@AuthenticationPrincipal UserDetails principal, Model model, RedirectAttributes ra) {
        // Verifica que el usuario tenga registro de Estudiante
        boolean tienePerfil = estudianteService.findByUserEmail(principal.getUsername()).isPresent();
        if (!tienePerfil) {
            ra.addFlashAttribute("error", "Debes completar tu registro de estudiante antes de crear expedientes.");
            return "redirect:/registro/estudiante/registro";
        }

        model.addAttribute("expediente", new ExpedienteCreateDto());
        model.addAttribute("carreras", carreraService.findAll());
        List<Oferta> ofertasConCupo = ofertaService.findAll().stream()
                .filter(o -> o.getOcupados() != null && o.getVacantes() != null && o.getOcupados() < o.getVacantes())
                .collect(Collectors.toList());
        model.addAttribute("ofertas", ofertasConCupo);
        return "expediente/form";
    }

    @PreAuthorize("hasRole('ESTUDIANTE')")
    @PostMapping
    @Transactional
    public String crear(@Valid @ModelAttribute("expediente") ExpedienteCreateDto dto,
                        BindingResult result,
                        Model model,
                        @AuthenticationPrincipal UserDetails principal,
                        RedirectAttributes ra) {
        if (dto.getFechaInicio() != null && dto.getFechaFin() != null && dto.getFechaFin().isBefore(dto.getFechaInicio())) {
            result.addError(new FieldError("expediente", "fechaFin", "La fecha fin no puede ser anterior a la fecha inicio"));
        }

        if (result.hasErrors()) {
            model.addAttribute("carreras", carreraService.findAll());
            List<Oferta> ofertasConCupo = ofertaService.findAll().stream()
                    .filter(o -> o.getOcupados() != null && o.getVacantes() != null && o.getOcupados() < o.getVacantes())
                    .collect(Collectors.toList());
            model.addAttribute("ofertas", ofertasConCupo);
            return "expediente/form";
        }

        java.util.Optional<Estudiante> optEst = estudianteService.findByUserEmail(principal.getUsername());
        if (optEst.isEmpty()) {
            ra.addFlashAttribute("error", "Debes completar tu registro de estudiante antes de crear expedientes.");
            return "redirect:/registro/estudiante/registro";
        }
        Estudiante estudiante = optEst.get();

        Carrera carrera = carreraService.findById(dto.getCarreraId())
                .orElseThrow(() -> new IllegalArgumentException("Carrera no encontrada"));
        Oferta oferta = ofertaService.findById(dto.getOfertaId())
                .orElseThrow(() -> new IllegalArgumentException("Oferta no encontrada"));

    // Regla: un estudiante no puede tener dos expedientes con la misma carrera
    if (expedienteService.existsByEstudianteAndCarrera(estudiante.getId(), carrera.getId())) {
        result.addError(new FieldError("expediente", "carreraId", "Ya tienes un expediente para esta carrera"));
        model.addAttribute("carreras", carreraService.findAll());
        List<Oferta> ofertasConCupo = ofertaService.findAll().stream()
            .filter(o -> o.getOcupados() != null && o.getVacantes() != null && o.getOcupados() < o.getVacantes())
            .collect(java.util.stream.Collectors.toList());
        model.addAttribute("ofertas", ofertasConCupo);
        return "expediente/form";
    }

    // Verificar cupo disponible y actualizar ocupados
        Integer ocupados = oferta.getOcupados() == null ? 0 : oferta.getOcupados();
        Integer vacantes = oferta.getVacantes() == null ? 0 : oferta.getVacantes();
        if (ocupados + 1 > vacantes) {
            result.addError(new FieldError("expediente", "ofertaId", "La oferta seleccionada no tiene cupo disponible"));
            model.addAttribute("carreras", carreraService.findAll());
            List<Oferta> ofertasConCupo = ofertaService.findAll().stream()
                    .filter(o -> o.getOcupados() != null && o.getVacantes() != null && o.getOcupados() < o.getVacantes())
                    .collect(Collectors.toList());
            model.addAttribute("ofertas", ofertasConCupo);
            return "expediente/form";
        }

        Expediente expediente = new Expediente();
        expediente.setEstudiante(estudiante);
        expediente.setCarrera(carrera);
        expediente.setOferta(oferta);
        expediente.setFechaInicio(dto.getFechaInicio());
        expediente.setFechaFin(dto.getFechaFin());
        expediente.setEstado("Registrado");
        expediente.setActivo(true);

    // Persistir cambios: primero actualizar oferta y luego el expediente
        oferta.setOcupados(ocupados + 1);
        ofertaService.save(oferta);
        expediente = expedienteService.save(expediente);
        return "redirect:/expediente/" + expediente.getId();
    }

    @PreAuthorize("hasRole('ESTUDIANTE')")
    @GetMapping("/mis")
    public String misExpedientes(@AuthenticationPrincipal UserDetails principal, Model model, RedirectAttributes ra) {
        java.util.Optional<Estudiante> optEst = estudianteService.findByUserEmail(principal.getUsername());
        if (optEst.isEmpty()) {
            ra.addFlashAttribute("error", "Debes completar tu registro de estudiante antes de crear expedientes.");
            return "redirect:/registro/estudiante/registro";
        }
        Estudiante estudiante = optEst.get();
        List<Expediente> expedientes = expedienteService.findByEstudianteId(estudiante.getId());
        model.addAttribute("expedientes", expedientes);
        return "expediente/estudiante/mis";
    }

    @PreAuthorize("hasRole('ESTUDIANTE')")
    @GetMapping("/detalle/{id}")
    public String verDetalleEstudiante(@PathVariable Long id,
                                       @AuthenticationPrincipal UserDetails principal,
                                       Model model) {
        Expediente expediente = expedienteService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Expediente no encontrado"));
        // Propiedad: solo el dueño puede ver este detalle desde ruta de estudiante
        if (!expediente.getEstudiante().getUser().getEmail().equalsIgnoreCase(principal.getUsername())) {
            return "access-denied";
        }
        model.addAttribute("expediente", expediente);
        return "expediente/estudiante/show";
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
