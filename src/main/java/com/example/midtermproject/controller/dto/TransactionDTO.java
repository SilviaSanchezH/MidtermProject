package com.example.midtermproject.controller.dto;

import com.sun.istack.NotNull;

import java.math.BigDecimal;

public class TransactionDTO {
    @NotNull
    private Integer originAccount;

    private Integer destinationAccount;
    private String thirdPartyDestinationHashedKey;

    @NotNull
    private String destinationOwnerName;
    @NotNull
    private BigDecimal quantity;
    @NotNull
    private String currency;

    public TransactionDTO() {
    }

    public TransactionDTO(Integer originAccount, Integer destinationAccount, String destinationOwnerName, BigDecimal quantity, String currency) {
        this.originAccount = originAccount;
        this.destinationAccount = destinationAccount;
        this.destinationOwnerName = destinationOwnerName;
        this.quantity = quantity;
        this.currency = currency;
    }

    public TransactionDTO(Integer originAccount, String thirdPartyDestinationHashedKey, String destinationOwnerName, BigDecimal quantity, String currency) {
        this.originAccount = originAccount;
        this.thirdPartyDestinationHashedKey = thirdPartyDestinationHashedKey;
        this.destinationOwnerName = destinationOwnerName;
        this.quantity = quantity;
        this.currency = currency;
    }

    public Integer getOriginAccount() {
        return originAccount;
    }

    public void setOriginAccount(Integer originAccount) {
        this.originAccount = originAccount;
    }

    public Integer getDestinationAccount() {
        return destinationAccount;
    }

    public void setDestinationAccount(Integer destinationAccount) {
        this.destinationAccount = destinationAccount;
    }

    public String getDestinationOwnerName() {
        return destinationOwnerName;
    }

    public void setDestinationOwnerName(String destinationOwnerName) {
        this.destinationOwnerName = destinationOwnerName;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getThirdPartyDestinationHashedKey() {
        return thirdPartyDestinationHashedKey;
    }

    public void setThirdPartyDestinationHashedKey(String thirdPartyDestinationHashedKey) {
        this.thirdPartyDestinationHashedKey = thirdPartyDestinationHashedKey;
    }
}
