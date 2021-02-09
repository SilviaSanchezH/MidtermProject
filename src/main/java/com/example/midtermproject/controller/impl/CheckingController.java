package com.example.midtermproject.controller.impl;

import com.example.midtermproject.controller.dto.CheckingDTO;
import com.example.midtermproject.model.Accounts.Account;
import com.example.midtermproject.model.Accounts.Checking;
import com.example.midtermproject.service.interfaces.ICheckingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class CheckingController {
    @Autowired
    private ICheckingService checkingService;

    //Create a new checking account
    @PostMapping("/account/checking")
    @ResponseStatus(HttpStatus.CREATED)
    public Account newChecking(@RequestBody @Valid CheckingDTO checkingDTO){
        return checkingService.newChecking(checkingDTO);
    }

}
