package com.distribebidas.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class CreateOrderRequestDto {

    private Long clientId;

    @NotEmpty(message = "O pedido deve conter ao menos um item")
    @Valid
    private List<OrderItemRequestDto> items = new ArrayList<>();

    @NotNull(message = "A forma de pagamento é obrigatória")
    private String paymentMethod;

    private Double discount = 0.0;

    private Boolean isDelivery = false;

    private String deliveryAddress = "";

    private String notes = "";

    public CreateOrderRequestDto() {}

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public List<OrderItemRequestDto> getItems() {
        return items;
    }

    public void setItems(List<OrderItemRequestDto> items) {
        this.items = items;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    public Boolean getIsDelivery() {
        return isDelivery != null && isDelivery;
    }

    public void setIsDelivery(Boolean delivery) {
        isDelivery = delivery;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
