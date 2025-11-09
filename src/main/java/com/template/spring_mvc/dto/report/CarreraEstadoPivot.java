package com.template.spring_mvc.dto.report;

public class CarreraEstadoPivot {
    private final Long carreraId;
    private final String carreraNombre;
    private long registrados;
    private long enCurso;
    private long suspendidos;
    private long finalizados;
    private long cancelados;

    public CarreraEstadoPivot(Long carreraId, String carreraNombre) {
        this.carreraId = carreraId;
        this.carreraNombre = carreraNombre;
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

    public Long getCarreraId() { return carreraId; }
    public String getCarreraNombre() { return carreraNombre; }
    public long getRegistrados() { return registrados; }
    public long getEnCurso() { return enCurso; }
    public long getSuspendidos() { return suspendidos; }
    public long getFinalizados() { return finalizados; }
    public long getCancelados() { return cancelados; }
    public long getTotal() { return registrados + enCurso + suspendidos + finalizados + cancelados; }
}
