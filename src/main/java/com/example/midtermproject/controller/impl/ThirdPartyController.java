package com.example.midtermproject.controller.impl;

import com.example.midtermproject.model.Users.ThirdParty;
import com.example.midtermproject.repository.ThirdPartyRepository;
import com.example.midtermproject.service.interfaces.IThirdPartyService;
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
    private IThirdPartyService thirdPartyService;


    @PostMapping("/user/thirdparty")
    @ResponseStatus(HttpStatus.CREATED)
    public ThirdParty newThirdParty(@RequestBody @Valid ThirdParty thirdParty){
        return thirdPartyService.newThirdParty(thirdParty);
    }
}
