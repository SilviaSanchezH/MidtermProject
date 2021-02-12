package com.example.midtermproject.service.impl;

import com.example.midtermproject.model.Users.ThirdParty;
import com.example.midtermproject.repository.ThirdPartyRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ThirdPartyServiceTest {

    @Autowired
    private ThirdPartyRepository thirdPartyRepository;
    @Autowired
    private ThirdPartyService thirdPartyService;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
        thirdPartyRepository.deleteAll();
    }

    @Test
    void newThirdParty_validThirdPaty_thirdParty() {
        ThirdParty thirdParty = new ThirdParty("Silvia", "uwu");
        thirdPartyService.newThirdParty(thirdParty);

        assertEquals("Silvia", thirdPartyRepository.findByName("Silvia").get().getName());
    }
}