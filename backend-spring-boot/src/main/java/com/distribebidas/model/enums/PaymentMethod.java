package com.distribebidas.model.enums;

public enum PaymentMethod {
    PIX("PIX"),
    DINHEIRO("Dinheiro"),
    CARTAO_DEBITO("Cartão de Débito"),
    CARTAO_CREDITO("Cartão de Crédito"),
    FATURADO_BOLETO("Faturado / Boleto (Fiado)");

    private final String description;

    PaymentMethod(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
