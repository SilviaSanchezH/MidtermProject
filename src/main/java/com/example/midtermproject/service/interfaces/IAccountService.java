package com.example.midtermproject.service.interfaces;

import com.example.midtermproject.controller.dto.BalanceDTO;
import com.example.midtermproject.model.Accounts.Account;

import java.math.BigDecimal;

public interface IAccountService {
    public void updateBalance(Integer id, BigDecimal amount, String currency);
    public BalanceDTO getAccountBalance(Integer id, String userName);
    public Account getAccount(Integer id, String userName);
}
