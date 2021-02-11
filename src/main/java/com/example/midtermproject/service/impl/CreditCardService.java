package com.example.midtermproject.service.impl;

import com.example.midtermproject.controller.dto.CreditCardDTO;
import com.example.midtermproject.model.Accounts.CreditCard;
import com.example.midtermproject.model.Users.AccountHolder;
import com.example.midtermproject.model.shared.Money;
import com.example.midtermproject.repository.AccountHolderRepository;
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

    @Autowired
    private AccountHolderRepository accountHolderRepository;

    public CreditCard newCreditCard(CreditCardDTO creditCardDTO) {
        AccountHolder primaryOwner = accountHolderRepository.findById(creditCardDTO.getPrimaryOwnerId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not valid primary owner"));
        AccountHolder secondaryOwner = creditCardDTO.getSecondaryOwnerId() != null ? accountHolderRepository.findById(creditCardDTO.getSecondaryOwnerId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not valid secondary owner")) : null;


        if(creditCardDTO.getCreditLimit() == null) creditCardDTO.setCreditLimit(new BigDecimal("100"));
        else if(creditCardDTO.getCreditLimit().compareTo(new BigDecimal(100)) < 0 ||
                creditCardDTO.getCreditLimit().compareTo(new BigDecimal(100000)) > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Credit limit must be between 100 and 100000");
        }
        if(creditCardDTO.getInterestRate().compareTo(new BigDecimal("0.1")) < 0 ||
                creditCardDTO.getInterestRate().compareTo(new BigDecimal("0.2")) > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Interest rate must be between 0.1 and 0.2");
        }

        CreditCard creditCard = new CreditCard(new Money(creditCardDTO.getBalance()), primaryOwner, secondaryOwner, new Money(creditCardDTO.getCreditLimit()), creditCardDTO.getInterestRate());
        return creditCardRepository.save(creditCard);
    }
}
