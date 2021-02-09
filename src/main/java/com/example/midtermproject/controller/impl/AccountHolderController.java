package com.example.midtermproject.controller.impl;

import com.example.midtermproject.controller.dto.AccountHolderDTO;
import com.example.midtermproject.model.Accounts.Account;
import com.example.midtermproject.model.Users.AccountHolder;
import com.example.midtermproject.repository.AccountHolderRepository;
import com.example.midtermproject.service.interfaces.IAccountHolderService;
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
    private IAccountHolderService accountHolderService;

    @PostMapping("/user/accountholder")
    @ResponseStatus(HttpStatus.CREATED)
    public AccountHolder newAccountHolder(@Valid @RequestBody AccountHolderDTO accountHolderDTO){
        return accountHolderService.newAccountHolder(accountHolderDTO);
    }
}
