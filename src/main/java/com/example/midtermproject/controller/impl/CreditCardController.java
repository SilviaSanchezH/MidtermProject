package com.example.midtermproject.controller.impl;

import com.example.midtermproject.model.Accounts.CreditCard;
import com.example.midtermproject.service.impl.CreditCardService;
import com.example.midtermproject.service.interfaces.ICreditCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class CreditCardController {
    @Autowired
    private ICreditCardService creditCardService;

    @PostMapping("/account/creditcard")
    @ResponseStatus(HttpStatus.CREATED)
    public CreditCard newCreditCard(@RequestBody @Valid CreditCard creditCard) {
        return creditCardService.newCreditCard(creditCard);
    }
}
