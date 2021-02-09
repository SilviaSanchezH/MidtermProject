package com.example.midtermproject.controller.impl;

import com.example.midtermproject.controller.dto.CheckingDTO;
import com.example.midtermproject.model.Users.AccountHolder;
import com.example.midtermproject.model.Users.ThirdParty;
import com.example.midtermproject.model.shared.Address;
import com.example.midtermproject.repository.AccountHolderRepository;
import com.example.midtermproject.repository.CheckingRepository;
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
class CheckingControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private AccountHolderRepository accountHolderRepository;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void newChecking_ValidWithSecondaryOwner_Created() throws Exception {
        Address primaryAddress = new Address("castellana", "madrid", "28888");
        Address secondaryAddress = new Address("goya", "madrid", "28976");
        AccountHolder accountHolder = new AccountHolder("Nino", "123", "Nino", LocalDate.of(1950, 9,8), primaryAddress, secondaryAddress);
        AccountHolder accountHolder2 = new AccountHolder("Nina", "123", "Nina", LocalDate.of(1950, 9,8), primaryAddress, secondaryAddress);

        accountHolderRepository.save(accountHolder);
        accountHolderRepository.save(accountHolder2);

       CheckingDTO checkingDTO = new CheckingDTO(new BigDecimal("50000"), accountHolder.getId(), accountHolder2.getId(), "uwu");
        String body = objectMapper.writeValueAsString(checkingDTO);
        MvcResult result = mockMvc.perform(
                post("/account/checking")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("uwu"));
    }

    @Test
    void newChecking_ValidWithoutSecondaryOwner_Created() throws Exception {
        Address primaryAddress = new Address("castellana", "madrid", "28888");
        Address secondaryAddress = new Address("goya", "madrid", "28976");
        AccountHolder accountHolder = new AccountHolder("Nino", "123", "Nino", LocalDate.of(1950, 9,8), primaryAddress, secondaryAddress);
        AccountHolder accountHolder2 = new AccountHolder("Nina", "123", "Nina", LocalDate.of(1950, 9,8), primaryAddress, secondaryAddress);

        accountHolderRepository.save(accountHolder);
        accountHolderRepository.save(accountHolder2);

        CheckingDTO checkingDTO = new CheckingDTO(new BigDecimal("50000"), accountHolder.getId(), null, "uwu");
        String body = objectMapper.writeValueAsString(checkingDTO);
        MvcResult result = mockMvc.perform(
                post("/account/checking")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("uwu"));
    }

    }
