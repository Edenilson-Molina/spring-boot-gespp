package com.template.spring_mvc.controller;

import com.template.spring_mvc.model.Expediente;
import com.template.spring_mvc.model.Oferta;
import com.template.spring_mvc.service.ExpedienteService;
import com.template.spring_mvc.service.OfertaService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/admin/expedientes")
@PreAuthorize("hasRole('ADMIN')")
public class AdminExpedienteController {

    private static final List<String> ESTADOS = Arrays.asList("Registrado","En curso","Suspendido","Finalizado","Cancelado");

    private final ExpedienteService expedienteService;
    private final OfertaService ofertaService;

    public AdminExpedienteController(ExpedienteService expedienteService, OfertaService ofertaService) {
        this.expedienteService = expedienteService;
        this.ofertaService = ofertaService;
    }

    @GetMapping
    public String index(Model model) {
        model.addAttribute("expedientes", expedienteService.findAll());
        return "expediente/index";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        Expediente expediente = expedienteService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Expediente no encontrado"));
        model.addAttribute("expediente", expediente);
        model.addAttribute("estados", ESTADOS);
        model.addAttribute("ofertas", ofertaService.findAll());
        return "expediente/edit";
    }

    @PostMapping("/{id}/editar")
    public String actualizar(@PathVariable Long id,
                             @RequestParam String estado,
                             @RequestParam(required = false) Long ofertaId,
                             Model model) {
        Expediente expediente = expedienteService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Expediente no encontrado"));
        if (!ESTADOS.contains(estado)) {
            model.addAttribute("expediente", expediente);
            model.addAttribute("estados", ESTADOS);
            model.addAttribute("ofertas", ofertaService.findAll());
            model.addAttribute("error", "Estado inválido");
            return "expediente/edit";
        }
        expediente.setEstado(estado);
        if (ofertaId != null) {
            Oferta nueva = ofertaService.findById(ofertaId)
                    .orElseThrow(() -> new IllegalArgumentException("Oferta no encontrada"));
            expediente.setOferta(nueva);
        }
        expedienteService.save(expediente);
        return "redirect:/admin/expedientes";
    }
}
