package com.template.spring_mvc.controller;

import com.template.spring_mvc.model.Oferta;
import com.template.spring_mvc.service.EmpresaService;
import com.template.spring_mvc.service.OfertaService;
import com.template.spring_mvc.service.SupervisorService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/admin/catalogo/ofertas")
@PreAuthorize("hasRole('ADMIN')")
public class OfertaController {

    private final OfertaService ofertaService;
    private final SupervisorService supervisorService;
    private final EmpresaService empresaService;

    public OfertaController(OfertaService ofertaService, SupervisorService supervisorService, EmpresaService empresaService) {
        this.ofertaService = ofertaService;
        this.supervisorService = supervisorService;
        this.empresaService = empresaService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("ofertas", ofertaService.findAll());
        return "catalogo/ofertas/index";
    }

    @GetMapping("/nuevo")
    public String showCreateForm(Model model) {
        Oferta oferta = new Oferta();
        // ocupados se inicializa a 0 en @PrePersist, pero lo mostramos como 0 en el form si deseas
        model.addAttribute("oferta", oferta);
        model.addAttribute("supervisores", supervisorService.findAll());
        return "catalogo/ofertas/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("oferta") Oferta oferta, BindingResult result, Model model, RedirectAttributes redirect) {
        if (oferta.getOcupados() == null) {
            oferta.setOcupados(0);
        }
        if (oferta.getVacantes() != null && oferta.getOcupados() != null && oferta.getVacantes() < oferta.getOcupados()) {
            result.rejectValue("vacantes", "invalid", "Las vacantes deben ser mayores o iguales a ocupados");
        }
        if (result.hasErrors()) {
            model.addAttribute("supervisores", supervisorService.findAll());
            return "catalogo/ofertas/form";
        }
        ofertaService.save(oferta);
        redirect.addFlashAttribute("success", "Oferta creada correctamente");
        return "redirect:/admin/catalogo/ofertas";
    }

    @GetMapping("/{id}/editar")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<Oferta> ofertaOpt = ofertaService.findById(id);
        if (ofertaOpt.isEmpty()) {
            return "redirect:/admin/catalogo/ofertas";
        }
        model.addAttribute("oferta", ofertaOpt.get());
        model.addAttribute("supervisores", supervisorService.findAll());
        return "catalogo/ofertas/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute("oferta") Oferta oferta, BindingResult result, Model model, RedirectAttributes redirect) {
        oferta.setId(id);
        // Cargar oferta actual para conocer ocupados reales
        Optional<Oferta> currentOpt = ofertaService.findById(id);
        if (currentOpt.isPresent()) {
            Integer ocupadosActual = currentOpt.get().getOcupados();
            if (oferta.getVacantes() != null && ocupadosActual != null && oferta.getVacantes() < ocupadosActual) {
                result.rejectValue("vacantes", "invalid", "Las vacantes deben ser mayores o iguales a ocupados (" + ocupadosActual + ")");
            }
        }
        if (result.hasErrors()) {
            model.addAttribute("supervisores", supervisorService.findAll());
            return "catalogo/ofertas/form";
        }
        // No permitimos editar ocupados desde este mantenimiento; preservarlo
        currentOpt.ifPresent(existing -> oferta.setOcupados(existing.getOcupados()));
        ofertaService.save(oferta);
        redirect.addFlashAttribute("success", "Oferta actualizada correctamente");
        return "redirect:/admin/catalogo/ofertas";
    }

    @PostMapping("/{id}/eliminar")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        ofertaService.deleteById(id);
        redirect.addFlashAttribute("success", "Oferta eliminada correctamente");
        return "redirect:/admin/catalogo/ofertas";
    }
}
