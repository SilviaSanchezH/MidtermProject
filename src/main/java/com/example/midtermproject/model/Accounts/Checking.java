package com.example.midtermproject.model.Accounts;

import com.example.midtermproject.enums.Status;
import com.example.midtermproject.model.Users.AccountHolder;
import com.example.midtermproject.model.shared.Money;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;

@Entity
@PrimaryKeyJoinColumn(name = "id")
public class Checking extends Account{
    @NotNull
    private String secretKey;
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "currency", column = @Column(name = "minimum_balance_currency")),
            @AttributeOverride(name = "amount", column = @Column(name = "minimum_balance_amount"))
    })
    private Money minimumBalance = new Money(new BigDecimal("250"));
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "currency", column = @Column(name = "monthly_maintenance_fee_currency")),
            @AttributeOverride(name = "amount", column = @Column(name = "monthly_maintenance_fee_amount"))
    })
    private final Money monthlyMaintenanceFee = new Money(new BigDecimal(12), Currency.getInstance("USD"));

    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;


    public Checking() {
    }

    public Checking(Money balance, AccountHolder primaryOwner, AccountHolder secondaryOwner, @NotNull String secretKey) {
        super(balance, primaryOwner, secondaryOwner);
        this.secretKey = secretKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public Money getMinimumBalance() {
        return minimumBalance;
    }

    public void setMinimumBalance(Money minimumBalance) {
        this.minimumBalance = minimumBalance;
    }

    public Money getMonthlyMaintenanceFee() {
        return monthlyMaintenanceFee;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
