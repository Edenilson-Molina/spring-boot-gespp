package com.template.spring_mvc.controller;

import com.template.spring_mvc.model.Empresa;
import com.template.spring_mvc.service.EmpresaService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/admin/catalogo/empresas")
@PreAuthorize("hasRole('ADMIN')")
public class EmpresaController {

    private final EmpresaService empresaService;

    public EmpresaController(EmpresaService empresaService) {
        this.empresaService = empresaService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("empresas", empresaService.findAll());
        return "catalogo/empresas/index";
    }

    @GetMapping("/nuevo")
    public String showCreateForm(Model model) {
        model.addAttribute("empresa", new Empresa());
        return "catalogo/empresas/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("empresa") Empresa empresa, BindingResult result, Model model, RedirectAttributes redirect) {
        if (empresaService.existsByNombre(empresa.getNombre(), null)) {
            result.rejectValue("nombre", "duplicate", "Ya existe una empresa con ese nombre");
        }
        if (result.hasErrors()) {
            return "catalogo/empresas/form";
        }
        empresaService.save(empresa);
        redirect.addFlashAttribute("success", "Empresa creada correctamente");
        return "redirect:/admin/catalogo/empresas";
    }

    @GetMapping("/{id}/editar")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<Empresa> empresaOpt = empresaService.findById(id);
        if (empresaOpt.isEmpty()) {
            return "redirect:/admin/catalogo/empresas";
        }
        model.addAttribute("empresa", empresaOpt.get());
        return "catalogo/empresas/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute("empresa") Empresa empresa, BindingResult result, Model model, RedirectAttributes redirect) {
        if (empresaService.existsByNombre(empresa.getNombre(), id)) {
            result.rejectValue("nombre", "duplicate", "Ya existe una empresa con ese nombre");
        }
        if (result.hasErrors()) {
            return "catalogo/empresas/form";
        }
        empresa.setId(id);
        empresaService.save(empresa);
        redirect.addFlashAttribute("success", "Empresa actualizada correctamente");
        return "redirect:/admin/catalogo/empresas";
    }

    @PostMapping("/{id}/eliminar")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        empresaService.deleteById(id);
        redirect.addFlashAttribute("success", "Empresa eliminada correctamente");
        return "redirect:/admin/catalogo/empresas";
    }
}
