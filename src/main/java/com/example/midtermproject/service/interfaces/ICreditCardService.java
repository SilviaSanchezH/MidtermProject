package com.example.midtermproject.service.interfaces;

import com.example.midtermproject.controller.dto.CreditCardDTO;
import com.example.midtermproject.model.Accounts.CreditCard;

public interface ICreditCardService {
    public CreditCard newCreditCard(CreditCardDTO creditCardDTO);
}
