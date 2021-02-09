package com.example.midtermproject.controller.dto;

import com.example.midtermproject.model.shared.Money;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;

public class CreditCardDTO {
    @NotNull
    private BigDecimal balance;
    @NotNull
    private Integer primaryOwnerId;

    private Integer secondaryOwnerId;
    @DecimalMin(value ="100", message = "The min value for creditLimit is 100")
    @DecimalMax(value = "100000", message = "The max value for creditLimit is 100000")
    private BigDecimal creditLimit;
    @DecimalMin(value ="0.1", message = "The min value for interestRate is 0.1")
    @DecimalMax(value = "0.2", message = "The max value for interestRate is 0.2")
    private BigDecimal interestRate;

    public CreditCardDTO() {
    }

    public CreditCardDTO(@NotNull BigDecimal balance, @NotNull Integer primaryOwnerId, Integer secondaryOwnerId, @DecimalMin(value = "100") @DecimalMax(value = "100000") BigDecimal creditLimit, @DecimalMin(value = "0.1") @DecimalMax(value = "0.2") BigDecimal interestRate) {
        this.balance = balance;
        this.primaryOwnerId = primaryOwnerId;
        this.secondaryOwnerId = secondaryOwnerId;
        this.creditLimit = creditLimit;
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

    public BigDecimal getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(BigDecimal creditLimit) {
        this.creditLimit = creditLimit;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }
}
