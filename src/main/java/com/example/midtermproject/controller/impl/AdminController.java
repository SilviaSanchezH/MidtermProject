package com.example.midtermproject.controller.impl;

import com.example.midtermproject.model.Users.Admin;
import com.example.midtermproject.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class AdminController {

    @Autowired
    private AdminRepository adminRepository;

    @PostMapping("/user/admin")
    @ResponseStatus(HttpStatus.CREATED)
    public Admin newAdmin(@Valid @RequestBody Admin admin){
        return adminRepository.save(admin);
    }

}
