package com.distribebidas.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "clientes")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome ou razão social é obrigatório")
    @Column(nullable = false)
    private String name;

    @Column(name = "trade_name")
    private String tradeName = "";

    private String document = ""; // CNPJ / CPF

    private String phone = "";

    private String address = "";

    @Column(name = "credit_balance", nullable = false)
    private Double creditBalance = 0.0;

    @Column(name = "returnable_bottles_pending", nullable = false)
    private Integer returnableBottlesPending = 0;

    public Client() {}

    public Client(String name, String tradeName, String document, String phone, String address, Double creditBalance, Integer returnableBottlesPending) {
        this.name = name;
        this.tradeName = tradeName;
        this.document = document;
        this.phone = phone;
        this.address = address;
        this.creditBalance = creditBalance;
        this.returnableBottlesPending = returnableBottlesPending;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getTradeName() { return tradeName; }
    public void setTradeName(String tradeName) { this.tradeName = tradeName; }

    public String getDocument() { return document; }
    public void setDocument(String document) { this.document = document; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Double getCreditBalance() { return creditBalance; }
    public void setCreditBalance(Double creditBalance) { this.creditBalance = creditBalance; }

    public Integer getReturnableBottlesPending() { return returnableBottlesPending; }
    public void setReturnableBottlesPending(Integer returnableBottlesPending) { this.returnableBottlesPending = returnableBottlesPending; }
}
