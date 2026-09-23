package com.distribebidas.model.enums;

public enum PaymentStatus {
    PAGO("Pago"),
    PENDENTE("Pendente"),
    CANCELADO("Cancelado");

    private final String label;

    PaymentStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
