package com.example.midtermproject.service.impl;

import com.example.midtermproject.controller.dto.CreditCardDTO;
import com.example.midtermproject.controller.dto.SavingsDTO;
import com.example.midtermproject.model.Users.AccountHolder;
import com.example.midtermproject.model.shared.Address;
import com.example.midtermproject.repository.AccountHolderRepository;
import com.example.midtermproject.repository.AccountRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SavingsServiceTest {

    @Autowired
    private AccountHolderRepository accountHolderRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private SavingsService savingsService;

    public AccountHolder nino;
    public AccountHolder nina;

    @BeforeEach
    void setUp() {
        Address primaryAddress = new Address("castellana", "madrid", "28888");
        Address secondaryAddress = new Address("goya", "madrid", "28976");
        nino = new AccountHolder("Nino", "123", "Nino", LocalDate.of(1950, 9,8), primaryAddress, secondaryAddress);
        nina = new AccountHolder("Nina", "123", "Nina", LocalDate.of(1950, 9,8), primaryAddress, secondaryAddress);
        accountHolderRepository.save(nino);
        accountHolderRepository.save(nina);
    }

    @AfterEach
    void tearDown() {
        accountRepository.deleteAll();
        accountHolderRepository.deleteAll();
    }

    @Test
    void newSaving_newSavingAccount_Create() {
        SavingsDTO savingsDTO = new SavingsDTO(new BigDecimal("9888"), nino.getId(), nina.getId(), "123", new BigDecimal("1000"), new BigDecimal(0.1));
        savingsService.newSaving(savingsDTO);
        assertEquals("Nino", accountRepository.findByPrimaryOwner(nino).get(0).getPrimaryOwner().getName());
    }

    @Test
    void newSaving_newSavingAccountWithoutSecondaryOwner_Create() {
        SavingsDTO savingsDTO = new SavingsDTO(new BigDecimal("9888"), nino.getId(), null, "123", new BigDecimal("1000"), new BigDecimal(0.1));
        savingsService.newSaving(savingsDTO);
        assertEquals("Nino", accountRepository.findByPrimaryOwner(nino).get(0).getPrimaryOwner().getName());
    }

    @Test
    void newSaving_invalidMinimumBalanceNewSavingAccount_Create() {
        SavingsDTO savingsDTO = new SavingsDTO(new BigDecimal("9888"), nino.getId(), null, "123", new BigDecimal("1200"), new BigDecimal(0.1));
        assertThrows(ResponseStatusException.class, ()-> savingsService.newSaving(savingsDTO));
    }


    @Test
    void newSaving_invalidInterestRateNewSavingAccount_Create() {
        SavingsDTO savingsDTO = new SavingsDTO(new BigDecimal("9888"), nino.getId(), null, "123", new BigDecimal("1000"), new BigDecimal(0.8));
        assertThrows(ResponseStatusException.class, ()-> savingsService.newSaving(savingsDTO));
    }

}