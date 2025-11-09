package com.template.spring_mvc.service;

import com.template.spring_mvc.dto.report.CarreraEstadoPivot;
import com.template.spring_mvc.repository.ExpedienteRepository;
import com.template.spring_mvc.repository.projection.ActivosEmpresaRow;
import com.template.spring_mvc.repository.projection.CarreraEstadoRow;
import com.template.spring_mvc.repository.projection.EmpresaEstadoRow;
import com.template.spring_mvc.repository.projection.EstadoRow;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    public List<CarreraEstadoPivot> porCarreraYEstado(LocalDate start, LocalDate end) {
        DateRange range = normalizeRange(start, end);
        List<CarreraEstadoRow> rows = expedienteRepository.countPorCarreraYEstado(range.start(), range.end());
        Map<Long, CarreraEstadoPivot> map = new LinkedHashMap<>();
        for (CarreraEstadoRow r : rows) {
            CarreraEstadoPivot pivot = map.computeIfAbsent(r.getCarreraId(),
                    id -> new CarreraEstadoPivot(r.getCarreraId(), r.getCarreraNombre()));
            pivot.add(r.getEstado(), r.getTotal());
        }
        return map.values().stream().collect(Collectors.toList());
    }

    public List<com.template.spring_mvc.dto.report.EmpresaEstadoPivot> porEmpresaYEstado(LocalDate start, LocalDate end) {
        DateRange range = normalizeRange(start, end);
        List<EmpresaEstadoRow> rows = expedienteRepository.countPorEmpresaYEstado(range.start(), range.end());
        Map<Long, com.template.spring_mvc.dto.report.EmpresaEstadoPivot> map = new LinkedHashMap<>();
        for (EmpresaEstadoRow r : rows) {
            var pivot = map.computeIfAbsent(r.getEmpresaId(),
                    id -> new com.template.spring_mvc.dto.report.EmpresaEstadoPivot(r.getEmpresaId(), r.getEmpresaNombre()));
            pivot.add(r.getEstado(), r.getTotal());
        }
        return map.values().stream().collect(Collectors.toList());
    }

    public List<EstadoRow> porEstado(LocalDate start, LocalDate end) {
        DateRange range = normalizeRange(start, end);
        return expedienteRepository.countPorEstado(range.start(), range.end());
    }
}
