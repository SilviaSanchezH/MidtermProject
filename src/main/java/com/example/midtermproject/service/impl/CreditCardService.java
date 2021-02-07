package com.example.midtermproject.service.impl;

import com.example.midtermproject.model.Accounts.CreditCard;
import com.example.midtermproject.repository.CreditCardRepository;
import com.example.midtermproject.service.interfaces.ICreditCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

@Service
public class CreditCardService implements ICreditCardService {
    @Autowired
    private CreditCardRepository creditCardRepository;

    public CreditCard newCreditCard(CreditCard creditCard) {
        if(creditCard.getCreditLimit().getAmount().compareTo(new BigDecimal(100)) < 0 ||
                creditCard.getCreditLimit().getAmount().compareTo(new BigDecimal(100000)) > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Credit limit must be between 100 and 100000");
        }
        if(creditCard.getInterestRate().compareTo(new BigDecimal("0.1")) < 0 ||
                creditCard.getInterestRate().compareTo(new BigDecimal("0.2")) > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Interest rate must be between 0.1 and 0.2");
        }
        return creditCardRepository.save(creditCard);
    }
}
