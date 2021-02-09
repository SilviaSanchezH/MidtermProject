package com.example.midtermproject.service.interfaces;

import com.example.midtermproject.controller.dto.AccountHolderDTO;
import com.example.midtermproject.model.Users.AccountHolder;

public interface IAccountHolderService {
    public AccountHolder newAccountHolder(AccountHolderDTO accountHolderDTO);
}
