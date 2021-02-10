package com.example.midtermproject.service.impl;

import com.example.midtermproject.controller.dto.TransactionDTO;
import com.example.midtermproject.model.Accounts.Account;
import com.example.midtermproject.model.Accounts.Savings;
import com.example.midtermproject.model.Accounts.Transaction;
import com.example.midtermproject.model.Users.AccountHolder;
import com.example.midtermproject.model.shared.Address;
import com.example.midtermproject.model.shared.Money;
import com.example.midtermproject.repository.AccountHolderRepository;
import com.example.midtermproject.repository.AccountRepository;
import com.example.midtermproject.repository.TransactionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TransactionServiceTest {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountHolderRepository accountHolderRepository;

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    private TransactionService transactionService;

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
        Savings savings2 = new Savings (new Money(new BigDecimal("4000")) , accountHolder2, accountHolder, "owo", new Money(new BigDecimal("200")), new BigDecimal("0.2"));

        accountRepository.saveAll(List.of(savings, savings2));
    }

    @AfterEach
    void tearDown() {
        transactionRepository.deleteAll();
        accountRepository.deleteAll();
        accountHolderRepository.deleteAll();
    }

    // TODO: ESTA MIERDA NO FUNCIONA
    @Test
    void newTransaction() {
        AccountHolder paco = accountHolderRepository.findByName("Paco").get(0);
        AccountHolder paca = accountHolderRepository.findByName("Paca").get(0);

        Integer originAccountId = accountRepository.findByPrimaryOwner(paco).get(0).getId();
        Integer destinationAccountId = accountRepository.findByPrimaryOwner(paca).get(0).getId();
        String username = paco.getUsername();

        BigDecimal quantity = new BigDecimal("500");
        TransactionDTO transactionDTO = new TransactionDTO(originAccountId, destinationAccountId, paca.getName(), quantity, "USD");
        Transaction transaction = transactionService.newTransaction(transactionDTO, username);

        //transactionRepository.save(transaction);

        Account account = accountRepository.findById(originAccountId).get();

        System.out.println("................. " + account.getBalance().getAmount());
        System.out.println(transaction.getValue());
        assertSame(new BigDecimal("77500"), account.getBalance().getAmount() );

    }

    @Test
    void newFromThirdPartyTransaction() {

    }
}