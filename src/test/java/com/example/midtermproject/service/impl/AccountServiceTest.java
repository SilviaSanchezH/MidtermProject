package com.example.midtermproject.service.impl;

import com.example.midtermproject.controller.dto.BalanceDTO;
import com.example.midtermproject.model.Accounts.Account;
import com.example.midtermproject.model.Accounts.CreditCard;
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

    public AccountHolder accountHolder;
    public AccountHolder accountHolder2;
    public AccountHolder accountHolder3;
    public Savings savings;
    public CreditCard creditCard;

    @BeforeEach
    void setUp() {
        Address primaryAddress = new Address("castellana", "madrid", "28888");
        Address secondaryAddress = new Address("goya", "madrid", "28976");
        accountHolder = new AccountHolder("Paco", "123", "Paco", LocalDate.of(1950, 9,8), primaryAddress, secondaryAddress);
        accountHolder2 = new AccountHolder("Paca", "123", "Paca", LocalDate.of(1955, 6,8), primaryAddress, secondaryAddress);
        accountHolder3 = new AccountHolder("Mercedes", "123", "Mercedes", LocalDate.of(1950, 9,8), primaryAddress, secondaryAddress);
        accountHolderRepository.saveAll(List.of(accountHolder, accountHolder2));

        savings = new Savings (new Money(new BigDecimal("78000")) , accountHolder, accountHolder2, "owo", new Money(new BigDecimal("200")), new BigDecimal("0.2"));
        creditCard = new CreditCard(new Money(new BigDecimal("30000")), accountHolder2, accountHolder, new Money(new BigDecimal("60000")), new BigDecimal("0.1"));
        accountRepository.saveAll(List.of(savings, creditCard));
    }

    @AfterEach
    void tearDown() {
        accountRepository.deleteAll();
        accountHolderRepository.deleteAll();
    }

    @Test
    void updateBalance_updateBalanceAccount_update() {
        BigDecimal newBalance = new BigDecimal("8");
        Integer accountId = savings.getId();
        accountService.updateBalance(accountId, newBalance, "USD");
        assertEquals(newBalance.compareTo(accountRepository.findById(accountId).get().getBalance().getAmount()), 0);
    }

    @Test
    void updateBalance_notFoundAccount_notUpdateBalance() {
        BigDecimal newBalance = new BigDecimal("8");
        Integer accountId = savings.getId();
        assertThrows(ResponseStatusException.class, () -> accountService.updateBalance(accountId+25, newBalance, "USD"));
    }

    @Test
    void getAccount_validAccount_account() {
        Integer accountId = savings.getId();
        String usernarme = accountHolder.getUsername();
        assertEquals("Paco", accountService.getAccount(accountId, usernarme).getPrimaryOwner().getName());
    }

    @Test
    void getAccount_addInterestSavingAccount_account() {
        Integer accountId = savings.getId();
        String usernarme = accountHolder.getUsername();

        savings.setCreatedAt(LocalDate.of(2020,2,6));
        accountRepository.save(savings);
        accountService.getAccount(accountId, usernarme);

        assertTrue((new BigDecimal("93600")).compareTo(accountRepository.findByPrimaryOwner(accountHolder).get(0).getBalance().getAmount())==0);
    }

    @Test
    void getAccount_addInterestCreditCardAccount_account() {
        Integer accountId = creditCard.getId();
        String usernarme = accountHolder.getUsername();

        creditCard.setCreatedAt(LocalDate.of(2021,1,6));
        accountRepository.save(creditCard);
        accountService.getAccount(accountId, usernarme);

        assertTrue((new BigDecimal("30249.90")).compareTo(accountRepository.findByPrimaryOwner(accountHolder2).get(0).getBalance().getAmount())==0);
    }

    @Test
    void getAccount_validAccountWithSecondaryOwner_account() {
        Integer accountId = savings.getId();
        String usernarme = accountHolder2.getUsername();
        assertEquals("Paca", accountService.getAccount(accountId, usernarme).getSecondaryOwner().getName());
    }

    @Test
    void getAccount_notValidAccountId_notAccount() {
        Integer accountId = savings.getId();
        String usernarme = accountHolder.getUsername();
        assertThrows(ResponseStatusException.class, ()-> accountService.getAccount(accountId+56, usernarme));
    }

    @Test
    void getAccount_notValidUsernameAccount_notAccount() {
        Integer accountId = savings.getId();
        String usernarme = accountHolder3.getUsername();
        assertThrows(ResponseStatusException.class, ()-> accountService.getAccount(accountId, usernarme));
    }

    @Test
    void getAccountBalance_ValidAccount_account() {
        Integer accountId = savings.getId();
        BalanceDTO result = accountService.getAccountBalance(accountId, accountHolder.getUsername());
        BigDecimal balance = savings.getBalance().getAmount();

        assertEquals(balance.compareTo(result.getAmount()), 0);
    }

    @Test
    void getAccountBalance_ValidAccountWithSecondaryOwner_account() {
        Integer accountId = savings.getId();
        BalanceDTO result = accountService.getAccountBalance(accountId, accountHolder2.getUsername());
        BigDecimal balance = savings.getBalance().getAmount();

        assertEquals(balance.compareTo(result.getAmount()), 0);
    }

    @Test
    void getAccountBalance_addInterestSavingAccount_account() {
        Integer accountId = savings.getId();
        String usernarme = accountHolder.getUsername();

        savings.setCreatedAt(LocalDate.of(2020,2,6));
        accountRepository.save(savings);
        accountService.getAccountBalance(accountId, usernarme);

        assertTrue((new BigDecimal("93600")).compareTo(accountRepository.findByPrimaryOwner(accountHolder).get(0).getBalance().getAmount())==0);
    }

    @Test
    void getAccountBalance_addInterestCreditCardAccount_account() {
        Integer accountId = creditCard.getId();
        String usernarme = accountHolder.getUsername();

        creditCard.setCreatedAt(LocalDate.of(2021,1,6));
        accountRepository.save(creditCard);
        accountService.getAccountBalance(accountId, usernarme);

        assertTrue((new BigDecimal("30249.90")).compareTo(accountRepository.findByPrimaryOwner(accountHolder2).get(0).getBalance().getAmount())==0);
    }

    @Test
    void getAccountBalance_notValidAccountId_notAccount() {
        Integer accountId = savings.getId();
        assertThrows(ResponseStatusException.class, ()-> accountService.getAccountBalance(accountId+56, accountHolder.getUsername()));
    }

    @Test
    void getAccountBalance_notValidUserName_notAccount() {
        Integer accountId = savings.getId();
        assertThrows(ResponseStatusException.class, ()-> accountService.getAccountBalance(accountId, accountHolder3.getUsername()));
    }

}