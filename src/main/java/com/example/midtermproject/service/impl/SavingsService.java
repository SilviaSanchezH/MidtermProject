package com.example.midtermproject.service.impl;

import com.example.midtermproject.controller.dto.SavingsDTO;
import com.example.midtermproject.model.Accounts.Savings;
import com.example.midtermproject.model.Users.AccountHolder;
import com.example.midtermproject.model.shared.Money;
import com.example.midtermproject.repository.AccountHolderRepository;
import com.example.midtermproject.repository.SavingsRepository;
import com.example.midtermproject.service.interfaces.ISavingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

@Service
public class SavingsService implements ISavingsService {
    @Autowired
    private SavingsRepository savingsRepository;

    @Autowired
    private AccountHolderRepository accountHolderRepository;

    @Override
    //Create a new savings account
    public Savings newSaving(SavingsDTO savingsDTO) {
        AccountHolder primaryOwner = accountHolderRepository.findById(savingsDTO.getPrimaryOwnerId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not valid primary owner"));
        AccountHolder secondaryOwner = savingsDTO.getSecondaryOwnerId() != null ? accountHolderRepository.findById(savingsDTO.getSecondaryOwnerId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not valid primary owner")) : null;

        if(savingsDTO.getInterestRate()==null) savingsDTO.setInterestRate(new BigDecimal("0.0025"));
        else if(savingsDTO.getInterestRate().compareTo(new BigDecimal("0.5")) > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Interest rate must be lower or equal than 0.5");
        }
        if(savingsDTO.getMinimumBalance()==null) savingsDTO.setMinimumBalance(new BigDecimal("1000"));
        else if(savingsDTO.getMinimumBalance().compareTo(new BigDecimal(100)) < 0 ||
                savingsDTO.getMinimumBalance().compareTo(new BigDecimal(1000)) > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Minimum balance must be between 100 and 1000");
        }

        Savings savings = new Savings(new Money(savingsDTO.getBalance()), primaryOwner, secondaryOwner, savingsDTO.getSecretKey(), new Money(savingsDTO.getMinimumBalance()), savingsDTO.getInterestRate());
        return savingsRepository.save(savings);
    }
}
