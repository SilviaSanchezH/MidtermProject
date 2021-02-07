package com.example.midtermproject.controller.impl;

import com.example.midtermproject.controller.dto.ThirdPartyTransactionDTO;
import com.example.midtermproject.controller.dto.TransactionDTO;
import com.example.midtermproject.service.interfaces.ITransactionService;
import com.sun.istack.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@RestController
public class TransactionController {

    @Autowired
    private ITransactionService transactionService;

    @PostMapping("/transaction")
    @ResponseStatus(HttpStatus.OK)
    public void newTransaction(@RequestBody @Valid TransactionDTO transactionDTO, Principal principal) {
        transactionService.newTransaction(transactionDTO, principal.getName());
    }

    @PostMapping("/transaction/thirdparty")
    @ResponseStatus(HttpStatus.OK)
    public void newThirdPartyTransaction(@RequestBody @Valid ThirdPartyTransactionDTO thirdPartyTransactionDTO, @RequestHeader("HASHED_KEY") @NotNull String hashedKey) {
        // TODO: Verificar que se accede de esa forma al campo del header
        transactionService.newFromThirdPartyTransaction(thirdPartyTransactionDTO, hashedKey);
    }

}
