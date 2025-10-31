package com.template.spring_mvc.controller;

import com.template.spring_mvc.model.Supervisor;
import com.template.spring_mvc.service.EmpresaService;
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
@RequestMapping("/admin/catalogo/supervisores")
@PreAuthorize("hasRole('ADMIN')")
public class SupervisorController {

    private final SupervisorService supervisorService;
    private final EmpresaService empresaService;

    public SupervisorController(SupervisorService supervisorService, EmpresaService empresaService) {
        this.supervisorService = supervisorService;
        this.empresaService = empresaService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("supervisores", supervisorService.findAll());
        return "catalogo/supervisores/index";
    }

    @GetMapping("/nuevo")
    public String showCreateForm(Model model) {
        model.addAttribute("supervisor", new Supervisor());
        model.addAttribute("empresas", empresaService.findAll());
        return "catalogo/supervisores/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("supervisor") Supervisor supervisor, BindingResult result, Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            model.addAttribute("empresas", empresaService.findAll());
            return "catalogo/supervisores/form";
        }
        supervisorService.save(supervisor);
        redirect.addFlashAttribute("success", "Supervisor creado correctamente");
        return "redirect:/admin/catalogo/supervisores";
    }

    @GetMapping("/{id}/editar")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<Supervisor> supervisorOpt = supervisorService.findById(id);
        if (supervisorOpt.isEmpty()) {
            return "redirect:/admin/catalogo/supervisores";
        }
        model.addAttribute("supervisor", supervisorOpt.get());
        model.addAttribute("empresas", empresaService.findAll());
        return "catalogo/supervisores/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute("supervisor") Supervisor supervisor, BindingResult result, Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            model.addAttribute("empresas", empresaService.findAll());
            return "catalogo/supervisores/form";
        }
        supervisor.setId(id);
        supervisorService.save(supervisor);
        redirect.addFlashAttribute("success", "Supervisor actualizado correctamente");
        return "redirect:/admin/catalogo/supervisores";
    }

    @PostMapping("/{id}/eliminar")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        supervisorService.deleteById(id);
        redirect.addFlashAttribute("success", "Supervisor eliminado correctamente");
        return "redirect:/admin/catalogo/supervisores";
    }
}
