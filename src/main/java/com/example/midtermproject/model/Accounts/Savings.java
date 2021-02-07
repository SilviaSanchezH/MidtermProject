package com.example.midtermproject.model.Accounts;

import com.example.midtermproject.enums.Status;
import com.example.midtermproject.model.Users.AccountHolder;
import com.example.midtermproject.model.shared.Money;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import java.util.Date;

@Entity
@DynamicUpdate
@PrimaryKeyJoinColumn(name = "id")
public class Savings extends Account{
    @NotNull
    private String secretKey;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "currency", column = @Column(name = "minimum_balance_currency")),
            @AttributeOverride(name = "amount", column = @Column(name = "minimum_balance_amount", columnDefinition = "decimal(19,2) default 1000"))
    })
    @DecimalMax(value = "1000")
    @DecimalMin(value = "100")
    private Money minimumBalance = new Money(new BigDecimal(1000), Currency.getInstance("USD"));

    @NotNull
    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;

    @Column(columnDefinition = "decimal(19,2) default 0.0025")
    @DecimalMax(value = "0.5")
    private BigDecimal interestRate = new BigDecimal("0.0025");

    private LocalDate interestAdditionDate;

    public Savings() {
    }

    public Savings(Money balance, AccountHolder primaryOwner, AccountHolder secondaryOwner, @NotNull String secretKey, @DecimalMax(value = "1000") @DecimalMin(value = "100") Money minimumBalance, @DecimalMax(value = "0.5") BigDecimal interestRate) {
        super(balance, primaryOwner, secondaryOwner);
        this.secretKey = secretKey;
        this.minimumBalance = minimumBalance;
        this.interestRate = interestRate;
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public LocalDate getInterestAdditionDate() {
        return interestAdditionDate;
    }

    public void setInterestAdditionDate(LocalDate interestAdditionDate) {
        this.interestAdditionDate = interestAdditionDate;
    }
}
