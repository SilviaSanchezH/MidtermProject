package com.example.midtermproject.model.Accounts;

import com.example.midtermproject.model.Users.AccountHolder;
import com.example.midtermproject.model.shared.Money;

import javax.persistence.*;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;

@Entity
@PrimaryKeyJoinColumn(name = "id")
public class CreditCard extends Account{
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "currency", column = @Column(name = "credit_limit_currency")),
            @AttributeOverride(name = "amount", column = @Column(name = "credit_limit_amount", columnDefinition = "decimal(19,2) default 100"))
    })

    private Money creditLimit = new Money(new BigDecimal("100"), Currency.getInstance("USD"));
    @Column(columnDefinition = "decimal(19,2) default 0.2")
    @DecimalMin(value ="0.1")
    @DecimalMax(value = "0.2")
    private BigDecimal interestRate = new BigDecimal("0.2");

    private LocalDate interestAdditionDate;

    public CreditCard() {
    }

    public CreditCard(Money balance, AccountHolder primaryOwner, AccountHolder secondaryOwner, Money creditLimit, @DecimalMin(value = "0.1") @DecimalMax(value = "0.2") BigDecimal interestRate) {
        super(balance, primaryOwner, secondaryOwner);
        if(creditLimit!= null) this.creditLimit = creditLimit;
        if(interestRate!= null) this.interestRate = interestRate;
    }

    public Money getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(Money creditLimit) {
        this.creditLimit = creditLimit;
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
