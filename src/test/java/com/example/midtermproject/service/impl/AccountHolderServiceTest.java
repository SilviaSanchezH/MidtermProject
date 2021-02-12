package com.example.midtermproject.service.impl;

import com.example.midtermproject.controller.dto.AccountHolderDTO;
import com.example.midtermproject.model.Users.AccountHolder;
import com.example.midtermproject.repository.AccountHolderRepository;
import com.example.midtermproject.repository.AccountRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AccountHolderServiceTest {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountHolderService accountHolderService;

    @Autowired
    private AccountHolderRepository accountHolderRepository;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
        accountRepository.deleteAll();
        accountHolderRepository.deleteAll();
    }

    @Test
    void newAccountHolder_validAccountHolder_accountHolder() {
        AccountHolderDTO accountHolderDTO = new AccountHolderDTO("Willirex", "123", "Willi", LocalDate.of(1992,5,5), "Soledad", "Madrid", "9874", "cuaderno", "cuenca", "9872");
        accountHolderService.newAccountHolder(accountHolderDTO);

        assertEquals("Willi", accountHolderRepository.findByName("Willi").get(0).getName());
    }

    @Test
    void newAccountHolder_validAccountHolderWithoutSecondaryOwner_accountHolder() {
        AccountHolderDTO accountHolderDTO = new AccountHolderDTO("Willirex", "123", "Willi", LocalDate.of(1992,5,5), "Soledad", "Madrid", "9874", null, null, null);
        accountHolderService.newAccountHolder(accountHolderDTO);

        assertEquals("Willi", accountHolderRepository.findByName("Willi").get(0).getName());
    }
}