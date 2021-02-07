package com.example.midtermproject.controller.impl;

import com.example.midtermproject.model.Accounts.Savings;
import com.example.midtermproject.service.interfaces.ISavingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class SavingsController {
    @Autowired
    private ISavingsService savingsService;

    @PostMapping("/account/savings")
    @ResponseStatus(HttpStatus.CREATED)
    public Savings newSaving(@RequestBody @Valid Savings savings) {
        return savingsService.newSaving(savings);
    }
}
