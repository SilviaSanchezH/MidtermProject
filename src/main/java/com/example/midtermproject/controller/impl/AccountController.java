package com.example.midtermproject.controller.impl;

import com.example.midtermproject.controller.dto.BalanceDTO;
import com.example.midtermproject.model.Accounts.Account;
import com.example.midtermproject.service.interfaces.IAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
public class AccountController {
    @Autowired
    private IAccountService accountService;

    @GetMapping("/account/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Account getAccount(@PathVariable Integer id, Principal principal){
        return accountService.getAccount(id, principal.getName());
    }

    @GetMapping("/account/balance/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BalanceDTO getAccountBalance(@PathVariable Integer id, Principal principal){
        return accountService.getAccountBalance(id, principal.getName());
    }

    @PatchMapping("/account/balance/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateBalance(@PathVariable Integer id, @RequestBody BalanceDTO balanceDTO){
        accountService.updateBalance(id, balanceDTO.getAmount(), balanceDTO.getCurrency());
    }
}
