package com.example.midtermproject.controller.impl;

import com.example.midtermproject.controller.dto.ThirdPartyTransactionDTO;
import com.example.midtermproject.controller.dto.TransactionDTO;
import com.example.midtermproject.model.Accounts.Account;
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
        Savings savings2 = new Savings (new Money(new BigDecimal("4000")) , accountHolder, accountHolder2, "owo", new Money(new BigDecimal("200")), new BigDecimal("0.2"));

        accountRepository.saveAll(List.of(savings, savings2));


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


        Address primaryAddress = new Address("castellana", "madrid", "28888");
        Address secondaryAddress = new Address("goya", "madrid", "28976");
        AccountHolder accountHolder = new AccountHolder("Berto", "123", "Berto", LocalDate.of(1950, 9,8), primaryAddress, secondaryAddress);
        AccountHolder accountHolder2 = new AccountHolder("Berta", "123", "Berta", LocalDate.of(1950, 9,8), primaryAddress, secondaryAddress);

        accountHolderRepository.save(accountHolder);
        accountHolderRepository.save(accountHolder2);

        Savings savings = new Savings (new Money(new BigDecimal("10000")) , accountHolder, accountHolder2, "owo", new Money(new BigDecimal("200")), new BigDecimal("0.2"));
        Savings savings2 = new Savings (new Money(new BigDecimal("4000")) , accountHolder, accountHolder2, "owo", new Money(new BigDecimal("200")), new BigDecimal("0.2"));

        accountRepository.saveAll(List.of(savings, savings2));

        User user = new User("Berto", "123", AuthorityUtils.createAuthorityList("ACCOUNT_HOLDER"));
        TestingAuthenticationToken testingAuthenticationToken = new TestingAuthenticationToken(user,null);

        TransactionDTO transactionDTO = new TransactionDTO(savings.getId(), savings2.getId(), savings.getPrimaryOwner().getName(), new BigDecimal("1000"), "USD");

        String body = objectMapper.writeValueAsString(transactionDTO);
        MvcResult result = mockMvc.perform(
                post("/transaction").principal(testingAuthenticationToken)
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk()).andReturn();

        assertTrue((new BigDecimal("9000")).compareTo(accountRepository.findByPrimaryOwner(accountHolderRepository.findByName("Berto").get(0)).get(0).getBalance().getAmount())==0);
    }

    //TODO: FALKTA TEST DE TRANSACCION A THIRDPARTY


    //TODO: ESTE TEST NO FUNCIONA 401!!!
    @Test
    void newThirdPartyTransaction_ThirdPartyTransaction_Transaction() throws Exception{


        Integer accountId = accountRepository.findByPrimaryOwner(accountHolderRepository.findByName("Berto").get(0)).get(0).getId();

        ThirdParty thirdParty = new ThirdParty("Pacathird", "123");
        thirdPartyRepository.save(thirdParty);

        ThirdPartyTransactionDTO thirdPartyTransactionDTO = new ThirdPartyTransactionDTO(new BigDecimal("100"), accountId, "owo", thirdParty.getId());

        String body = objectMapper.writeValueAsString(thirdPartyTransactionDTO);
        MvcResult result = mockMvc.perform(
                post("/transaction/thirdparty")
                        .header("HASHED_KEY", "123")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk()).andReturn();

        assertTrue((new BigDecimal("10100")).compareTo(accountRepository.findByPrimaryOwner(accountHolderRepository.findByName("Berto").get(0)).get(0).getBalance().getAmount())==0);


    }
}