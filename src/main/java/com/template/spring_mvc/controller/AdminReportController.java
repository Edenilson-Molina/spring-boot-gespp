package com.template.spring_mvc.controller;

import com.template.spring_mvc.dto.report.CarreraEstadoPivot;
import com.template.spring_mvc.dto.report.EmpresaEstadoPivot;
import com.template.spring_mvc.repository.projection.ActivosEmpresaRow;
import com.template.spring_mvc.service.ReportService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/admin/reportes")
public class AdminReportController {

    private final ReportService reportService;

    public AdminReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping
    public String index(@RequestParam(value = "start", required = false)
                        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
                        @RequestParam(value = "end", required = false)
                        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
                        Model model) {
        var range = reportService.normalizeRange(start, end);
        List<ActivosEmpresaRow> data = reportService.activosPorEmpresa(range.start(), range.end());
        List<CarreraEstadoPivot> porCarrera = reportService.porCarreraYEstado(range.start(), range.end());
        List<EmpresaEstadoPivot> porEmpresa = reportService.porEmpresaYEstado(range.start(), range.end());
        model.addAttribute("start", range.start());
        model.addAttribute("end", range.end());
        model.addAttribute("activosEmpresa", data);
        model.addAttribute("porCarrera", porCarrera);
        model.addAttribute("porEmpresa", porEmpresa);
        return "reportes/index";
    }

    @GetMapping(value = "/activos-por-empresa.csv")
    public void exportActivosCsv(@RequestParam(value = "start", required = false)
                                 @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
                                 @RequestParam(value = "end", required = false)
                                 @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
                                 HttpServletResponse response) throws IOException {
        var range = reportService.normalizeRange(start, end);
        List<ActivosEmpresaRow> rows = reportService.activosPorEmpresa(range.start(), range.end());

        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=activos_por_empresa.csv");

        try (PrintWriter writer = response.getWriter()) {
            writer.println("Empresa,Activos");
            for (ActivosEmpresaRow r : rows) {
                String empresa = r.getEmpresaNombre().replaceAll(",", " ");
                writer.printf("%s,%d%n", empresa, r.getTotalActivos());
            }
        }
    }

    @GetMapping(value = "/carreras.csv")
    public void exportCarrerasCsv(@RequestParam(value = "start", required = false)
                                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
                                  @RequestParam(value = "end", required = false)
                                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
                                  HttpServletResponse response) throws IOException {
        var range = reportService.normalizeRange(start, end);
        List<CarreraEstadoPivot> rows = reportService.porCarreraYEstado(range.start(), range.end());

        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=expedientes_por_carrera.csv");

        try (PrintWriter writer = response.getWriter()) {
            writer.println("Carrera,Registrado,En curso,Suspendido,Finalizado,Cancelado,Total");
            for (CarreraEstadoPivot r : rows) {
                String carrera = r.getCarreraNombre().replaceAll(",", " ");
                writer.printf("%s,%d,%d,%d,%d,%d,%d%n", carrera, r.getRegistrados(), r.getEnCurso(), r.getSuspendidos(), r.getFinalizados(), r.getCancelados(), r.getTotal());
            }
        }
    }

    @GetMapping(value = "/empresas.csv")
    public void exportEmpresasCsv(@RequestParam(value = "start", required = false)
                                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
                                  @RequestParam(value = "end", required = false)
                                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
                                  HttpServletResponse response) throws IOException {
        var range = reportService.normalizeRange(start, end);
        List<EmpresaEstadoPivot> rows = reportService.porEmpresaYEstado(range.start(), range.end());

        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=expedientes_por_empresa.csv");

        try (PrintWriter writer = response.getWriter()) {
            writer.println("Empresa,Registrado,En curso,Suspendido,Finalizado,Cancelado,Total");
            for (EmpresaEstadoPivot r : rows) {
                String empresa = r.getEmpresaNombre().replaceAll(",", " ");
                writer.printf("%s,%d,%d,%d,%d,%d,%d%n", empresa, r.getRegistrados(), r.getEnCurso(), r.getSuspendidos(), r.getFinalizados(), r.getCancelados(), r.getTotal());
            }
        }
    }
}
