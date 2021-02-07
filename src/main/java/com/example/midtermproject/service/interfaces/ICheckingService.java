package com.example.midtermproject.service.interfaces;

import com.example.midtermproject.controller.dto.CheckingDTO;
import com.example.midtermproject.model.Accounts.Account;
import com.example.midtermproject.model.Accounts.Checking;
import com.example.midtermproject.model.Accounts.StudentChecking;

public interface ICheckingService {
    public Account newChecking(CheckingDTO checkingDTO);
}
