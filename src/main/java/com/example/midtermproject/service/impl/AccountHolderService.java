package com.example.midtermproject.service.impl;

import com.example.midtermproject.controller.dto.AccountHolderDTO;
import com.example.midtermproject.enums.RoleEnum;
import com.example.midtermproject.model.Users.AccountHolder;
import com.example.midtermproject.model.Users.Role;
import com.example.midtermproject.model.shared.Address;
import com.example.midtermproject.repository.AccountHolderRepository;
import com.example.midtermproject.service.interfaces.IAccountHolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class AccountHolderService implements IAccountHolderService {

    @Autowired
    private AccountHolderRepository accountHolderRepository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    //Create a new account holder
    public AccountHolder newAccountHolder(AccountHolderDTO accountHolderDTO) {
        Address primaryAddress = new Address(accountHolderDTO.getPrimaryStreet(), accountHolderDTO.getPrimaryCity(), accountHolderDTO.getPrimaryPostalCode());
        Address mailingAddress = null;
        if(accountHolderDTO.getMailingStreet() != null && accountHolderDTO.getMailingCity() != null && accountHolderDTO.getMailingPostalCode() != null){
            mailingAddress = new Address(accountHolderDTO.getMailingStreet(), accountHolderDTO.getMailingCity(), accountHolderDTO.getMailingPostalCode());
        }

        String password = passwordEncoder.encode(accountHolderDTO.getPassword());

        AccountHolder accountHolder = new AccountHolder(accountHolderDTO.getUsername(), password, accountHolderDTO.getName(), accountHolderDTO.getBirth(), primaryAddress, mailingAddress);

        Set<Role> roles = new HashSet<>();
        roles.add(new Role(RoleEnum.ACCOUNT_HOLDER, accountHolder));
        accountHolder.setRoles(roles);

        return accountHolderRepository.save(accountHolder);
    }
}
