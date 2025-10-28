package com.template.spring_mvc.controller;

import com.template.spring_mvc.model.Carrera;
import com.template.spring_mvc.service.CarreraService;
import com.template.spring_mvc.service.FacultadService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/admin/catalogo/carreras")
@PreAuthorize("hasRole('ADMIN')")
public class CarreraController {

    private final CarreraService carreraService;
    private final FacultadService facultadService;

    public CarreraController(CarreraService carreraService, FacultadService facultadService) {
        this.carreraService = carreraService;
        this.facultadService = facultadService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("carreras", carreraService.findAll());
        return "catalogo/carreras/index";
    }

    @GetMapping("/nuevo")
    public String showCreateForm(Model model) {
        model.addAttribute("carrera", new Carrera());
        model.addAttribute("facultades", facultadService.findAll());
        return "catalogo/carreras/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("carrera") Carrera carrera, BindingResult result, Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            model.addAttribute("facultades", facultadService.findAll());
            return "catalogo/carreras/form";
        }
        // Duplicados: nombre por facultad
        if (carrera.getFacultad() != null && carreraService.existsByNombreAndFacultad(carrera.getNombre(), carrera.getFacultad().getId(), null)) {
            result.rejectValue("nombre", "duplicate", "Ya existe una carrera con ese nombre en la facultad seleccionada");
            model.addAttribute("facultades", facultadService.findAll());
            return "catalogo/carreras/form";
        }
        carreraService.save(carrera);
        redirect.addFlashAttribute("success", "Carrera creada correctamente");
        return "redirect:/admin/catalogo/carreras";
    }

    @GetMapping("/{id}/editar")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<Carrera> carreraOpt = carreraService.findById(id);
        if (carreraOpt.isEmpty()) {
            return "redirect:/admin/catalogo/carreras";
        }
        model.addAttribute("carrera", carreraOpt.get());
        model.addAttribute("facultades", facultadService.findAll());
        return "catalogo/carreras/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute("carrera") Carrera carrera, BindingResult result, Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            model.addAttribute("facultades", facultadService.findAll());
            return "catalogo/carreras/form";
        }
        carrera.setId(id);
        if (carrera.getFacultad() != null && carreraService.existsByNombreAndFacultad(carrera.getNombre(), carrera.getFacultad().getId(), id)) {
            result.rejectValue("nombre", "duplicate", "Ya existe una carrera con ese nombre en la facultad seleccionada");
            model.addAttribute("facultades", facultadService.findAll());
            return "catalogo/carreras/form";
        }
        carreraService.save(carrera);
        redirect.addFlashAttribute("success", "Carrera actualizada correctamente");
        return "redirect:/admin/catalogo/carreras";
    }

    @PostMapping("/{id}/eliminar")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        carreraService.deleteById(id);
        redirect.addFlashAttribute("success", "Carrera eliminada correctamente");
        return "redirect:/admin/catalogo/carreras";
    }
}
