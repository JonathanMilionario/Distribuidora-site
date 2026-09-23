package com.distribebidas.model.enums;

public enum OrderStatus {
    EM_SEPARACAO("Em Separação"),
    EM_ROTA("Em Rota"),
    CONCLUIDO("Concluído"),
    CANCELADO("Cancelado");

    private final String label;

    OrderStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
