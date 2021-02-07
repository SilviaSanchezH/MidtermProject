package com.example.midtermproject.controller.impl;

import com.example.midtermproject.model.Accounts.Account;
import com.example.midtermproject.model.Users.AccountHolder;
import com.example.midtermproject.repository.AccountHolderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class AccountHolderController {
    @Autowired
    private AccountHolderRepository accountHolderRepository;

    @PostMapping("/user/accountholder")
    @ResponseStatus(HttpStatus.CREATED)
    public AccountHolder newAccountHolder(@Valid @RequestBody AccountHolder accountHolder){
        return accountHolderRepository.save(accountHolder);
    }
}
