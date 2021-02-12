package com.example.midtermproject.controller.impl;

import com.example.midtermproject.controller.dto.CheckingDTO;
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
class SavingsControllerTest {

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
    void newSaving_Valid_Created() throws Exception {

        SavingsDTO savingsDTO = new SavingsDTO(new BigDecimal("78000"), accountHolder.getId(), accountHolder2.getId(), "owo", new BigDecimal("200"), new BigDecimal("0.2"));
        String body = objectMapper.writeValueAsString(savingsDTO);
        MvcResult result = mockMvc.perform(
                post("/account/savings")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("owo"));
    }

    @Test
    void newSaving_savingAccountWithoutSecondaryOwner_Created() throws Exception {

        SavingsDTO savingsDTO = new SavingsDTO(new BigDecimal("78000"), accountHolder.getId(), null, "owo", new BigDecimal("200"), new BigDecimal("0.2"));
        String body = objectMapper.writeValueAsString(savingsDTO);
        MvcResult result = mockMvc.perform(
                post("/account/savings")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("owo"));
    }

    @Test
    void newSaving_nullBalanceSavingAccount_notCreated() throws Exception {

        SavingsDTO savingsDTO = new SavingsDTO(null, accountHolder.getId(), null, "owo", new BigDecimal("200"), new BigDecimal("0.2"));
        String body = objectMapper.writeValueAsString(savingsDTO);
        MvcResult result = mockMvc.perform(
                post("/account/savings")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    void newSaving_invalidMinimunBalanceSavingAccount_notCreated() throws Exception {

        SavingsDTO savingsDTO = new SavingsDTO(new BigDecimal("78000"), accountHolder.getId(), null, "owo", new BigDecimal("90"), new BigDecimal("0.2"));
        String body = objectMapper.writeValueAsString(savingsDTO);
        MvcResult result = mockMvc.perform(
                post("/account/savings")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    void newSaving_invalidInterestRateSavingAccount_notCreated() throws Exception {

        SavingsDTO savingsDTO = new SavingsDTO(new BigDecimal("78000"), accountHolder.getId(), null, "owo", new BigDecimal("200"), new BigDecimal("0.6"));
        String body = objectMapper.writeValueAsString(savingsDTO);
        MvcResult result = mockMvc.perform(
                post("/account/savings")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    void newSaving_DefaultInterestRateSavingAccount_Created() throws Exception {

        SavingsDTO savingsDTO = new SavingsDTO(new BigDecimal("78000"), accountHolder.getId(), null, "owo", new BigDecimal("200"), null);
        String body = objectMapper.writeValueAsString(savingsDTO);
        MvcResult result = mockMvc.perform(
                post("/account/savings")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("0.0025"));
    }

    @Test
    void newSaving_DefaultMinimumBalanceSavingAccount_Created() throws Exception {

        SavingsDTO savingsDTO = new SavingsDTO(new BigDecimal("78000"), accountHolder.getId(), null, "owo", null, null);
        String body = objectMapper.writeValueAsString(savingsDTO);
        MvcResult result = mockMvc.perform(
                post("/account/savings")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("1000"));
    }




}