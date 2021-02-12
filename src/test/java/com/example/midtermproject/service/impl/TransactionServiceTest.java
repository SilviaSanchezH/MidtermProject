package com.example.midtermproject.service.impl;

import com.example.midtermproject.controller.dto.ThirdPartyTransactionDTO;
import com.example.midtermproject.controller.dto.TransactionDTO;
import com.example.midtermproject.enums.Status;
import com.example.midtermproject.model.Accounts.Savings;
import com.example.midtermproject.model.Users.AccountHolder;
import com.example.midtermproject.model.Users.ThirdParty;
import com.example.midtermproject.model.shared.Address;
import com.example.midtermproject.model.shared.Money;
import com.example.midtermproject.repository.AccountHolderRepository;
import com.example.midtermproject.repository.AccountRepository;
import com.example.midtermproject.repository.ThirdPartyRepository;
import com.example.midtermproject.repository.TransactionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TransactionServiceTest {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ThirdPartyRepository thirdPartyRepository;

    @Autowired
    private AccountHolderRepository accountHolderRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private AccountService accountService;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AccountHolder paco;
    public AccountHolder paca;
    public AccountHolder willirex;
    public Savings savings;
    public Savings savings2;
    public ThirdParty thirdParty;
    public ThirdParty thirdParty2;

    private final CountDownLatch waiter = new CountDownLatch(1);

    @BeforeEach
    void setUp() {
        Address primaryAddress = new Address("castellana", "madrid", "28888");
        Address secondaryAddress = new Address("goya", "madrid", "28976");
        paco = new AccountHolder("Paco", "123", "Paco", LocalDate.of(1950, 9,8), primaryAddress, secondaryAddress);
        paca  = new AccountHolder("Paca", "123", "Paca", LocalDate.of(1955, 6,8), primaryAddress, secondaryAddress);
        willirex = new AccountHolder("Willirex", "123", "Willi", LocalDate.of(1992,8,4), primaryAddress, secondaryAddress);
        accountHolderRepository.saveAll(List.of(paco, paca));
        savings = new Savings (new Money(new BigDecimal("78000")) , paco, paca, "owo", new Money(new BigDecimal("200")), new BigDecimal("0.2"));
        savings2 = new Savings (new Money(new BigDecimal("4000")) , paca, paco, "owo", new Money(new BigDecimal("200")), new BigDecimal("0.2"));

        accountRepository.saveAll(List.of(savings, savings2));

        String password = passwordEncoder.encode("123");
        thirdParty = new ThirdParty("Pacathird", password);
        thirdPartyRepository.save(thirdParty);

        String password2 = passwordEncoder.encode("123");
        thirdParty2 = new ThirdParty("Ibai", password2);
        thirdPartyRepository.save(thirdParty2);
    }

    @AfterEach
    void tearDown() {
        transactionRepository.deleteAll();
        accountRepository.deleteAll();
        accountHolderRepository.deleteAll();
    }

    @Test
    void newTransaction_localTransaction_transaction() {

        Integer originAccountId = savings.getId();
        Integer destinationAccountId = savings2.getId();
        String username = paco.getUsername();
        BigDecimal quantity = new BigDecimal("500");

        TransactionDTO transactionDTO = new TransactionDTO(originAccountId, destinationAccountId, paca.getName(), quantity, "USD");
        transactionService.newTransaction(transactionDTO, username);

        assertEquals((new BigDecimal("77500")).compareTo(accountRepository.findByPrimaryOwner(paco).get(0).getBalance().getAmount()), 0);
    }


    @Test
    void newTransaction_localTransactionWithSecondaryOwner_transaction() {

        Integer originAccountId = savings.getId();
        Integer destinationAccountId = savings2.getId();
        String username = paca.getUsername();
        BigDecimal quantity = new BigDecimal("500");

        TransactionDTO transactionDTO = new TransactionDTO(originAccountId, destinationAccountId, paca.getName(), quantity, "USD");
        transactionService.newTransaction(transactionDTO, username);

        assertEquals((new BigDecimal("77500")).compareTo(accountRepository.findByPrimaryOwner(paco).get(0).getBalance().getAmount()), 0);
    }


    @Test
    void newTransaction_localTransactionApplyPenaltyFeeSaving_transaction() {

        Integer originAccountId = savings.getId();
        Integer destinationAccountId = savings2.getId();
        String username = paco.getUsername();
        BigDecimal quantity = new BigDecimal("77900");

        TransactionDTO transactionDTO = new TransactionDTO(originAccountId, destinationAccountId, paca.getName(), quantity, "USD");
        transactionService.newTransaction(transactionDTO, username);

        assertEquals((new BigDecimal("60")).compareTo(accountRepository.findByPrimaryOwner(paco).get(0).getBalance().getAmount()), 0);

    }


    @Test
    void newTransaction_checkStatusLocalTransactionWithSecondaryOwner_notTransaction() {

        Integer originAccountId = savings.getId();
        Integer destinationAccountId = savings2.getId();
        String username = paca.getUsername();
        BigDecimal quantity = new BigDecimal("500");

        savings.setStatus(Status.FROZEN);

        accountRepository.save(savings);

        TransactionDTO transactionDTO = new TransactionDTO(originAccountId, destinationAccountId, paca.getName(), quantity, "USD");

        assertThrows(ResponseStatusException.class, ()-> transactionService.newTransaction(transactionDTO, username));
    }


    @Test
    void newTransaction_localTransactionWithInvalidDestinationAccount_notTransaction() {

        Integer originAccountId = savings.getId();
        Integer destinationAccountId = savings2.getId();
        String username = paco.getUsername();
        BigDecimal quantity = new BigDecimal("500");

        TransactionDTO transactionDTO = new TransactionDTO(originAccountId, destinationAccountId +987, paca.getName(), quantity, "USD");

        assertThrows(ResponseStatusException.class, ()-> transactionService.newTransaction(transactionDTO, username));
    }


    @Test
    void newTransaction_localTransactionWithInvalidOriginAccount_notTransaction() {

        Integer originAccountId = savings.getId();
        Integer destinationAccountId = savings2.getId();
        String username = paco.getUsername();
        BigDecimal quantity = new BigDecimal("500");

        TransactionDTO transactionDTO = new TransactionDTO(originAccountId +89, destinationAccountId, paca.getName(), quantity, "USD");

        assertThrows(ResponseStatusException.class, ()-> transactionService.newTransaction(transactionDTO, username));
    }


    @Test
    void newTransaction_localTransactionWithNonExistUsername_notTransaction() {

        Integer originAccountId = savings.getId();
        Integer destinationAccountId = savings2.getId();
        BigDecimal quantity = new BigDecimal("500");

        TransactionDTO transactionDTO = new TransactionDTO(originAccountId, destinationAccountId, paca.getName(), quantity, "USD");

        assertThrows(ResponseStatusException.class, ()-> transactionService.newTransaction(transactionDTO, "Rafael"));
    }


    @Test
    void newTransaction_localTransactionWithInvalidUsername_notTransaction() {

        Integer originAccountId = savings.getId();
        Integer destinationAccountId = savings2.getId();
        String username = willirex.getUsername();
        BigDecimal quantity = new BigDecimal("500");

        TransactionDTO transactionDTO = new TransactionDTO(originAccountId, destinationAccountId, paca.getName(), quantity, "USD");

        assertThrows(ResponseStatusException.class, ()-> transactionService.newTransaction(transactionDTO, username));
    }


    @Test
    void newTransaction_checkSufficientFundsLocalTransaction_notTransaction() {

        Integer originAccountId = savings.getId();
        Integer destinationAccountId = savings2.getId();
        String username = paco.getUsername();
        BigDecimal quantity = new BigDecimal("100000");

        TransactionDTO transactionDTO = new TransactionDTO(originAccountId, destinationAccountId, paca.getName(), quantity, "USD");

        assertThrows(ResponseStatusException.class, ()-> transactionService.newTransaction(transactionDTO, username));
    }


    @Test
    void newTransaction_checkTwoTransactionIn1Second_notTransaction() throws InterruptedException {

        Integer originAccountId = savings.getId();
        Integer destinationAccountId = savings2.getId();
        String username = paco.getUsername();
        BigDecimal quantity = new BigDecimal("500");

        TransactionDTO transactionDTO = new TransactionDTO(originAccountId, destinationAccountId, paca.getName(), quantity, "USD");
        TransactionDTO transactionDTO2 = new TransactionDTO(originAccountId, destinationAccountId, paca.getName(), new BigDecimal("2"), "USD");
        transactionService.newTransaction(transactionDTO, username);

        waiter.await(100, TimeUnit.MILLISECONDS);

        assertThrows(ResponseStatusException.class, ()-> transactionService.newTransaction(transactionDTO2, username));
    }


    @Test
    void newTransaction_checkMaxAmountFraudLocalTransaction_notTransaction() throws InterruptedException {
        Integer originAccountId = savings.getId();
        Integer destinationAccountId = savings2.getId();
        String username = paco.getUsername();
        BigDecimal quantity = new BigDecimal("500");

        TransactionDTO transactionDTO = new TransactionDTO(originAccountId, destinationAccountId, paca.getName(), quantity, "USD");
        transactionService.newTransaction(transactionDTO, username);

        waiter.await(1500, TimeUnit.MILLISECONDS);

        assertThrows(ResponseStatusException.class, ()-> transactionService.newTransaction(transactionDTO, username));
    }


    @Test
    void newTransaction_localTransactionToThirdParty_transaction() {

        Integer originAccountId = savings.getId();
        String destinationThirdPartyName = thirdParty.getName();
        Integer thirdPartyDestinationId = thirdParty.getId();
        String username = paco.getUsername();
        BigDecimal quantity = new BigDecimal("500");

        TransactionDTO transactionDTO = new TransactionDTO(originAccountId, destinationThirdPartyName, thirdPartyDestinationId, quantity, "USD");
        transactionService.newTransaction(transactionDTO, username);

        assertEquals((new BigDecimal("77500")).compareTo(accountRepository.findByPrimaryOwner(paco).get(0).getBalance().getAmount()), 0);
    }


    @Test
    void newTransaction_invalidThirdPartyIdLocalTransactionToThirdParty_transaction() {

        Integer originAccountId = savings.getId();
        String destinationThirdPartyName = thirdParty.getName();
        Integer thirdPartyDestinationId = thirdParty.getId();
        String username = paco.getUsername();
        BigDecimal quantity = new BigDecimal("500");

        TransactionDTO transactionDTO = new TransactionDTO(originAccountId, destinationThirdPartyName, thirdPartyDestinationId+89, quantity, "USD");
        assertThrows(ResponseStatusException.class, ()-> transactionService.newTransaction(transactionDTO, username));
    }


    @Test
    void newTransaction_invalidNameThirdPartyLocalTransactionToThirdParty_notTransaction() {

        Integer originAccountId = savings.getId();
        String destinationThirdPartyName = thirdParty2.getName();
        Integer thirdPartyDestinationId = thirdParty.getId();
        String username = paco.getUsername();
        BigDecimal quantity = new BigDecimal("500");

        TransactionDTO transactionDTO = new TransactionDTO(originAccountId, destinationThirdPartyName, thirdPartyDestinationId+89, quantity, "USD");
        assertThrows(ResponseStatusException.class, ()-> transactionService.newTransaction(transactionDTO, username));
    }


    @Test
    void newThirdPartyTransaction_transactionToLocalAccountFromThirdParty_transaction() {

        Integer destinationAccountId = savings2.getId();
        String secretKey = savings2.getSecretKey();
        BigDecimal quantity = new BigDecimal("500");
        Integer thirdPartyId = thirdParty.getId();

        ThirdPartyTransactionDTO thirdPartyTransactionDTO = new ThirdPartyTransactionDTO(quantity, destinationAccountId, secretKey, thirdPartyId);
        transactionService.newFromThirdPartyTransaction(thirdPartyTransactionDTO, "123");

        assertEquals((new BigDecimal("4500")).compareTo(accountRepository.findByPrimaryOwner(paca).get(0).getBalance().getAmount()), 0);

    }

    //Not valid destination account id
    @Test
    void newThirdPartyTransaction_invalidAccountIdTransactionToLocalAccountFromThirdParty_notTransaction() {

        Integer destinationAccountId = savings2.getId();
        String secretKey = savings2.getSecretKey();
        BigDecimal quantity = new BigDecimal("500");
        Integer thirdPartyId = thirdParty.getId();

        ThirdPartyTransactionDTO thirdPartyTransactionDTO = new ThirdPartyTransactionDTO(quantity, destinationAccountId+7, secretKey, thirdPartyId);

        assertThrows(ResponseStatusException.class, ()-> transactionService.newFromThirdPartyTransaction(thirdPartyTransactionDTO, "123"));
    }


    @Test
    void newThirdPartyTransaction_invalidHashedKeyTransactionToLocalAccountFromThirdParty_notTransaction() {

        Integer destinationAccountId = savings2.getId();
        String secretKey = savings2.getSecretKey();
        BigDecimal quantity = new BigDecimal("500");
        Integer thirdPartyId = thirdParty.getId();

        ThirdPartyTransactionDTO thirdPartyTransactionDTO = new ThirdPartyTransactionDTO(quantity, destinationAccountId, secretKey, thirdPartyId);

        assertThrows(ResponseStatusException.class, ()-> transactionService.newFromThirdPartyTransaction(thirdPartyTransactionDTO, "uwu"));
    }


    @Test
    void newThirdPartyTransaction_invalidThirdPartyIdTransactionToLocalAccountFromThirdParty_notTransaction() {

        Integer destinationAccountId = savings2.getId();
        String secretKey = savings2.getSecretKey();
        BigDecimal quantity = new BigDecimal("500");
        Integer thirdPartyId = thirdParty.getId();

        ThirdPartyTransactionDTO thirdPartyTransactionDTO = new ThirdPartyTransactionDTO(quantity, destinationAccountId, secretKey, thirdPartyId+9);

        assertThrows(ResponseStatusException.class, ()-> transactionService.newFromThirdPartyTransaction(thirdPartyTransactionDTO, "123"));
    }

}