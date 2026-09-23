package com.distribebidas.model.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class StockAdjustmentDto {

    @NotNull(message = "O ID do produto é obrigatório")
    private Long productId;

    @NotNull(message = "A quantidade é obrigatória")
    @Min(value = 1, message = "A quantidade deve ser de no mínimo 1")
    private Integer delta;

    @NotBlank(message = "O tipo de movimentação é obrigatório (ex: ENTRADA, SAIDA)")
    private String type;

    private String reason = "";

    public StockAdjustmentDto() {}

    public StockAdjustmentDto(Long productId, Integer delta, String type, String reason) {
        this.productId = productId;
        this.delta = delta;
        this.type = type;
        this.reason = reason;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getDelta() {
        return delta;
    }

    public void setDelta(Integer delta) {
        this.delta = delta;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
