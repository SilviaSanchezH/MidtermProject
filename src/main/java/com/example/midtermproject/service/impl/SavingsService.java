package com.example.midtermproject.service.impl;

import com.example.midtermproject.model.Accounts.Savings;
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

    @Override
    public Savings newSaving(Savings savings) {
        if(savings.getInterestRate().compareTo(new BigDecimal("0.5")) > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Interest rate must be lower or equal than 0.5");
        }
        if(savings.getMinimumBalance().getAmount().compareTo(new BigDecimal(100)) < 0 ||
                savings.getMinimumBalance().getAmount().compareTo(new BigDecimal(1000)) > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Minimum balance must be between 100 and 1000");
        }
        return savingsRepository.save(savings);
    }
}
