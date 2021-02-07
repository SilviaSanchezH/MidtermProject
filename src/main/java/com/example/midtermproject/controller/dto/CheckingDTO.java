package com.example.midtermproject.controller.dto;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;


public class CheckingDTO {

    private BigDecimal balance;
    @NotNull
    private Integer primaryOwnerId;

    private Integer secondaryOwnerId;

    @NotNull
    private String secretKey;

    public CheckingDTO() {
    }

    public CheckingDTO(BigDecimal balance, @NotNull Integer primaryOwnerId, Integer secondaryOwnerId, @NotNull String secretKey) {
        this.balance = balance;
        this.primaryOwnerId = primaryOwnerId;
        this.secondaryOwnerId = secondaryOwnerId;
        this.secretKey = secretKey;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Integer getPrimaryOwnerId() {
        return primaryOwnerId;
    }

    public void setPrimaryOwnerId(Integer primaryOwnerId) {
        this.primaryOwnerId = primaryOwnerId;
    }

    public Integer getSecondaryOwnerId() {
        return secondaryOwnerId;
    }

    public void setSecondaryOwnerId(Integer secondaryOwnerId) {
        this.secondaryOwnerId = secondaryOwnerId;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }
}
