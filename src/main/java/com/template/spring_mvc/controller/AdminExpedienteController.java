package com.template.spring_mvc.controller;

import com.template.spring_mvc.model.Expediente;
import com.template.spring_mvc.model.Informe;
import com.template.spring_mvc.model.Oferta;
import com.template.spring_mvc.service.ExpedienteService;
import com.template.spring_mvc.service.InformeService;
import com.template.spring_mvc.service.OfertaService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;

@Controller
@RequestMapping("/admin/expedientes")
@PreAuthorize("hasRole('ADMIN')")
public class AdminExpedienteController {

    private static final List<String> ESTADOS = Arrays.asList("Registrado","En curso","Suspendido","Finalizado","Cancelado");

    private final ExpedienteService expedienteService;
    private final OfertaService ofertaService;
    private final InformeService informeService;

    public AdminExpedienteController(ExpedienteService expedienteService, OfertaService ofertaService, InformeService informeService) {
        this.expedienteService = expedienteService;
        this.ofertaService = ofertaService;
        this.informeService = informeService;
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
    List<Oferta> opciones = ofertaService.findAll().stream()
        .filter(o -> (o.getOcupados() != null && o.getVacantes() != null && o.getOcupados() < o.getVacantes())
            || o.getId().equals(expediente.getOferta().getId()))
        .collect(Collectors.toList());
    model.addAttribute("ofertas", opciones);
        return "expediente/edit";
    }

    @PostMapping("/{id}/editar")
    @Transactional
    public String actualizar(@PathVariable Long id,
                             @RequestParam String estado,
                             @RequestParam(required = false) Long ofertaId,
                             Model model) {
        Expediente expediente = expedienteService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Expediente no encontrado"));
        if (!ESTADOS.contains(estado)) {
            model.addAttribute("expediente", expediente);
            model.addAttribute("estados", ESTADOS);
        List<Oferta> opciones = ofertaService.findAll().stream()
            .filter(o -> (o.getOcupados() != null && o.getVacantes() != null && o.getOcupados() < o.getVacantes())
                || o.getId().equals(expediente.getOferta().getId()))
            .collect(Collectors.toList());
        model.addAttribute("ofertas", opciones);
            model.addAttribute("error", "Estado inválido");
            return "expediente/edit";
        }
    expediente.setEstado(estado);
    if (ofertaId != null && !ofertaId.equals(expediente.getOferta().getId())) {
        // Validar cupo en la nueva oferta
        Oferta actual = expediente.getOferta();
        Oferta nueva = ofertaService.findById(ofertaId)
            .orElseThrow(() -> new IllegalArgumentException("Oferta no encontrada"));

        Integer ocupadosNueva = nueva.getOcupados() == null ? 0 : nueva.getOcupados();
        Integer vacantesNueva = nueva.getVacantes() == null ? 0 : nueva.getVacantes();
        if (ocupadosNueva + 1 > vacantesNueva) {
        model.addAttribute("expediente", expediente);
        model.addAttribute("estados", ESTADOS);
        List<Oferta> opciones = ofertaService.findAll().stream()
            .filter(o -> (o.getOcupados() != null && o.getVacantes() != null && o.getOcupados() < o.getVacantes())
                || o.getId().equals(expediente.getOferta().getId()))
            .collect(Collectors.toList());
        model.addAttribute("ofertas", opciones);
        model.addAttribute("error", "La oferta seleccionada no tiene cupo disponible");
        return "expediente/edit";
        }

        // Actualizar ocupados: -1 a oferta actual (sin bajar de 0), +1 a nueva oferta
        Integer ocupadosActual = actual.getOcupados() == null ? 0 : actual.getOcupados();
        actual.setOcupados(Math.max(0, ocupadosActual - 1));
        nueva.setOcupados(ocupadosNueva + 1);

        // Persistir cambios
        ofertaService.save(actual);
        ofertaService.save(nueva);

        expediente.setOferta(nueva);
    }
    expedienteService.save(expediente);
        return "redirect:/admin/expedientes";
    }

    @GetMapping("/{id}/revision")
    public String revisarInformes(@PathVariable Long id, Model model) {
        Expediente expediente = expedienteService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Expediente no encontrado"));
        try { informeService.syncMissingFromFilesystem(expediente, java.nio.file.Path.of("uploads")); } catch (Exception ignore) {}
        List<String> meses = InformeService.mesesEntre(expediente.getFechaInicio(), expediente.getFechaFin());
        java.util.Map<String, Informe> informes = informeService.mapByMes(expediente.getId());
        model.addAttribute("expediente", expediente);
        model.addAttribute("meses", meses);
        model.addAttribute("informes", informes);
        return "expediente/revision";
    }

    // Debug endpoint: devuelve informes de un expediente en JSON
    @GetMapping("/{id}/informes.json")
    @ResponseBody
    public java.util.List<Informe> informesJson(@PathVariable Long id) {
        return informeService.findByExpediente(id);
    }

    @PostMapping("/{expedienteId}/informes/{informeId}/estado")
    @Transactional
    public String actualizarEstadoInforme(@PathVariable Long expedienteId,
                                          @PathVariable Long informeId,
                                          @RequestParam("estado") Informe.EstadoInforme estado,
                                          @RequestParam(value = "comentario", required = false) String comentario,
                                          Model model) {
        Expediente expediente = expedienteService.findById(expedienteId)
                .orElseThrow(() -> new IllegalArgumentException("Expediente no encontrado"));
        Informe informe = informeService.findByExpediente(expedienteId).stream()
                .filter(i -> i.getId().equals(informeId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Informe no encontrado"));
        informe.setEstado(estado);
        informe.setComentario(comentario);
        informeService.save(informe);
        return "redirect:/admin/expedientes/" + expedienteId + "/revision";
    }
}
