package com.template.spring_mvc.controller;

import com.template.spring_mvc.model.Carrera;
import com.template.spring_mvc.service.CarreraService;
import com.template.spring_mvc.service.FacultadService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/admin/catalogo/carreras")
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
    public String create(@ModelAttribute("carrera") Carrera carrera, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("facultades", facultadService.findAll());
            return "catalogo/carreras/form";
        }
        carreraService.save(carrera);
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
    public String update(@PathVariable Long id, @ModelAttribute("carrera") Carrera carrera, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("facultades", facultadService.findAll());
            return "catalogo/carreras/form";
        }
        carrera.setId(id);
        carreraService.save(carrera);
        return "redirect:/admin/catalogo/carreras";
    }

    @PostMapping("/{id}/eliminar")
    public String delete(@PathVariable Long id) {
        carreraService.deleteById(id);
        return "redirect:/admin/catalogo/carreras";
    }
}
