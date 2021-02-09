package com.example.midtermproject.controller.dto;

import com.example.midtermproject.enums.Status;
import com.example.midtermproject.model.shared.Money;

import javax.persistence.*;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;

public class SavingsDTO {

    private BigDecimal balance;

    @NotNull
    private Integer primaryOwnerId;

    private Integer secondaryOwnerId;

    @NotNull
    private String secretKey;

    @DecimalMax(value = "1000", message = "The max value for minimumBalance is 1000")
    @DecimalMin(value = "100", message = "The min value for minimumBalance is 100")
    private BigDecimal minimumBalance;

    @DecimalMax(value = "0.5", message = "The max value for interestRate is 0.5")
    private BigDecimal interestRate;


    public SavingsDTO() {
    }

    public SavingsDTO(BigDecimal balance, @NotNull Integer primaryOwnerId, Integer secondaryOwnerId, @NotNull String secretKey, @DecimalMax(value = "1000") @DecimalMin(value = "100") BigDecimal minimumBalance, @DecimalMax(value = "0.5") BigDecimal interestRate) {
        this.balance = balance;
        this.primaryOwnerId = primaryOwnerId;
        this.secondaryOwnerId = secondaryOwnerId;
        this.secretKey = secretKey;
        this.minimumBalance = minimumBalance;
        this.interestRate = interestRate;
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

    public BigDecimal getMinimumBalance() {
        return minimumBalance;
    }

    public void setMinimumBalance(BigDecimal minimumBalance) {
        this.minimumBalance = minimumBalance;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }
}
