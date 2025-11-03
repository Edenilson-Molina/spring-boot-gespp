package com.template.spring_mvc.dto.report;

public final class EstadoLabels {
    private EstadoLabels() {}
    public static String label(String estado) {
        if (estado == null) return "Desconocido";
        return switch (estado) {
            case "Registrado" -> "Registrado";
            case "En curso" -> "En curso";
            case "Suspendido" -> "Suspendido";
            case "Finalizado" -> "Finalizado";
            case "Cancelado" -> "Cancelado";
            default -> estado;
        };
    }
}
