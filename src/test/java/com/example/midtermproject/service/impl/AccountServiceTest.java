package com.example.midtermproject.service.impl;

import com.example.midtermproject.controller.dto.BalanceDTO;
import com.example.midtermproject.model.Accounts.Account;
import com.example.midtermproject.model.Accounts.Savings;
import com.example.midtermproject.model.Users.AccountHolder;
import com.example.midtermproject.model.shared.Address;
import com.example.midtermproject.model.shared.Money;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class AccountServiceTest {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountHolderRepository accountHolderRepository;

    @Autowired
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        Address primaryAddress = new Address("castellana", "madrid", "28888");
        Address secondaryAddress = new Address("goya", "madrid", "28976");
        AccountHolder accountHolder = new AccountHolder("Paco", "123", "Paco", LocalDate.of(1950, 9,8), primaryAddress, secondaryAddress);
        AccountHolder accountHolder2 = new AccountHolder("Paca", "123", "Paca", LocalDate.of(1955, 6,8), primaryAddress, secondaryAddress);
        accountHolderRepository.saveAll(List.of(accountHolder, accountHolder2));
        Savings savings = new Savings (new Money(new BigDecimal("78000")) , accountHolder, accountHolder2, "owo", new Money(new BigDecimal("200")), new BigDecimal("0.2"));

        accountRepository.save(savings);
    }

    @AfterEach
    void tearDown() {
        accountRepository.deleteAll();
        accountHolderRepository.deleteAll();
    }

    @Test
    void updateBalance() {
        BigDecimal newBalance = new BigDecimal("8");
        AccountHolder accountHolder = accountHolderRepository.findByName("Paco").get(0);
        Integer accountId = accountRepository.findByPrimaryOwner(accountHolder).get(0).getId();
        accountService.updateBalance(accountId, newBalance, "USD");
        assertEquals(newBalance.compareTo(accountRepository.findById(accountId).get().getBalance().getAmount()), 0);
    }

    @Test
    void updateBalance_notFound() {
        BigDecimal newBalance = new BigDecimal("8");
        AccountHolder accountHolder = accountHolderRepository.findByName("Paco").get(0);
        Integer accountId = accountRepository.findByPrimaryOwner(accountHolder).get(0).getId();
        assertThrows(ResponseStatusException.class, () -> accountService.updateBalance(accountId+25, newBalance, "USD"));
    }

    @Test
    void getAccount() {
        AccountHolder accountHolder = accountHolderRepository.findByName("Paco").get(0);
        Integer accountId = accountRepository.findByPrimaryOwner(accountHolder).get(0).getId();
        String usernarme = accountHolder.getUsername();
        assertEquals("Paco", accountService.getAccount(accountId, usernarme).getPrimaryOwner().getName());
    }

    @Test
    void getAccount_notvalidId() {
        AccountHolder accountHolder = accountHolderRepository.findByName("Paco").get(0);
        Integer accountId = accountRepository.findByPrimaryOwner(accountHolder).get(0).getId();
        String usernarme = accountHolder.getUsername();
        assertThrows(ResponseStatusException.class, ()-> accountService.getAccount(accountId+56, usernarme));
    }

    @Test
    void getAccount_notValidUsername() {
        AccountHolder accountHolder = accountHolderRepository.findByName("Paco").get(0);
        Integer accountId = accountRepository.findByPrimaryOwner(accountHolder).get(0).getId();
        String usernarme = accountHolder.getUsername();
        assertThrows(ResponseStatusException.class, ()-> accountService.getAccount(accountId+56, "manolo"));
    }

    @Test
    void getAccountBalance() {
        AccountHolder accountHolder = accountHolderRepository.findByName("Paco").get(0);
        Integer accountId = accountRepository.findByPrimaryOwner(accountHolder).get(0).getId();
        BalanceDTO result = accountService.getAccountBalance(accountId, accountHolder.getUsername());

        Account account = accountRepository.findByPrimaryOwner(accountHolder).get(0);
        BigDecimal balance = account.getBalance().getAmount();

        assertEquals(balance.compareTo(result.getAmount()), 0);
    }

    @Test
    void getAccountBalance_notValidId() {
        AccountHolder accountHolder = accountHolderRepository.findByName("Paco").get(0);
        Integer accountId = accountRepository.findByPrimaryOwner(accountHolder).get(0).getId();

       assertThrows(ResponseStatusException.class, ()-> accountService.getAccountBalance(accountId+56, accountHolder.getUsername()));
    }

    @Test
    void getAccountBalance_notValidUserName() {
        AccountHolder accountHolder = accountHolderRepository.findByName("Paco").get(0);
        Integer accountId = accountRepository.findByPrimaryOwner(accountHolder).get(0).getId();
        assertThrows(ResponseStatusException.class, ()-> accountService.getAccountBalance(accountId, "Manolo"));
    }

}