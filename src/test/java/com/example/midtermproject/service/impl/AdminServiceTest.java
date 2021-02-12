package com.example.midtermproject.service.impl;

import com.example.midtermproject.model.Users.Admin;
import com.example.midtermproject.repository.AdminRepository;
import com.example.midtermproject.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AdminServiceTest {

    @Autowired
    private AdminService adminService;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
        adminRepository.deleteAll();
    }

    @Test
    void newAdmin() {
        Admin admin = new Admin("ElAdmin", "123", "gustavo");
        adminService.newAdmin(admin);
        assertEquals("gustavo", adminRepository.findByName("gustavo").get(0).getName());
    }
}