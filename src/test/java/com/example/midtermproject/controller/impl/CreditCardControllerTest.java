package com.example.midtermproject.controller.impl;

import com.example.midtermproject.controller.dto.CreditCardDTO;
import com.example.midtermproject.controller.dto.SavingsDTO;
import com.example.midtermproject.model.Users.AccountHolder;
import com.example.midtermproject.model.shared.Address;
import com.example.midtermproject.repository.AccountHolderRepository;
import com.example.midtermproject.repository.AccountRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class CreditCardControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private AccountHolderRepository accountHolderRepository;

    @Autowired
    private AccountRepository accountRepository;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    public AccountHolder accountHolder;
    public AccountHolder accountHolder2;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        Address primaryAddress = new Address("castellana", "madrid", "28888");
        Address secondaryAddress = new Address("goya", "madrid", "28976");
        accountHolder = new AccountHolder("Cayetano", "123", "Nino", LocalDate.of(1950, 9,8), primaryAddress, secondaryAddress);
        accountHolder2 = new AccountHolder("Cayetana", "123", "Nina", LocalDate.of(1950, 9,8), primaryAddress, secondaryAddress);

        accountHolderRepository.save(accountHolder);
        accountHolderRepository.save(accountHolder2);
    }

    @AfterEach
    void tearDown() {
        accountRepository.deleteAll();
        accountHolderRepository.deleteAll();

    }


    @Test
    void newCreditCard_Valid_Created() throws Exception {
        CreditCardDTO creditCardDTO = new CreditCardDTO(new BigDecimal("78000"), accountHolder.getId(), accountHolder2.getId(), new BigDecimal("20000"), new BigDecimal("0.1"));
        String body = objectMapper.writeValueAsString(creditCardDTO);
        MvcResult result = mockMvc.perform(
                post("/account/creditcard")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("Cayetano"));
    }

    @Test
    void newCreditCard_CreditCardWithoutSecondaryOwner_Created() throws Exception {
        CreditCardDTO creditCardDTO = new CreditCardDTO(new BigDecimal("78000"), accountHolder.getId(), null, new BigDecimal("20000"), new BigDecimal("0.1"));
        String body = objectMapper.writeValueAsString(creditCardDTO);
        MvcResult result = mockMvc.perform(
                post("/account/creditcard")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("Cayetano"));
    }

    @Test
    void newCreditCard_invalidCreditLimit_Created() throws Exception {
        CreditCardDTO creditCardDTO = new CreditCardDTO(new BigDecimal("78000"), accountHolder.getId(), accountHolder2.getId(), new BigDecimal("20"), new BigDecimal("0.1"));
        String body = objectMapper.writeValueAsString(creditCardDTO);
        MvcResult result = mockMvc.perform(
                post("/account/creditcard")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    void newCreditCard_invalidInterestRate_Created() throws Exception {
        CreditCardDTO creditCardDTO = new CreditCardDTO(new BigDecimal("78000"), accountHolder.getId(), accountHolder2.getId(), new BigDecimal("20000"), new BigDecimal("0.01"));
        String body = objectMapper.writeValueAsString(creditCardDTO);
        MvcResult result = mockMvc.perform(
                post("/account/creditcard")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    void newCreditCard_nullBalance_notCreated() throws Exception {
        CreditCardDTO creditCardDTO = new CreditCardDTO(null, accountHolder.getId(), accountHolder2.getId(), new BigDecimal("20000"), new BigDecimal("0.1"));
        String body = objectMapper.writeValueAsString(creditCardDTO);
        MvcResult result = mockMvc.perform(
                post("/account/creditcard")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest()).andReturn();
    }


    @Test
    void newCreditCard_nullCreditLimit_CreatedWithDefaultCreditLimit() throws Exception {
        CreditCardDTO creditCardDTO = new CreditCardDTO(new BigDecimal("78000"), accountHolder.getId(), accountHolder2.getId(), null, new BigDecimal("0.1"));
        String body = objectMapper.writeValueAsString(creditCardDTO);
        MvcResult result = mockMvc.perform(
                post("/account/creditcard")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("100"));
    }

    @Test
    void newCreditCard_nullInterestRate_CreatedWithDefaultInterestRate() throws Exception {
        CreditCardDTO creditCardDTO = new CreditCardDTO(new BigDecimal("78000"), accountHolder.getId(), accountHolder2.getId(),  new BigDecimal("20000"), null);
        String body = objectMapper.writeValueAsString(creditCardDTO);
        MvcResult result = mockMvc.perform(
                post("/account/creditcard")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("0.2"));
    }
}
