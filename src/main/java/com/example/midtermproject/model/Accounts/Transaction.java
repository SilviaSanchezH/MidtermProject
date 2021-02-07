package com.example.midtermproject.model.Accounts;

import com.example.midtermproject.model.Users.ThirdParty;
import com.example.midtermproject.model.shared.Money;
import com.sun.istack.NotNull;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    @NotNull
    private final Date transactionDate = new Date();
    @NotNull
    private Money value;
    @ManyToOne
    @JoinColumn(name = "origin_account")
    private Account originAccount;
    @ManyToOne
    @JoinColumn(name = "destination_account")
    private Account destinationAccount;

    @ManyToOne
    @JoinColumn(name = "origin_third_party")
    private ThirdParty originThirdParty;
    @ManyToOne
    @JoinColumn(name = "destination_third_party")
    private ThirdParty destinationThirdParty;

    public Transaction() {
    }

    public Transaction(Money value, Account originAccount, Account destinationAccount) {
        this.value = value;
        this.originAccount = originAccount;
        this.destinationAccount = destinationAccount;
    }

    public Transaction(Money value, Account originAccount, ThirdParty destinationThirdParty) {
        this.value = value;
        this.originAccount = originAccount;
        this.destinationThirdParty = destinationThirdParty;
    }

    public Transaction(Money value, ThirdParty originThirdParty, Account destinationAccount) {
        this.value = value;
        this.originThirdParty = originThirdParty;
        this.destinationAccount = destinationAccount;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public Money getValue() {
        return value;
    }

    public void setValue(Money value) {
        this.value = value;
    }

    public Account getOriginAccount() {
        return originAccount;
    }

    public void setOriginAccount(Account originAccount) {
        this.originAccount = originAccount;
    }

    public Account getDestinationAccount() {
        return destinationAccount;
    }

    public void setDestinationAccount(Account destinationAccount) {
        this.destinationAccount = destinationAccount;
    }

    public ThirdParty getOriginThirdParty() {
        return originThirdParty;
    }

    public void setOriginThirdParty(ThirdParty originThirdParty) {
        this.originThirdParty = originThirdParty;
    }

    public ThirdParty getDestinationThirdParty() {
        return destinationThirdParty;
    }

    public void setDestinationThirdParty(ThirdParty destinationThirdParty) {
        this.destinationThirdParty = destinationThirdParty;
    }
}
