package com.distribebidas.model.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class BottleReturnDto {

    @NotNull(message = "A quantidade de vasilhames é obrigatória")
    @Min(value = 1, message = "A quantidade deve ser de no mínimo 1")
    private Integer count;

    public BottleReturnDto() {}

    public BottleReturnDto(Integer count) {
        this.count = count;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
