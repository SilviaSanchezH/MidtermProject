package com.example.midtermproject.controller.dto;

import com.sun.istack.NotNull;

import java.math.BigDecimal;

public class ThirdPartyTransactionDTO {
    @NotNull
    private BigDecimal amount;
    @NotNull
    private Integer accountId;
    @NotNull
    private String secretKey;
    @NotNull
    private Integer thirdPartyId;

    public ThirdPartyTransactionDTO() {
    }

    public ThirdPartyTransactionDTO(BigDecimal amount, Integer accountId, String secretKey, Integer thirdPartyId) {
        this.amount = amount;
        this.accountId = accountId;
        this.secretKey = secretKey;
        this.thirdPartyId = thirdPartyId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public Integer getThirdPartyId() {
        return thirdPartyId;
    }

    public void setThirdPartyId(Integer thirdPartyId) {
        this.thirdPartyId = thirdPartyId;
    }
}
