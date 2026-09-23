package com.distribebidas.model.enums;

public enum MovementType {
    ENTRADA("Entrada de Carga"),
    SAIDA("Saída por Avaria/Perda"),
    VENDA("Saída por Venda"),
    AJUSTE("Ajuste Manual de Inventário");

    private final String description;

    MovementType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
