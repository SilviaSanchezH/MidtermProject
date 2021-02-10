package com.example.midtermproject.controller.impl;

import com.example.midtermproject.controller.dto.ThirdPartyTransactionDTO;
import com.example.midtermproject.controller.dto.TransactionDTO;
import com.example.midtermproject.enums.Status;
import com.example.midtermproject.model.Accounts.Account;
import com.example.midtermproject.model.Accounts.Savings;
import com.example.midtermproject.model.Accounts.Transaction;
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
import org.springframework.test.web.servlet.MvcResult;
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

    private ObjectMapper objectMapper = new ObjectMapper();

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        Address primaryAddress = new Address("castellana", "madrid", "28888");
        Address secondaryAddress = new Address("goya", "madrid", "28976");
        AccountHolder accountHolder = new AccountHolder("Berto", "123", "Berto", LocalDate.of(1950, 9,8), primaryAddress, secondaryAddress);
        AccountHolder accountHolder2 = new AccountHolder("Berta", "123", "Berta", LocalDate.of(1950, 9,8), primaryAddress, secondaryAddress);

        accountHolderRepository.save(accountHolder);
        accountHolderRepository.save(accountHolder2);

        Savings savings = new Savings (new Money(new BigDecimal("10000")) , accountHolder, accountHolder2, "owo", new Money(new BigDecimal("200")), new BigDecimal("0.2"));
        Savings savings2 = new Savings (new Money(new BigDecimal("4000")) , accountHolder2, accountHolder, "owo", new Money(new BigDecimal("200")), new BigDecimal("0.2"));

        accountRepository.saveAll(List.of(savings, savings2));

        String password = passwordEncoder.encode("123");
        ThirdParty thirdParty = new ThirdParty("Pacathird", password);
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
    void newTransaction_LocalTransaction_Transaction() throws Exception{

        User user = new User("Berto", "123", AuthorityUtils.createAuthorityList("ACCOUNT_HOLDER"));
        TestingAuthenticationToken testingAuthenticationToken = new TestingAuthenticationToken(user,null);

        Integer savingsId = accountRepository.findByPrimaryOwner(accountHolderRepository.findByName("Berto").get(0)).get(0).getId();
        Integer savingsId2 = accountRepository.findByPrimaryOwner(accountHolderRepository.findByName("Berta").get(0)).get(0).getId();

        TransactionDTO transactionDTO = new TransactionDTO(savingsId, savingsId2, "Berta", new BigDecimal("1000"), "USD");

        String body = objectMapper.writeValueAsString(transactionDTO);
        MvcResult result = mockMvc.perform(
                post("/transaction").principal(testingAuthenticationToken)
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk()).andReturn();

        assertTrue((new BigDecimal("9000")).compareTo(accountRepository.findByPrimaryOwner(accountHolderRepository.findByName("Berto").get(0)).get(0).getBalance().getAmount())==0);
    }

    @Test
    void newTransaction_checkStatusLocalTransaction_Transaction() throws Exception{

        User user = new User("Berto", "123", AuthorityUtils.createAuthorityList("ACCOUNT_HOLDER"));
        TestingAuthenticationToken testingAuthenticationToken = new TestingAuthenticationToken(user,null);

        Account savings = accountRepository.findByPrimaryOwner(accountHolderRepository.findByName("Berto").get(0)).get(0);
        Integer savingsId = accountRepository.findByPrimaryOwner(accountHolderRepository.findByName("Berto").get(0)).get(0).getId();
        Integer savingsId2 = accountRepository.findByPrimaryOwner(accountHolderRepository.findByName("Berta").get(0)).get(0).getId();

        ((Savings) savings).setStatus(Status.FROZEN);

        accountRepository.save(savings);

        TransactionDTO transactionDTO = new TransactionDTO(savingsId, savingsId2, "Berta", new BigDecimal("1000"), "USD");

        String body = objectMapper.writeValueAsString(transactionDTO);
        MvcResult result = mockMvc.perform(
                post("/transaction").principal(testingAuthenticationToken)
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isUnauthorized()).andReturn();
    }

    @Test
    void newTransaction_CheckFraudLocalTransaction_notTransaction() throws Exception{

        User user = new User("Berto", "123", AuthorityUtils.createAuthorityList("ACCOUNT_HOLDER"));
        TestingAuthenticationToken testingAuthenticationToken = new TestingAuthenticationToken(user,null);

        Integer savingsId = accountRepository.findByPrimaryOwner(accountHolderRepository.findByName("Berto").get(0)).get(0).getId();
        Integer savingsId2 = accountRepository.findByPrimaryOwner(accountHolderRepository.findByName("Berta").get(0)).get(0).getId();

        TransactionDTO transactionDTO = new TransactionDTO(savingsId, savingsId2, "Berta", new BigDecimal("1000"), "USD");
        TransactionDTO transactionDTO2 = new TransactionDTO(savingsId, savingsId2, "Berta", new BigDecimal("1000"), "USD");

        String body = objectMapper.writeValueAsString(transactionDTO);
        MvcResult result = mockMvc.perform(
                post("/transaction").principal(testingAuthenticationToken)
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk()).andReturn();

        Thread.sleep(250);

        String body2 = objectMapper.writeValueAsString(transactionDTO2);
        MvcResult result2 = mockMvc.perform(
                post("/transaction").principal(testingAuthenticationToken)
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isForbidden()).andReturn();

    }

    @Test
    void newTransaction_CheckFraud150LocalTransaction_notTransaction() throws Exception{

        User user = new User("Berto", "123", AuthorityUtils.createAuthorityList("ACCOUNT_HOLDER"));
        TestingAuthenticationToken testingAuthenticationToken = new TestingAuthenticationToken(user,null);

        Account savings = accountRepository.findByPrimaryOwner(accountHolderRepository.findByName("Berto").get(0)).get(0);
        Account savings2 = accountRepository.findByPrimaryOwner(accountHolderRepository.findByName("Berta").get(0)).get(0);

        TransactionDTO transactionDTO = new TransactionDTO(savings.getId(), savings2.getId(), "Berta", new BigDecimal("1000"), "USD");
        Transaction transaction = new Transaction(new Money(new BigDecimal(500)), savings2, savings);
        transactionRepository.save(transaction);

        String body = objectMapper.writeValueAsString(transactionDTO);
        MvcResult result = mockMvc.perform(
                post("/transaction").principal(testingAuthenticationToken)
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk()).andReturn();
        Thread.sleep(1500); // Para evitar que salte el fraude por menos de 1 segundo
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

        User user = new User("Agustino", "123", AuthorityUtils.createAuthorityList("ACCOUNT_HOLDER"));
        TestingAuthenticationToken testingAuthenticationToken = new TestingAuthenticationToken(user,null);

        Integer savingsId = accountRepository.findByPrimaryOwner(accountHolderRepository.findByName("Berto").get(0)).get(0).getId();
        Integer savingsId2 = accountRepository.findByPrimaryOwner(accountHolderRepository.findByName("Berta").get(0)).get(0).getId();

        TransactionDTO transactionDTO = new TransactionDTO(savingsId, savingsId2, "Berta", new BigDecimal("1000"), "USD");

        String body = objectMapper.writeValueAsString(transactionDTO);
        MvcResult result = mockMvc.perform(
                post("/transaction").principal(testingAuthenticationToken)
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isUnauthorized()).andReturn();
    }

    @Test
    void newTransaction_invalidOriginAccountLocalTransaction_notTransaction() throws Exception{

        User user = new User("Berto", "123", AuthorityUtils.createAuthorityList("ACCOUNT_HOLDER"));
        TestingAuthenticationToken testingAuthenticationToken = new TestingAuthenticationToken(user,null);

        Integer savingsId = accountRepository.findByPrimaryOwner(accountHolderRepository.findByName("Berto").get(0)).get(0).getId();
        Integer savingsId2 = accountRepository.findByPrimaryOwner(accountHolderRepository.findByName("Berta").get(0)).get(0).getId();

        TransactionDTO transactionDTO = new TransactionDTO(896, savingsId2, "Berta", new BigDecimal("1000"), "USD");

        String body = objectMapper.writeValueAsString(transactionDTO);
        MvcResult result = mockMvc.perform(
                post("/transaction").principal(testingAuthenticationToken)
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    void newTransaction_invalidDestinationAccountLocalTransaction_notTransaction() throws Exception{

        User user = new User("Berto", "123", AuthorityUtils.createAuthorityList("ACCOUNT_HOLDER"));
        TestingAuthenticationToken testingAuthenticationToken = new TestingAuthenticationToken(user,null);

        Integer savingsId = accountRepository.findByPrimaryOwner(accountHolderRepository.findByName("Berto").get(0)).get(0).getId();
        Integer savingsId2 = accountRepository.findByPrimaryOwner(accountHolderRepository.findByName("Berta").get(0)).get(0).getId();

        TransactionDTO transactionDTO = new TransactionDTO(savingsId, 987, "Berta", new BigDecimal("1000"), "USD");

        String body = objectMapper.writeValueAsString(transactionDTO);
        MvcResult result = mockMvc.perform(
                post("/transaction").principal(testingAuthenticationToken)
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest()).andReturn();
    }



    //Al hacer una transferencia de 9900, como ha bajado del mínimum balance se le restan 40.
    @Test
    void newTransaction_LocalTransaction_applyPenaltyFee() throws Exception{

        User user = new User("Berto", "123", AuthorityUtils.createAuthorityList("ACCOUNT_HOLDER"));
        TestingAuthenticationToken testingAuthenticationToken = new TestingAuthenticationToken(user,null);

        Integer savingsId = accountRepository.findByPrimaryOwner(accountHolderRepository.findByName("Berto").get(0)).get(0).getId();
        Integer savingsId2 = accountRepository.findByPrimaryOwner(accountHolderRepository.findByName("Berta").get(0)).get(0).getId();

        TransactionDTO transactionDTO = new TransactionDTO(savingsId, savingsId2, "Berta", new BigDecimal("9900"), "USD");

        String body = objectMapper.writeValueAsString(transactionDTO);
        MvcResult result = mockMvc.perform(
                post("/transaction").principal(testingAuthenticationToken)
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk()).andReturn();

        assertTrue((new BigDecimal("60")).compareTo(accountRepository.findByPrimaryOwner(accountHolderRepository.findByName("Berto").get(0)).get(0).getBalance().getAmount())==0);
    }

    @Test
    void newTransaction_LocalTransactionNotSufficientFunds_notTransaction() throws Exception{

        User user = new User("Berto", "123", AuthorityUtils.createAuthorityList("ACCOUNT_HOLDER"));
        TestingAuthenticationToken testingAuthenticationToken = new TestingAuthenticationToken(user,null);

        Integer savingsId = accountRepository.findByPrimaryOwner(accountHolderRepository.findByName("Berto").get(0)).get(0).getId();
        Integer savingsId2 = accountRepository.findByPrimaryOwner(accountHolderRepository.findByName("Berta").get(0)).get(0).getId();

        TransactionDTO transactionDTO = new TransactionDTO(savingsId, savingsId2, "Berta", new BigDecimal("11000"), "USD");

        String body = objectMapper.writeValueAsString(transactionDTO);
        MvcResult result = mockMvc.perform(
                post("/transaction").principal(testingAuthenticationToken)
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    void newTransaction_transactionToThirdParty_Transaction() throws Exception{

        User user = new User("Berto", "123", AuthorityUtils.createAuthorityList("ACCOUNT_HOLDER"));
        TestingAuthenticationToken testingAuthenticationToken = new TestingAuthenticationToken(user,null);

        Integer savingsId = accountRepository.findByPrimaryOwner(accountHolderRepository.findByName("Berto").get(0)).get(0).getId();
        Integer thirdPartyId = thirdPartyRepository.findByName("Pacathird").get().getId();

        //Integer originAccount, String destinationOwnerName, Integer thirdPartyDestinationId, BigDecimal quantity, String currency
        TransactionDTO transactionDTO = new TransactionDTO(savingsId, "Pacathird", thirdPartyId, new BigDecimal("1000"), "USD");

        String body = objectMapper.writeValueAsString(transactionDTO);
        MvcResult result = mockMvc.perform(
                post("/transaction").principal(testingAuthenticationToken)
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk()).andReturn();

        assertTrue((new BigDecimal("9000")).compareTo(accountRepository.findByPrimaryOwner(accountHolderRepository.findByName("Berto").get(0)).get(0).getBalance().getAmount())==0);
    }

    @Test
    void newTransaction_transactionToThirdPartyNotSuffcientFunds_Nottransaction() throws Exception{

        User user = new User("Berto", "123", AuthorityUtils.createAuthorityList("ACCOUNT_HOLDER"));
        TestingAuthenticationToken testingAuthenticationToken = new TestingAuthenticationToken(user,null);

        Integer savingsId = accountRepository.findByPrimaryOwner(accountHolderRepository.findByName("Berto").get(0)).get(0).getId();
        Integer thirdPartyId = thirdPartyRepository.findByName("Pacathird").get().getId();

        //Integer originAccount, String destinationOwnerName, Integer thirdPartyDestinationId, BigDecimal quantity, String currency
        TransactionDTO transactionDTO = new TransactionDTO(savingsId, "Pacathird", thirdPartyId, new BigDecimal("100000"), "USD");

        String body = objectMapper.writeValueAsString(transactionDTO);
        MvcResult result = mockMvc.perform(
                post("/transaction").principal(testingAuthenticationToken)
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    void newTransaction_transactionToThirdParty_PenaltyFee() throws Exception{

        User user = new User("Berto", "123", AuthorityUtils.createAuthorityList("ACCOUNT_HOLDER"));
        TestingAuthenticationToken testingAuthenticationToken = new TestingAuthenticationToken(user,null);

        Integer savingsId = accountRepository.findByPrimaryOwner(accountHolderRepository.findByName("Berto").get(0)).get(0).getId();
        Integer thirdPartyId = thirdPartyRepository.findByName("Pacathird").get().getId();

        TransactionDTO transactionDTO = new TransactionDTO(savingsId, "Pacathird", thirdPartyId, new BigDecimal("9900"), "USD");

        String body = objectMapper.writeValueAsString(transactionDTO);
        MvcResult result = mockMvc.perform(
                post("/transaction").principal(testingAuthenticationToken)
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk()).andReturn();

        assertTrue((new BigDecimal("60")).compareTo(accountRepository.findByPrimaryOwner(accountHolderRepository.findByName("Berto").get(0)).get(0).getBalance().getAmount())==0);
    }

    @Test
    void newTransaction_transactionToInvalidThirdParty_notTransaction() throws Exception{

        User user = new User("Berto", "123", AuthorityUtils.createAuthorityList("ACCOUNT_HOLDER"));
        TestingAuthenticationToken testingAuthenticationToken = new TestingAuthenticationToken(user,null);

        Integer savingsId = accountRepository.findByPrimaryOwner(accountHolderRepository.findByName("Berto").get(0)).get(0).getId();
        Integer thirdPartyId = thirdPartyRepository.findByName("Pacathird").get().getId();

        TransactionDTO transactionDTO = new TransactionDTO(savingsId, "Pacathird", 654, new BigDecimal("9900"), "USD");

        String body = objectMapper.writeValueAsString(transactionDTO);
        MvcResult result = mockMvc.perform(
                post("/transaction").principal(testingAuthenticationToken)
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    void newTransaction_transactionFromInvalidOriginAccountToThirdParty_notTransaction() throws Exception{

        User user = new User("Berto", "123", AuthorityUtils.createAuthorityList("ACCOUNT_HOLDER"));
        TestingAuthenticationToken testingAuthenticationToken = new TestingAuthenticationToken(user,null);

        Integer savingsId = accountRepository.findByPrimaryOwner(accountHolderRepository.findByName("Berto").get(0)).get(0).getId();
        Integer thirdPartyId = thirdPartyRepository.findByName("Pacathird").get().getId();

        TransactionDTO transactionDTO = new TransactionDTO(978, "Pacathird", 654, new BigDecimal("9900"), "USD");

        String body = objectMapper.writeValueAsString(transactionDTO);
        MvcResult result = mockMvc.perform(
                post("/transaction").principal(testingAuthenticationToken)
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    void newThirdPartyTransaction_ThirdPartyTransaction_Transaction() throws Exception{

        Integer accountId = accountRepository.findByPrimaryOwner(accountHolderRepository.findByName("Berto").get(0)).get(0).getId();
        ThirdParty thirdParty = thirdPartyRepository.findByName("Pacathird").get();

        ThirdPartyTransactionDTO thirdPartyTransactionDTO = new ThirdPartyTransactionDTO(new BigDecimal("100"), accountId, "owo", thirdParty.getId());

        String body = objectMapper.writeValueAsString(thirdPartyTransactionDTO);
        MvcResult result = mockMvc.perform(
                post("/transaction/thirdparty")
                        .header("HASHED_KEY", "123")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk()).andReturn();

        assertEquals((new BigDecimal("10100")).compareTo(accountRepository.findByPrimaryOwner(accountHolderRepository.findByName("Berto").get(0)).get(0).getBalance().getAmount()), 0);

    }
}