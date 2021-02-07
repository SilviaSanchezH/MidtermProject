package com.example.midtermproject.controller.impl;

import com.example.midtermproject.model.Users.ThirdParty;
import com.example.midtermproject.repository.ThirdPartyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class ThirdPartyController {

    @Autowired
    private ThirdPartyRepository thirdPartyRepository;

    @PostMapping("/user/thirdparty")
    @ResponseStatus(HttpStatus.CREATED)
    public ThirdParty newThirdParty(@RequestBody @Valid ThirdParty thirdParty){
        return thirdPartyRepository.save(thirdParty);
    }
}
