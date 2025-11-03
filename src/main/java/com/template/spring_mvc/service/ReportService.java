package com.template.spring_mvc.service;

import com.template.spring_mvc.repository.ExpedienteRepository;
import com.template.spring_mvc.repository.projection.ActivosEmpresaRow;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReportService {

    private final ExpedienteRepository expedienteRepository;

    public ReportService(ExpedienteRepository expedienteRepository) {
        this.expedienteRepository = expedienteRepository;
    }

    public record DateRange(LocalDate start, LocalDate end) {}

    public DateRange normalizeRange(LocalDate start, LocalDate end) {
        LocalDate today = LocalDate.now();
        LocalDate defEnd = end != null ? end : today;
        LocalDate defStart = start != null ? start : defEnd.minusMonths(1).withDayOfMonth(1);
        if (defStart.isAfter(defEnd)) {
            // swap if provided in reverse
            LocalDate tmp = defStart;
            defStart = defEnd;
            defEnd = tmp;
        }
        return new DateRange(defStart, defEnd);
    }

    public List<ActivosEmpresaRow> activosPorEmpresa(LocalDate start, LocalDate end) {
        DateRange range = normalizeRange(start, end);
        return expedienteRepository.countActivosPorEmpresa(range.start(), range.end());
    }
}
