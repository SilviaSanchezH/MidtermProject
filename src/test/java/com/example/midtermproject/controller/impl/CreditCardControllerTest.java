package com.example.midtermproject.controller.impl;

import com.example.midtermproject.controller.dto.CreditCardDTO;
import com.example.midtermproject.controller.dto.SavingsDTO;
import com.example.midtermproject.model.Users.AccountHolder;
import com.example.midtermproject.model.shared.Address;
import com.example.midtermproject.repository.AccountHolderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void newCreditCard_Valid_Created() throws Exception {
        Address primaryAddress = new Address("castellana", "madrid", "28888");
        Address secondaryAddress = new Address("goya", "madrid", "28976");
        AccountHolder accountHolder5 = new AccountHolder("Cayetano", "123", "Nino", LocalDate.of(1950, 9,8), primaryAddress, secondaryAddress);
        AccountHolder accountHolder6 = new AccountHolder("Cayetana", "123", "Nina", LocalDate.of(1950, 9,8), primaryAddress, secondaryAddress);

        accountHolderRepository.save(accountHolder5);
        accountHolderRepository.save(accountHolder6);

        CreditCardDTO creditCardDTO = new CreditCardDTO(new BigDecimal("78000"), accountHolder5.getId(), accountHolder6.getId(), new BigDecimal("20000"), new BigDecimal("0.1"));
        String body = objectMapper.writeValueAsString(creditCardDTO);
        MvcResult result = mockMvc.perform(
                post("/account/creditcard")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("Cayetano"));
    }
    }
