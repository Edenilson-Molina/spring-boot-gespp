package com.template.spring_mvc.service;

import com.template.spring_mvc.model.Expediente;
import com.template.spring_mvc.model.Informe;
import com.template.spring_mvc.repository.InformeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class InformeService {

    private final InformeRepository informeRepository;

    public InformeService(InformeRepository informeRepository) {
        this.informeRepository = informeRepository;
    }

    public List<Informe> findByExpediente(Long expedienteId) {
        return informeRepository.findByExpedienteIdOrderByMesAsc(expedienteId);
    }

    public Optional<Informe> findByExpedienteAndMes(Long expedienteId, String mes) {
        return informeRepository.findByExpedienteIdAndMes(expedienteId, mes);
    }

    public Informe save(Informe informe) {
        return informeRepository.save(informe);
    }

    public Map<String, Informe> mapByMes(Long expedienteId) {
        Map<String, Informe> map = new LinkedHashMap<>();
        List<Informe> list = findByExpediente(expedienteId);
        for (Informe inf : list) {
            map.put(inf.getMes(), inf);
        }
        return map;
    }

    public static List<String> mesesEntre(LocalDate inicio, LocalDate fin) {
        if (inicio == null || fin == null) return Collections.emptyList();
        YearMonth start = YearMonth.from(inicio);
        YearMonth end = YearMonth.from(fin);
        List<String> result = new ArrayList<>();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM");
        for (YearMonth ym = start; !ym.isAfter(end); ym = ym.plusMonths(1)) {
            result.add(ym.format(fmt));
        }
        return result;
    }

    public static boolean mesDentroDeRango(String mes, LocalDate inicio, LocalDate fin) {
        try {
            YearMonth target = YearMonth.parse(mes);
            YearMonth start = YearMonth.from(inicio);
            YearMonth end = YearMonth.from(fin);
            return !(target.isBefore(start) || target.isAfter(end));
        } catch (Exception e) {
            return false;
        }
    }

    @Transactional
    public Informe crearOReemplazarArchivo(Expediente expediente, String mes, String originalFilename, byte[] bytes, Path baseUploadsDir) throws IOException {
        if (!mesDentroDeRango(mes, expediente.getFechaInicio(), expediente.getFechaFin())) {
            throw new IllegalArgumentException("Mes fuera del rango del expediente");
        }

        Path dir = baseUploadsDir.resolve(Path.of("informes", String.valueOf(expediente.getId())));
        Files.createDirectories(dir);
        String safeName = mes + ".pdf";
        Path target = dir.resolve(safeName);
        Files.write(target, bytes);

        Informe informe = findByExpedienteAndMes(expediente.getId(), mes).orElseGet(Informe::new);
        informe.setExpediente(expediente);
        informe.setMes(mes);
    // Guardamos la ruta relativa pública para servir el archivo
    informe.setArchivoPath("/uploads/informes/" + expediente.getId() + "/" + safeName);
        // estado regresa a pendiente al subir archivo
        informe.setEstado(Informe.EstadoInforme.PENDIENTE_VERIFICACION);
        informe.setComentario(null);
    return save(informe);
    }

    @Transactional
    public int syncMissingFromFilesystem(Expediente expediente, Path baseUploadsDir) {
        int created = 0;
        List<String> meses = mesesEntre(expediente.getFechaInicio(), expediente.getFechaFin());
        Path dir = baseUploadsDir.resolve(Path.of("informes", String.valueOf(expediente.getId())));
        for (String mes : meses) {
            if (!informeRepository.existsByExpedienteIdAndMes(expediente.getId(), mes)) {
                Path file = dir.resolve(mes + ".pdf");
                if (Files.exists(file)) {
                    Informe inf = new Informe();
                    inf.setExpediente(expediente);
                    inf.setMes(mes);
                    inf.setArchivoPath("/uploads/informes/" + expediente.getId() + "/" + mes + ".pdf");
                    inf.setEstado(Informe.EstadoInforme.PENDIENTE_VERIFICACION);
                    informeRepository.save(inf);
                    created++;
                }
            }
        }
        return created;
    }
}
