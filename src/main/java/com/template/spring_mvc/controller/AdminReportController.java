package com.template.spring_mvc.controller;

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
        model.addAttribute("start", range.start());
        model.addAttribute("end", range.end());
        model.addAttribute("activosEmpresa", data);
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
}
