package com.example.midtermproject.service.impl;

import com.example.midtermproject.enums.RoleEnum;
import com.example.midtermproject.model.Users.Role;
import com.example.midtermproject.model.Users.ThirdParty;
import com.example.midtermproject.repository.ThirdPartyRepository;
import com.example.midtermproject.service.interfaces.IThirdPartyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class ThirdPartyService implements IThirdPartyService {

    @Autowired
    private ThirdPartyRepository thirdPartyRepository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public ThirdParty newThirdParty(ThirdParty thirdParty) {
        String password = passwordEncoder.encode(thirdParty.getHashedKey());
        thirdParty.setHashedKey(password);
        return thirdPartyRepository.save(thirdParty);
    }
}
