package com.example.midtermproject.service.impl;

import com.example.midtermproject.controller.dto.ThirdPartyTransactionDTO;
import com.example.midtermproject.controller.dto.TransactionDTO;
import com.example.midtermproject.enums.Status;
import com.example.midtermproject.model.Accounts.Checking;
import com.example.midtermproject.model.Accounts.CreditCard;
import com.example.midtermproject.model.Accounts.Savings;
import com.example.midtermproject.model.Accounts.StudentChecking;
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
    public AccountHolder mercedes;
    public AccountHolder bebe;
    public Savings savings;
    public Savings savings2;
    public CreditCard creditCard;
    public Checking checking;
    public ThirdParty thirdParty;
    public ThirdParty thirdParty2;
    public StudentChecking studentChecking;

    private final CountDownLatch waiter = new CountDownLatch(1);

    @BeforeEach
    void setUp() {
        Address primaryAddress = new Address("castellana", "madrid", "28888");
        Address secondaryAddress = new Address("goya", "madrid", "28976");
        paco = new AccountHolder("Paco", "123", "Paco", LocalDate.of(1950, 9,8), primaryAddress, secondaryAddress);
        paca  = new AccountHolder("Paca", "123", "Paca", LocalDate.of(1955, 6,8), primaryAddress, secondaryAddress);
        willirex = new AccountHolder("Willirex", "123", "Willi", LocalDate.of(1992,8,4), primaryAddress, secondaryAddress);
        mercedes = new AccountHolder("Mercedes", "123", "Mercedes", LocalDate.of(1950, 9,8), primaryAddress, secondaryAddress);
        bebe = new AccountHolder("Jaime", "123", "Jaime", LocalDate.of(2000,8,4), primaryAddress, secondaryAddress);

        accountHolderRepository.saveAll(List.of(paco, paca, willirex, mercedes, bebe));

        savings = new Savings (new Money(new BigDecimal("78000")) , paco, paca, "owo", new Money(new BigDecimal("200")), new BigDecimal("0.2"));
        savings2 = new Savings (new Money(new BigDecimal("4000")) , paca, paco, "owo", new Money(new BigDecimal("200")), new BigDecimal("0.2"));
        creditCard = new CreditCard(new Money(new BigDecimal("30000")), willirex, null, new Money(new BigDecimal("60000")), new BigDecimal("0.1"));
        checking = new Checking(new Money(new BigDecimal("78000")), mercedes, null, "owo");
        studentChecking = new StudentChecking(new Money(new BigDecimal("5000")), bebe, null, "owo");

        accountRepository.saveAll(List.of(savings, savings2, creditCard, checking, studentChecking));

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
    void newTransaction_localTransactionFromSavingsAccount_transaction() {

        Integer originAccountId = savings.getId();
        Integer destinationAccountId = savings2.getId();
        String username = paco.getUsername();
        BigDecimal quantity = new BigDecimal("500");

        TransactionDTO transactionDTO = new TransactionDTO(originAccountId, destinationAccountId, paca.getName(), quantity, "USD");
        transactionService.newTransaction(transactionDTO, username);

        assertEquals((new BigDecimal("77500")).compareTo(accountRepository.findByPrimaryOwner(paco).get(0).getBalance().getAmount()), 0);
        assertEquals((new BigDecimal("4500")).compareTo(accountRepository.findByPrimaryOwner(paca).get(0).getBalance().getAmount()), 0);
    }

    @Test
    void newTransaction_localTransactionFromCheckingAccount_transaction() {

        Integer originAccountId = checking.getId();
        Integer destinationAccountId = creditCard.getId();
        String username = mercedes.getUsername();
        BigDecimal quantity = new BigDecimal("500");

        TransactionDTO transactionDTO = new TransactionDTO(originAccountId, destinationAccountId, willirex.getName(), quantity, "USD");
        transactionService.newTransaction(transactionDTO, username);

        assertEquals((new BigDecimal("77500")).compareTo(accountRepository.findByPrimaryOwner(mercedes).get(0).getBalance().getAmount()), 0);
        assertEquals((new BigDecimal("30500")).compareTo(accountRepository.findByPrimaryOwner(willirex).get(0).getBalance().getAmount()), 0);
    }

    @Test
    void newTransaction_localTransactionFromStudentAccount_transaction() {

        Integer originAccountId = studentChecking.getId();
        Integer destinationAccountId = creditCard.getId();
        String username = bebe.getUsername();
        BigDecimal quantity = new BigDecimal("500");

        TransactionDTO transactionDTO = new TransactionDTO(originAccountId, destinationAccountId, willirex.getName(), quantity, "USD");
        transactionService.newTransaction(transactionDTO, username);

        assertEquals((new BigDecimal("4500")).compareTo(accountRepository.findByPrimaryOwner(bebe).get(0).getBalance().getAmount()), 0);
        assertEquals((new BigDecimal("30500")).compareTo(accountRepository.findByPrimaryOwner(willirex).get(0).getBalance().getAmount()), 0);
    }

    @Test
    void newTransaction_localTransactionFromCreditAccount_transaction() {

        Integer originAccountId = creditCard.getId();
        Integer destinationAccountId = savings2.getId();
        String username = willirex.getUsername();
        BigDecimal quantity = new BigDecimal("500");

        TransactionDTO transactionDTO = new TransactionDTO(originAccountId, destinationAccountId, paca.getName(), quantity, "USD");
        transactionService.newTransaction(transactionDTO, username);

        assertEquals((new BigDecimal("29500")).compareTo(accountRepository.findByPrimaryOwner(willirex).get(0).getBalance().getAmount()), 0);
        assertEquals((new BigDecimal("4500")).compareTo(accountRepository.findByPrimaryOwner(paca).get(0).getBalance().getAmount()), 0);
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
        assertEquals((new BigDecimal("4500")).compareTo(accountRepository.findByPrimaryOwner(paca).get(0).getBalance().getAmount()), 0);
    }

    @Test
    void newTransaction_invalidDestinationOwnerNameLocalTransaction_transaction() {

        Integer originAccountId = savings.getId();
        Integer destinationAccountId = savings2.getId();
        String username = paco.getUsername();
        BigDecimal quantity = new BigDecimal("500");

        TransactionDTO transactionDTO = new TransactionDTO(originAccountId, destinationAccountId, "Camela", quantity, "USD");

        assertThrows(ResponseStatusException.class, ()-> transactionService.newTransaction(transactionDTO, username));
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
    void newTransaction_localTransactionApplyPenaltyFeeChecking_transaction() {

        Integer originAccountId = checking.getId();
        Integer destinationAccountId = savings2.getId();
        String username = mercedes.getUsername();
        BigDecimal quantity = new BigDecimal("77900");

        TransactionDTO transactionDTO = new TransactionDTO(originAccountId, destinationAccountId, paca.getName(), quantity, "USD");
        transactionService.newTransaction(transactionDTO, username);

        assertEquals((new BigDecimal("60")).compareTo(accountRepository.findByPrimaryOwner(mercedes).get(0).getBalance().getAmount()), 0);
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

        waiter.await(100000, TimeUnit.NANOSECONDS);

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
    void newTransaction_localTransactionToThirdPartyFromSavingAccount_transaction() {

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
    void newTransaction_localTransactionToThirdPartyFromCheckingAccount_transaction() {

        Integer originAccountId = checking.getId();
        String destinationThirdPartyName = thirdParty.getName();
        Integer thirdPartyDestinationId = thirdParty.getId();
        String username = mercedes.getUsername();
        BigDecimal quantity = new BigDecimal("500");

        TransactionDTO transactionDTO = new TransactionDTO(originAccountId, destinationThirdPartyName, thirdPartyDestinationId, quantity, "USD");
        transactionService.newTransaction(transactionDTO, username);

        assertEquals((new BigDecimal("77500")).compareTo(accountRepository.findByPrimaryOwner(mercedes).get(0).getBalance().getAmount()), 0);
    }

    @Test
    void newTransaction_localTransactionToThirdPartyFromStudentCheckingAccount_transaction() {

        Integer originAccountId = studentChecking.getId();
        String destinationThirdPartyName = thirdParty.getName();
        Integer thirdPartyDestinationId = thirdParty.getId();
        String username = bebe.getUsername();
        BigDecimal quantity = new BigDecimal("500");

        TransactionDTO transactionDTO = new TransactionDTO(originAccountId, destinationThirdPartyName, thirdPartyDestinationId, quantity, "USD");
        transactionService.newTransaction(transactionDTO, username);

        assertEquals((new BigDecimal("4500")).compareTo(accountRepository.findByPrimaryOwner(bebe).get(0).getBalance().getAmount()), 0);
    }

    @Test
    void newTransaction_localTransactionToThirdPartyFromCreditCardAccount_transaction() {

        Integer originAccountId = creditCard.getId();
        String destinationThirdPartyName = thirdParty.getName();
        Integer thirdPartyDestinationId = thirdParty.getId();
        String username = willirex.getUsername();
        BigDecimal quantity = new BigDecimal("500");

        TransactionDTO transactionDTO = new TransactionDTO(originAccountId, destinationThirdPartyName, thirdPartyDestinationId, quantity, "USD");
        transactionService.newTransaction(transactionDTO, username);

        assertEquals((new BigDecimal("29500")).compareTo(accountRepository.findByPrimaryOwner(willirex).get(0).getBalance().getAmount()), 0);
    }



    @Test
    void newTransaction_invalidDestinationOwnerNameLocalTransactionToThirdParty_transaction() {

        Integer originAccountId = savings.getId();
        Integer thirdPartyDestinationId = thirdParty.getId();
        String username = paco.getUsername();
        BigDecimal quantity = new BigDecimal("500");

        TransactionDTO transactionDTO = new TransactionDTO(originAccountId, "Camela", thirdPartyDestinationId, quantity, "USD");

        assertThrows(ResponseStatusException.class, ()-> transactionService.newTransaction(transactionDTO, username));
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
    void newThirdPartyTransaction_transactionToSavingAccountFromThirdParty_transaction() {

        Integer destinationAccountId = savings2.getId();
        String secretKey = savings2.getSecretKey();
        BigDecimal quantity = new BigDecimal("500");
        Integer thirdPartyId = thirdParty.getId();

        ThirdPartyTransactionDTO thirdPartyTransactionDTO = new ThirdPartyTransactionDTO(quantity, destinationAccountId, secretKey, thirdPartyId);
        transactionService.newFromThirdPartyTransaction(thirdPartyTransactionDTO, "123");

        assertEquals((new BigDecimal("4500")).compareTo(accountRepository.findByPrimaryOwner(paca).get(0).getBalance().getAmount()), 0);
    }

    @Test
    void newThirdPartyTransaction_transactionToCheckingAccountFromThirdParty_transaction() {

        Integer destinationAccountId = checking.getId();
        String secretKey = checking.getSecretKey();
        BigDecimal quantity = new BigDecimal("500");
        Integer thirdPartyId = thirdParty.getId();

        ThirdPartyTransactionDTO thirdPartyTransactionDTO = new ThirdPartyTransactionDTO(quantity, destinationAccountId, secretKey, thirdPartyId);
        transactionService.newFromThirdPartyTransaction(thirdPartyTransactionDTO, "123");

        assertEquals((new BigDecimal("78500")).compareTo(accountRepository.findByPrimaryOwner(mercedes).get(0).getBalance().getAmount()), 0);
    }

    @Test
    void newThirdPartyTransaction_transactionToStudentCheckingAccountFromThirdParty_transaction() {

        Integer destinationAccountId = studentChecking.getId();
        String secretKey = studentChecking.getSecretKey();
        BigDecimal quantity = new BigDecimal("500");
        Integer thirdPartyId = thirdParty.getId();

        ThirdPartyTransactionDTO thirdPartyTransactionDTO = new ThirdPartyTransactionDTO(quantity, destinationAccountId, secretKey, thirdPartyId);
        transactionService.newFromThirdPartyTransaction(thirdPartyTransactionDTO, "123");

        assertEquals((new BigDecimal("5500")).compareTo(accountRepository.findByPrimaryOwner(bebe).get(0).getBalance().getAmount()), 0);
    }



    @Test
    void newThirdPartyTransaction_invalidSecretKeyTransactionToLocalAccountFromThirdParty_transaction() {

        Integer destinationAccountId = savings2.getId();
        String secretKey = savings2.getSecretKey();
        BigDecimal quantity = new BigDecimal("500");
        Integer thirdPartyId = thirdParty.getId();

        ThirdPartyTransactionDTO thirdPartyTransactionDTO = new ThirdPartyTransactionDTO(quantity, destinationAccountId, "bolo", thirdPartyId);

        assertThrows(ResponseStatusException.class, ()-> transactionService.newFromThirdPartyTransaction(thirdPartyTransactionDTO, "123"));
    }


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