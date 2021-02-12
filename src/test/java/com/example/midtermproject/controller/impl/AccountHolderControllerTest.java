package com.example.midtermproject.controller.impl;

import com.example.midtermproject.controller.dto.AccountHolderDTO;
import com.example.midtermproject.model.Users.AccountHolder;
import com.example.midtermproject.model.shared.Address;
import com.example.midtermproject.repository.AccountHolderRepository;
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

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class AccountHolderControllerTest {
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
        accountHolderRepository.deleteAll();
    }

    @Test
    void newAccountHolder_Valid_created() throws Exception{

        AccountHolderDTO accountHolderDTO = new AccountHolderDTO("yuyita", "123", "yuyi", LocalDate.of(1990, 8, 4), "castellana", "madrid", "28888", "goya", "madrid", "28888");
        String body = objectMapper.writeValueAsString(accountHolderDTO);
        MvcResult result = mockMvc.perform(
                post("/user/accountholder")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("yuyita"));
    }

    @Test
    void newAccountHolder_ValidAccountHolderWithoutSecondaryOwner_created() throws Exception{

        AccountHolderDTO accountHolderDTO = new AccountHolderDTO("yuyita", "123", "yuyi", LocalDate.of(1990, 8, 4), "castellana", "madrid", "28888", null, null, null);
        String body = objectMapper.writeValueAsString(accountHolderDTO);
        MvcResult result = mockMvc.perform(
                post("/user/accountholder")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("yuyita"));
    }

}