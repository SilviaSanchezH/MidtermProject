package com.example.midtermproject.service.interfaces;

import com.example.midtermproject.controller.dto.ThirdPartyTransactionDTO;
import com.example.midtermproject.controller.dto.TransactionDTO;
import com.example.midtermproject.model.Accounts.Transaction;

public interface ITransactionService {
    public Transaction newTransaction(TransactionDTO transactionDTO, String userName);
    public Transaction newFromThirdPartyTransaction(ThirdPartyTransactionDTO thirdPartyTransactionDTO, String hashedKey);
}
