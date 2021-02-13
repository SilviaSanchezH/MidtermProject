package com.example.midtermproject.controller.impl;

import com.example.midtermproject.controller.dto.ThirdPartyTransactionDTO;
import com.example.midtermproject.controller.dto.TransactionDTO;
import com.example.midtermproject.enums.Status;
import com.example.midtermproject.model.Accounts.Checking;
import com.example.midtermproject.model.Accounts.CreditCard;
import com.example.midtermproject.model.Accounts.Savings;
import com.example.midtermproject.model.Users.AccountHolder;
import com.example.midtermproject.model.Users.ThirdParty;
import com.example.midtermproject.model.shared.Address;
import com.example.midtermproject.model.shared.Money;
import com.example.midtermproject.repository.AccountHolderRepository;
import com.example.midtermproject.repository.AccountRepository;
import com.example.midtermproject.repository.ThirdPartyRepository;
import com.example.midtermproject.repository.TransactionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class TransactionControllerTest {


    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private AccountHolderRepository accountHolderRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ThirdPartyRepository thirdPartyRepository;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AccountHolder accountHolder;
    public AccountHolder accountHolder2;
    public AccountHolder accountHolder3;
    public AccountHolder accountHolder4;
    public Savings savings;
    public Savings savings2;
    public CreditCard creditCard;
    public Checking checking;
    public ThirdParty thirdParty;


    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        Address primaryAddress = new Address("castellana", "madrid", "28888");
        Address secondaryAddress = new Address("goya", "madrid", "28976");
        accountHolder = new AccountHolder("Berto", "123", "Berto", LocalDate.of(1950, 9,8), primaryAddress, secondaryAddress);
        accountHolder2 = new AccountHolder("Berta", "123", "Berta", LocalDate.of(1950, 9,8), primaryAddress, secondaryAddress);
        accountHolder3 = new AccountHolder("Mercedes", "123", "Mercedes", LocalDate.of(1950, 9,8), primaryAddress, secondaryAddress);
        accountHolder4 = new AccountHolder("Willirex", "123", "Willi", LocalDate.of(1992,8,4), primaryAddress, secondaryAddress);

        accountHolderRepository.saveAll(List.of(accountHolder,accountHolder2,accountHolder3, accountHolder4));

        savings = new Savings (new Money(new BigDecimal("10000")) , accountHolder, accountHolder2, "owo", new Money(new BigDecimal("200")), new BigDecimal("0.2"));
        savings2 = new Savings (new Money(new BigDecimal("4000")) , accountHolder2, accountHolder, "owo", new Money(new BigDecimal("200")), new BigDecimal("0.2"));
        creditCard = new CreditCard(new Money(new BigDecimal("30000")), accountHolder3, null, new Money(new BigDecimal("60000")), new BigDecimal("0.1"));
        checking = new Checking(new Money(new BigDecimal("10000")), accountHolder4, null, "owo");

        accountRepository.saveAll(List.of(savings, savings2,creditCard, checking));

        String password = passwordEncoder.encode("123");
        thirdParty = new ThirdParty("Pacathird", password);
        thirdPartyRepository.save(thirdParty);

    }

    @AfterEach
    void tearDown() {
        transactionRepository.deleteAll();
        accountRepository.deleteAll();
        accountHolderRepository.deleteAll();
        thirdPartyRepository.deleteAll();
    }

    @Test
    void newTransaction_LocalTransactionFromSavingsAccount_Transaction() throws Exception{

        User user = new User("Berto", "123", AuthorityUtils.createAuthorityList("ACCOUNT_HOLDER"));
        TestingAuthenticationToken testingAuthenticationToken = new TestingAuthenticationToken(user,null);

        Integer savingsId = savings.getId();
        Integer savingsId2 = savings2.getId();

        TransactionDTO transactionDTO = new TransactionDTO(savingsId, savingsId2, "Berta", new BigDecimal("1000"), "USD");

        String body = objectMapper.writeValueAsString(transactionDTO);
        mockMvc.perform(
                post("/transaction").principal(testingAuthenticationToken)
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk()).andReturn();

        assertEquals((new BigDecimal("9000")).compareTo(accountRepository.findByPrimaryOwner(accountHolder).get(0).getBalance().getAmount()), 0);
        assertEquals((new BigDecimal("5000")).compareTo(accountRepository.findByPrimaryOwner(accountHolder2).get(0).getBalance().getAmount()), 0);
    }

    @Test
    void newTransaction_LocalTransactionFromCheckingAccount_Transaction() throws Exception{

        User user = new User("Willirex", "123", AuthorityUtils.createAuthorityList("ACCOUNT_HOLDER"));
        TestingAuthenticationToken testingAuthenticationToken = new TestingAuthenticationToken(user,null);

        Integer checkingId = checking.getId();
        Integer creditcardId = creditCard.getId();

        TransactionDTO transactionDTO = new TransactionDTO(checkingId, creditcardId, "Mercedes", new BigDecimal("1000"), "USD");

        String body = objectMapper.writeValueAsString(transactionDTO);
        mockMvc.perform(
                post("/transaction").principal(testingAuthenticationToken)
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk()).andReturn();

        assertEquals((new BigDecimal("9000")).compareTo(accountRepository.findByPrimaryOwner(accountHolder4).get(0).getBalance().getAmount()), 0);
        assertEquals((new BigDecimal("31000")).compareTo(accountRepository.findByPrimaryOwner(accountHolder3).get(0).getBalance().getAmount()), 0);
    }

    @Test
    void newTransaction_checkStatusLocalTransaction_notTransaction() throws Exception{

        User user = new User("Berto", "123", AuthorityUtils.createAuthorityList("ACCOUNT_HOLDER"));
        TestingAuthenticationToken testingAuthenticationToken = new TestingAuthenticationToken(user,null);

        Integer savingsId = savings.getId();
        Integer savingsId2 = savings2.getId();

        savings.setStatus(Status.FROZEN);

        accountRepository.save(savings);

        TransactionDTO transactionDTO = new TransactionDTO(savingsId, savingsId2, "Berta", new BigDecimal("1000"), "USD");

        String body = objectMapper.writeValueAsString(transactionDTO);
        mockMvc.perform(
                post("/transaction").principal(testingAuthenticationToken)
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isUnauthorized()).andReturn();
    }

    @Test
    void newTransaction_checkTwoTransactionIn1Second_notTransaction() throws Exception{

        User user = new User("Berto", "123", AuthorityUtils.createAuthorityList("ACCOUNT_HOLDER"));
        TestingAuthenticationToken testingAuthenticationToken = new TestingAuthenticationToken(user,null);

        Integer savingsId = savings.getId();
        Integer savingsId2 = savings2.getId();

        TransactionDTO transactionDTO = new TransactionDTO(savingsId, savingsId2, "Berta", new BigDecimal("1000"), "USD");

        String body = objectMapper.writeValueAsString(transactionDTO);
        mockMvc.perform(
                post("/transaction").principal(testingAuthenticationToken)
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk()).andReturn();

        Thread.sleep(250);

        mockMvc.perform(
                post("/transaction").principal(testingAuthenticationToken)
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isForbidden()).andReturn();

    }

    @Test
    void newTransaction_checkMaxAmountFraudLocalTransaction_notTransaction() throws Exception{

        User user = new User("Berto", "123", AuthorityUtils.createAuthorityList("ACCOUNT_HOLDER"));
        TestingAuthenticationToken testingAuthenticationToken = new TestingAuthenticationToken(user,null);

        TransactionDTO transactionDTO = new TransactionDTO(savings.getId(), savings2.getId(), "Berta", new BigDecimal("1000"), "USD");

        String body = objectMapper.writeValueAsString(transactionDTO);
        mockMvc.perform(
                post("/transaction").principal(testingAuthenticationToken)
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk()).andReturn();
        Thread.sleep(1500); // Para evitar que salte el fraude por menos de 1 segundo
        mockMvc.perform(
                post("/transaction").principal(testingAuthenticationToken)
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isForbidden()).andReturn();
    }

    @Test
    void newTransaction_invalidUserLocalTransaction_notTransaction() throws Exception{

        User user = new User("Mercedes", "123", AuthorityUtils.createAuthorityList("ACCOUNT_HOLDER"));
        TestingAuthenticationToken testingAuthenticationToken = new TestingAuthenticationToken(user,null);

        Integer savingsId = savings.getId();
        Integer savingsId2 = savings2.getId();

        TransactionDTO transactionDTO = new TransactionDTO(savingsId, savingsId2, "Berta", new BigDecimal("1000"), "USD");

        String body = objectMapper.writeValueAsString(transactionDTO);
        mockMvc.perform(
                post("/transaction").principal(testingAuthenticationToken)
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isUnauthorized()).andReturn();
    }

    @Test
    void newTransaction_invalidOriginAccountLocalTransaction_notTransaction() throws Exception{

        User user = new User("Berto", "123", AuthorityUtils.createAuthorityList("ACCOUNT_HOLDER"));
        TestingAuthenticationToken testingAuthenticationToken = new TestingAuthenticationToken(user,null);

        Integer savingsId2 = savings2.getId();

        TransactionDTO transactionDTO = new TransactionDTO(896, savingsId2, "Berta", new BigDecimal("1000"), "USD");

        String body = objectMapper.writeValueAsString(transactionDTO);
        mockMvc.perform(
                post("/transaction").principal(testingAuthenticationToken)
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    void newTransaction_invalidOriginAccountThatExistsLocalTransaction_notTransaction() throws Exception{

        User user = new User("Berto", "123", AuthorityUtils.createAuthorityList("ACCOUNT_HOLDER"));
        TestingAuthenticationToken testingAuthenticationToken = new TestingAuthenticationToken(user,null);

        Integer savingsId2 = savings2.getId();
        Integer creditCardId = creditCard.getId();

        TransactionDTO transactionDTO = new TransactionDTO(creditCardId, savingsId2, "Berta", new BigDecimal("1000"), "USD");

        String body = objectMapper.writeValueAsString(transactionDTO);
       mockMvc.perform(
                post("/transaction").principal(testingAuthenticationToken)
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isUnauthorized()).andReturn();
    }

    @Test
    void newTransaction_invalidDestinationOwnerNameLocalTransaction_notTransaction() throws Exception{

        User user = new User("Berto", "123", AuthorityUtils.createAuthorityList("ACCOUNT_HOLDER"));
        TestingAuthenticationToken testingAuthenticationToken = new TestingAuthenticationToken(user,null);

        Integer savingsId = savings.getId();
        Integer savingsId2 = savings2.getId();

        TransactionDTO transactionDTO = new TransactionDTO(savingsId, savingsId2, "Pepe", new BigDecimal("1000"), "USD");

        String body = objectMapper.writeValueAsString(transactionDTO);
        mockMvc.perform(
                post("/transaction").principal(testingAuthenticationToken)
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    void newTransaction_invalidDestinationAccountLocalTransaction_notTransaction() throws Exception{

        User user = new User("Berto", "123", AuthorityUtils.createAuthorityList("ACCOUNT_HOLDER"));
        TestingAuthenticationToken testingAuthenticationToken = new TestingAuthenticationToken(user,null);

        Integer savingsId = savings.getId();

        TransactionDTO transactionDTO = new TransactionDTO(savingsId, 987, "Berta", new BigDecimal("1000"), "USD");

        String body = objectMapper.writeValueAsString(transactionDTO);
        mockMvc.perform(
                post("/transaction").principal(testingAuthenticationToken)
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest()).andReturn();
    }


    @Test
    void newTransaction_LocalTransaction_applyPenaltyFee() throws Exception{
        User user = new User("Berto", "123", AuthorityUtils.createAuthorityList("ACCOUNT_HOLDER"));
        TestingAuthenticationToken testingAuthenticationToken = new TestingAuthenticationToken(user,null);

        Integer savingsId = savings.getId();
        Integer savingsId2 = savings2.getId();

        TransactionDTO transactionDTO = new TransactionDTO(savingsId, savingsId2, "Berta", new BigDecimal("9900"), "USD");

        String body = objectMapper.writeValueAsString(transactionDTO);
        mockMvc.perform(
                post("/transaction").principal(testingAuthenticationToken)
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk()).andReturn();

        assertEquals((new BigDecimal("60")).compareTo(accountRepository.findByPrimaryOwner(accountHolder).get(0).getBalance().getAmount()), 0);
    }

    @Test
    void newTransaction_LocalTransactionNotSufficientFunds_notTransaction() throws Exception{

        User user = new User("Berto", "123", AuthorityUtils.createAuthorityList("ACCOUNT_HOLDER"));
        TestingAuthenticationToken testingAuthenticationToken = new TestingAuthenticationToken(user,null);

        Integer savingsId = savings.getId();
        Integer savingsId2 = savings2.getId();

        TransactionDTO transactionDTO = new TransactionDTO(savingsId, savingsId2, "Berta", new BigDecimal("11000"), "USD");

        String body = objectMapper.writeValueAsString(transactionDTO);
        mockMvc.perform(
                post("/transaction").principal(testingAuthenticationToken)
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    void newTransaction_transactionToThirdParty_Transaction() throws Exception{

        User user = new User("Berto", "123", AuthorityUtils.createAuthorityList("ACCOUNT_HOLDER"));
        TestingAuthenticationToken testingAuthenticationToken = new TestingAuthenticationToken(user,null);

        Integer savingsId = savings.getId();
        Integer thirdPartyId = thirdParty.getId();

        TransactionDTO transactionDTO = new TransactionDTO(savingsId, "Pacathird", thirdPartyId, new BigDecimal("1000"), "USD");

        String body = objectMapper.writeValueAsString(transactionDTO);
        mockMvc.perform(
                post("/transaction").principal(testingAuthenticationToken)
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk()).andReturn();

        assertEquals((new BigDecimal("9000")).compareTo(accountRepository.findByPrimaryOwner(accountHolder).get(0).getBalance().getAmount()), 0);
    }


    @Test
    void newTransaction_invalidDestinationOwnerNameTransactionToThirdParty_notTransaction() throws Exception{

        User user = new User("Berto", "123", AuthorityUtils.createAuthorityList("ACCOUNT_HOLDER"));
        TestingAuthenticationToken testingAuthenticationToken = new TestingAuthenticationToken(user,null);

        Integer savingsId = savings.getId();
        Integer thirdPartyId = thirdParty.getId();

        TransactionDTO transactionDTO = new TransactionDTO(savingsId, "Camela", thirdPartyId, new BigDecimal("1000"), "USD");

        String body = objectMapper.writeValueAsString(transactionDTO);
        mockMvc.perform(
                post("/transaction").principal(testingAuthenticationToken)
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    void newTransaction_notSufficientFundsTransactionToThirdParty_notTransaction() throws Exception{

        User user = new User("Berto", "123", AuthorityUtils.createAuthorityList("ACCOUNT_HOLDER"));
        TestingAuthenticationToken testingAuthenticationToken = new TestingAuthenticationToken(user,null);

        Integer savingsId = savings.getId();
        Integer thirdPartyId = thirdParty.getId();

        TransactionDTO transactionDTO = new TransactionDTO(savingsId, "Pacathird", thirdPartyId, new BigDecimal("100000"), "USD");

        String body = objectMapper.writeValueAsString(transactionDTO);
        mockMvc.perform(
                post("/transaction").principal(testingAuthenticationToken)
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    void newTransaction_transactionToThirdParty_PenaltyFee() throws Exception{

        User user = new User("Berto", "123", AuthorityUtils.createAuthorityList("ACCOUNT_HOLDER"));
        TestingAuthenticationToken testingAuthenticationToken = new TestingAuthenticationToken(user,null);

        Integer savingsId = savings.getId();
        Integer thirdPartyId = thirdParty.getId();

        TransactionDTO transactionDTO = new TransactionDTO(savingsId, "Pacathird", thirdPartyId, new BigDecimal("9900"), "USD");

        String body = objectMapper.writeValueAsString(transactionDTO);
        mockMvc.perform(
                post("/transaction").principal(testingAuthenticationToken)
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk()).andReturn();

        assertEquals((new BigDecimal("60")).compareTo(accountRepository.findByPrimaryOwner(accountHolder).get(0).getBalance().getAmount()), 0);
    }

    @Test
    void newTransaction_transactionToInvalidThirdParty_notTransaction() throws Exception{

        User user = new User("Berto", "123", AuthorityUtils.createAuthorityList("ACCOUNT_HOLDER"));
        TestingAuthenticationToken testingAuthenticationToken = new TestingAuthenticationToken(user,null);

        Integer savingsId = savings.getId();

        TransactionDTO transactionDTO = new TransactionDTO(savingsId, "Pacathird", 654, new BigDecimal("9900"), "USD");

        String body = objectMapper.writeValueAsString(transactionDTO);
        mockMvc.perform(
                post("/transaction").principal(testingAuthenticationToken)
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    void newTransaction_transactionFromInvalidOriginAccountToThirdParty_notTransaction() throws Exception{

        User user = new User("Berto", "123", AuthorityUtils.createAuthorityList("ACCOUNT_HOLDER"));
        TestingAuthenticationToken testingAuthenticationToken = new TestingAuthenticationToken(user,null);

        TransactionDTO transactionDTO = new TransactionDTO(978, "Pacathird", 654, new BigDecimal("9900"), "USD");

        String body = objectMapper.writeValueAsString(transactionDTO);
        mockMvc.perform(
                post("/transaction").principal(testingAuthenticationToken)
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    void newThirdPartyTransaction_ThirdPartyTransaction_Transaction() throws Exception{
        Integer accountId = savings.getId();

        ThirdPartyTransactionDTO thirdPartyTransactionDTO = new ThirdPartyTransactionDTO(new BigDecimal("100"), accountId, "owo", thirdParty.getId());

        String body = objectMapper.writeValueAsString(thirdPartyTransactionDTO);
        mockMvc.perform(
                post("/transaction/thirdparty")
                        .header("HASHED_KEY", "123")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk()).andReturn();

        assertEquals((new BigDecimal("10100")).compareTo(accountRepository.findByPrimaryOwner(accountHolderRepository.findByName("Berto").get(0)).get(0).getBalance().getAmount()), 0);
    }

    @Test
    void newThirdPartyTransaction_invalidSecretKeyThirdPartyTransaction_Transaction() throws Exception{
        Integer accountId = savings.getId();

        ThirdPartyTransactionDTO thirdPartyTransactionDTO = new ThirdPartyTransactionDTO(new BigDecimal("100"), accountId, "camelita", thirdParty.getId());

        String body = objectMapper.writeValueAsString(thirdPartyTransactionDTO);
        mockMvc.perform(
                post("/transaction/thirdparty")
                        .header("HASHED_KEY", "123")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    void newThirdPartyTransaction_ThirdPartyTransactionToInvalidDestination_notTransaction() throws Exception{

        ThirdPartyTransactionDTO thirdPartyTransactionDTO = new ThirdPartyTransactionDTO(new BigDecimal("100"), 789, "owo", thirdParty.getId());

        String body = objectMapper.writeValueAsString(thirdPartyTransactionDTO);
        mockMvc.perform(
                post("/transaction/thirdparty")
                        .header("HASHED_KEY", "123")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    void newThirdPartyTransaction_ThirdPartyTransactionInvalidThirdParty_notTransaction() throws Exception{
        Integer accountId = savings.getId();

        ThirdPartyTransactionDTO thirdPartyTransactionDTO = new ThirdPartyTransactionDTO(new BigDecimal("100"), accountId, "owo", thirdParty.getId()+987);

        String body = objectMapper.writeValueAsString(thirdPartyTransactionDTO);
        mockMvc.perform(
                post("/transaction/thirdparty")
                        .header("HASHED_KEY", "123")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    void newThirdPartyTransaction_ThirdPartyTransactionInvalidHashedKey_notTransaction() throws Exception{
        Integer accountId = savings.getId();

        ThirdPartyTransactionDTO thirdPartyTransactionDTO = new ThirdPartyTransactionDTO(new BigDecimal("100"), accountId, "owo", thirdParty.getId()+987);

        String body = objectMapper.writeValueAsString(thirdPartyTransactionDTO);
        mockMvc.perform(
                post("/transaction/thirdparty")
                        .header("HASHED_KEY", "897")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest()).andReturn();
    }

}