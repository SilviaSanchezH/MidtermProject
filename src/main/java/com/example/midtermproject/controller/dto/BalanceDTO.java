package com.example.midtermproject.controller.dto;

import com.sun.istack.NotNull;

import java.math.BigDecimal;

public class BalanceDTO {
    @NotNull
    private BigDecimal amount;
    @NotNull
    private String currency;

    public BalanceDTO(BigDecimal amount, String currency) {
        this.amount = amount;
        this.currency = currency;
    }

    public BalanceDTO() {
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
