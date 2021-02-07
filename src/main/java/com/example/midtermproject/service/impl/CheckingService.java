package com.example.midtermproject.service.impl;

import com.example.midtermproject.controller.dto.CheckingDTO;
import com.example.midtermproject.model.Accounts.Account;
import com.example.midtermproject.model.Accounts.Checking;
import com.example.midtermproject.model.Accounts.StudentChecking;
import com.example.midtermproject.model.Users.AccountHolder;
import com.example.midtermproject.model.shared.Money;
import com.example.midtermproject.repository.AccountHolderRepository;
import com.example.midtermproject.repository.CheckingRepository;
import com.example.midtermproject.repository.StudentCheckingRepository;
import com.example.midtermproject.service.interfaces.ICheckingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.Period;

@Service
public class CheckingService implements ICheckingService {

    @Autowired
    private CheckingRepository checkingRepository;

    @Autowired
    private StudentCheckingRepository studentCheckingRepository;

    @Autowired
    private AccountHolderRepository accountHolderRepository;

    public Account newChecking(CheckingDTO checkingDTO) {
        AccountHolder primaryOwner = accountHolderRepository.findById(checkingDTO.getPrimaryOwnerId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not valid primary owner"));
        AccountHolder secondaryOwner = checkingDTO.getSecondaryOwnerId() != null ? accountHolderRepository.findById(checkingDTO.getSecondaryOwnerId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not valid primary owner")) : null;

        if(Period.between(primaryOwner.getBirth(), LocalDate.now()).getYears() < 24) {
            StudentChecking studentChecking = new StudentChecking(new Money(checkingDTO.getBalance()), primaryOwner, secondaryOwner, checkingDTO.getSecretKey());
            return studentCheckingRepository.save(studentChecking);
        }

        Checking checking = new Checking(new Money(checkingDTO.getBalance()), primaryOwner, secondaryOwner, checkingDTO.getSecretKey());
        return checkingRepository.save(checking);
    }
}
