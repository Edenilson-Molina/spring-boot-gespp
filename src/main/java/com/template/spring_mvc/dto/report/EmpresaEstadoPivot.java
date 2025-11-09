package com.template.spring_mvc.dto.report;

public class EmpresaEstadoPivot {
    private final Long empresaId;
    private final String empresaNombre;
    private long registrados;
    private long enCurso;
    private long suspendidos;
    private long finalizados;
    private long cancelados;

    public EmpresaEstadoPivot(Long empresaId, String empresaNombre) {
        this.empresaId = empresaId;
        this.empresaNombre = empresaNombre;
    }

    public void add(String estado, long total) {
        if (estado == null) return;
        switch (estado) {
            case "Registrado" -> this.registrados += total;
            case "En curso" -> this.enCurso += total;
            case "Suspendido" -> this.suspendidos += total;
            case "Finalizado" -> this.finalizados += total;
            case "Cancelado" -> this.cancelados += total;
            default -> {}
        }
    }

    public Long getEmpresaId() { return empresaId; }
    public String getEmpresaNombre() { return empresaNombre; }
    public long getRegistrados() { return registrados; }
    public long getEnCurso() { return enCurso; }
    public long getSuspendidos() { return suspendidos; }
    public long getFinalizados() { return finalizados; }
    public long getCancelados() { return cancelados; }
    public long getTotal() { return registrados + enCurso + suspendidos + finalizados + cancelados; }
}
