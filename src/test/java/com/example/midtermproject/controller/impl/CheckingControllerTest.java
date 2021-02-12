package com.example.midtermproject.controller.impl;

import com.example.midtermproject.controller.dto.CheckingDTO;
import com.example.midtermproject.model.Users.AccountHolder;
import com.example.midtermproject.model.Users.ThirdParty;
import com.example.midtermproject.model.shared.Address;
import com.example.midtermproject.repository.AccountHolderRepository;
import com.example.midtermproject.repository.AccountRepository;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class CheckingControllerTest {

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
        accountHolder = new AccountHolder("Nino", "123", "Nino", LocalDate.of(1950, 9,8), primaryAddress, secondaryAddress);
        accountHolder2 = new AccountHolder("Nina", "123", "Nina", LocalDate.of(1950, 9,8), primaryAddress, secondaryAddress);

        accountHolderRepository.saveAll(List.of(accountHolder,accountHolder2));
    }

    @AfterEach
    void tearDown() {
        accountRepository.deleteAll();
        accountHolderRepository.deleteAll();
    }

    @Test
    void newChecking_ValidWithSecondaryOwner_Created() throws Exception {

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

        CheckingDTO checkingDTO = new CheckingDTO(new BigDecimal("50000"), accountHolder.getId(), null, "uwu");
        String body = objectMapper.writeValueAsString(checkingDTO);
        MvcResult result = mockMvc.perform(
                post("/account/checking")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("uwu"));
    }


    @Test
    void newChecking_EmptyPrimaryOwner_notCreated() throws Exception {
        CheckingDTO checkingDTO = new CheckingDTO(new BigDecimal("50000"), null, accountHolder2.getId(), "uwu");
        String body = objectMapper.writeValueAsString(checkingDTO);
        MvcResult result = mockMvc.perform(
                post("/account/checking")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    void newChecking_EmptyBalance_notCreated() throws Exception {
        CheckingDTO checkingDTO = new CheckingDTO(null, accountHolder.getId(), accountHolder2.getId(), "uwu");
        String body = objectMapper.writeValueAsString(checkingDTO);
        MvcResult result = mockMvc.perform(
                post("/account/checking")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    void newChecking_EmptySecreyKey_Created() throws Exception {

        CheckingDTO checkingDTO = new CheckingDTO(new BigDecimal("50000"), accountHolder.getId(), null, null);
        String body = objectMapper.writeValueAsString(checkingDTO);
        MvcResult result = mockMvc.perform(
                post("/account/checking")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest()).andReturn();
    }

}
