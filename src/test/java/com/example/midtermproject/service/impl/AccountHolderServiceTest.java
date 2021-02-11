package com.example.midtermproject.service.impl;

import com.example.midtermproject.controller.dto.AccountHolderDTO;
import com.example.midtermproject.model.Users.AccountHolder;
import com.example.midtermproject.repository.AccountHolderRepository;
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
    private AccountHolderService accountHolderService;

    @Autowired
    private AccountHolderRepository accountHolderRepository;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
        accountHolderRepository.deleteAll();
    }

    //TODO NO FUNSIONA
    @Test
    void newAccountHolder() {
        //    public AccountHolderDTO(String username, String password, String name, LocalDate birth, String primaryStreet, String primaryCity, String primaryPostalCode, String mailingStreet, String mailingCity, String mailingPostalCode) {
        AccountHolderDTO accountHolderDTO = new AccountHolderDTO("Willirex", "123", "Willi", LocalDate.of(1992,5,5), "Soledad", "Madrid", "9874", "cuaderno", "cuenca", "9872");
        AccountHolder accountHolder = accountHolderService.newAccountHolder(accountHolderDTO);

        assertEquals("Willi", accountHolderRepository.findByName("Willi").get(0).getName());
    }
}