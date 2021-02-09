package com.example.midtermproject.service.interfaces;

import com.example.midtermproject.controller.dto.SavingsDTO;
import com.example.midtermproject.model.Accounts.Savings;

public interface ISavingsService {
    public Savings newSaving(SavingsDTO savingsDTO);
}
