package com.example.midtermproject.service.impl;

import com.example.midtermproject.controller.dto.CreditCardDTO;
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
class CreditCardServiceTest {

    @Autowired
    private AccountHolderRepository accountHolderRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CreditCardService creditCardService;

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
    void newCreditCard_newCreditCardAccount_Create() {
        CreditCardDTO creditCardDTO = new CreditCardDTO(new BigDecimal("9888"), nino.getId(), nina.getId(), new BigDecimal("2000"), new BigDecimal(0.1));
        creditCardService.newCreditCard(creditCardDTO);

        assertEquals("Nino", accountRepository.findByPrimaryOwner(nino).get(0).getPrimaryOwner().getName());
    }

    @Test
    void newCreditCard_newCreditCardAccountWithoutSecondaryOwner_Create() {
        CreditCardDTO creditCardDTO = new CreditCardDTO(new BigDecimal("9888"), nino.getId(), null, new BigDecimal("2000"), new BigDecimal(0.1));
        creditCardService.newCreditCard(creditCardDTO);

        assertEquals("Nino", accountRepository.findByPrimaryOwner(nino).get(0).getPrimaryOwner().getName());
    }

    @Test
    void newCreditCard_invalidCreditLimitCreditCardAccount_notCreate() {
        CreditCardDTO creditCardDTO = new CreditCardDTO(new BigDecimal("9888"), nino.getId(), nina.getId(), new BigDecimal("20"), new BigDecimal(0.1));
        assertThrows(ResponseStatusException.class, ()-> creditCardService.newCreditCard(creditCardDTO));
    }

    @Test
    void newCreditCard_invalidInterestRateCreditCardAccount_notCreate() {
        CreditCardDTO creditCardDTO = new CreditCardDTO(new BigDecimal("9888"), nino.getId(), nina.getId(), new BigDecimal("2000"), new BigDecimal(0.01));
        assertThrows(ResponseStatusException.class, ()-> creditCardService.newCreditCard(creditCardDTO));
    }

}