package com.example.midtermproject.model.Accounts;

import com.example.midtermproject.enums.Status;
import com.example.midtermproject.model.Users.AccountHolder;
import com.example.midtermproject.model.shared.Money;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@PrimaryKeyJoinColumn(name = "id")
public class StudentChecking extends Account {
    @NotNull
    private String secretKey;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;

    public StudentChecking() {
    }

    public StudentChecking(Money balance, AccountHolder primaryOwner, AccountHolder secondaryOwner, @NotNull String secretKey) {
        super(balance, primaryOwner, secondaryOwner);
        this.secretKey = secretKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
