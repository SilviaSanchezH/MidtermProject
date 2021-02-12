package com.example.midtermproject.service.impl;

import com.example.midtermproject.controller.dto.CheckingDTO;
import com.example.midtermproject.model.Users.AccountHolder;
import com.example.midtermproject.model.shared.Address;
import com.example.midtermproject.repository.AccountHolderRepository;
import com.example.midtermproject.repository.AccountRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.ConstraintViolationException;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CheckingServiceTest {

    @Autowired
    private AccountHolderRepository accountHolderRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CheckingService checkingService;

    public AccountHolder nino;
    public AccountHolder nina;


    @BeforeEach
    void setUp() {
        Address primaryAddress = new Address("castellana", "madrid", "28888");
        Address secondaryAddress = new Address("goya", "madrid", "28976");
        nino = new AccountHolder("Nino", "123", "Nino", LocalDate.of(1950, 9,8), primaryAddress, secondaryAddress);
        nina = new AccountHolder("Nina", "123", "Nina", LocalDate.of(1950, 9,8), primaryAddress, secondaryAddress);
        accountHolderRepository.save(nino);
        accountHolderRepository.save(nina);
    }

    @AfterEach
    void tearDown() {
        accountRepository.deleteAll();
        accountHolderRepository.deleteAll();
    }

    @Test
    void newChecking_newCheckingAccount_Create() {
        Integer primaryOwnerId = nino.getId();
        Integer secondaryOwnerIc = nina.getId();
        CheckingDTO checkingDTO = new CheckingDTO(new BigDecimal("2000"), primaryOwnerId, secondaryOwnerIc, "123");

        checkingService.newChecking(checkingDTO);

        assertEquals("Nino", accountRepository.findByPrimaryOwner(nino).get(0).getPrimaryOwner().getName());
    }

    @Test
    void newChecking_newCheckingAccountWithoutSecondaryOwner_Create() {
        Integer primaryOwnerId = nino.getId();
        Integer secondaryOwnerIc = nina.getId();
        CheckingDTO checkingDTO = new CheckingDTO(new BigDecimal("2000"), primaryOwnerId, null, "123");

        checkingService.newChecking(checkingDTO);

        assertEquals("Nino", accountRepository.findByPrimaryOwner(nino).get(0).getPrimaryOwner().getName());
    }

    @Test
    void newChecking_newCheckingAccountWithoutSecretKey_notCreate() {
        Integer primaryOwnerId = nino.getId();
        Integer secondaryOwnerIc = nina.getId();
        CheckingDTO checkingDTO = new CheckingDTO(new BigDecimal("2000"), primaryOwnerId, null, null);
        assertThrows(ConstraintViolationException.class, ()->checkingService.newChecking(checkingDTO));
    }

    @Test
    void newChecking_newCheckingAccountWithoutPrimaryOwner_notCreate() {
        Integer primaryOwnerId = nino.getId();
        Integer secondaryOwnerIc = nina.getId();
        CheckingDTO checkingDTO = new CheckingDTO(new BigDecimal("2000"), null, null, "123");
        assertThrows(InvalidDataAccessApiUsageException.class, ()->checkingService.newChecking(checkingDTO));
    }


}