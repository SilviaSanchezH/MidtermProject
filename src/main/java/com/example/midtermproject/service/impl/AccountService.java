package com.example.midtermproject.service.impl;

import com.example.midtermproject.controller.dto.BalanceDTO;
import com.example.midtermproject.enums.RoleEnum;
import com.example.midtermproject.model.Accounts.Account;
import com.example.midtermproject.model.Accounts.CreditCard;
import com.example.midtermproject.model.Accounts.Savings;
import com.example.midtermproject.model.Users.Role;
import com.example.midtermproject.model.Users.User;
import com.example.midtermproject.model.shared.Money;
import com.example.midtermproject.repository.AccountRepository;
import com.example.midtermproject.repository.UserRepository;
import com.example.midtermproject.service.interfaces.IAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.util.Currency;
import java.util.Optional;


@Service
public class AccountService implements IAccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    //This method allows to the admin update the balance account
    public void updateBalance(Integer id, BigDecimal amount, String currency) {
        Optional<Account> account = accountRepository.findById(id);
        if(account.isPresent()){
            account.get().setBalance(new Money(amount, Currency.getInstance(currency)));
            accountRepository.save(account.get());
        }else{
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "La cuenta con el ID " + id + "no existe");
        }
    }

    @Override
    //This method return the account corresponding to the id
    public Account getAccount(Integer id, String userName) {
        Account account = accountRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account for that id doesn't exists"));
        User user = userRepository.findByUsername(userName).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Not valid userName"));
        boolean isAdmin = user.getRoles().stream().anyMatch(role -> role.getName().equals(RoleEnum.ADMIN));
        if(isAdmin || account.getPrimaryOwner().getUsername().equals(userName) || (account.getSecondaryOwner() != null && account.getSecondaryOwner().getUsername().equals(userName))) {
            if(account instanceof Savings) account = addSavingsAccountInterest((Savings) account);
            if(account instanceof CreditCard) account = addCreditCardInterest((CreditCard) account);
            return account;
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You don't have access to this account");
    }

    @Override
    //This method return the account balance corresponding to the id.
    public BalanceDTO getAccountBalance(Integer id, String userName) {
        Account account = accountRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account for that id doesn't exists"));
        User user = userRepository.findByUsername(userName).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Not valid userName"));
        boolean isAdmin = user.getRoles().stream().anyMatch(role -> role.getName().equals(RoleEnum.ADMIN));
        if(isAdmin || account.getPrimaryOwner().getUsername().equals(userName) || (account.getSecondaryOwner() != null && account.getSecondaryOwner().getUsername().equals(userName))) {
            if(account instanceof Savings) account = addSavingsAccountInterest((Savings) account);
            if(account instanceof CreditCard) account = addCreditCardInterest((CreditCard) account);
            return new BalanceDTO(account.getBalance().getAmount(), account.getBalance().getCurrency().getCurrencyCode());
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You don't have access to this account");
    }

    //Interest on savings accounts is added to the account annually at the rate of specified interestRate per year.
    private Account addSavingsAccountInterest(Savings account) {
        //Time since the account was created until now.
        int yearsSinceCreation = Period.between(account.getCreatedAt(), LocalDate.now()).getYears();
        int yearsToAdd = yearsSinceCreation;

        //Check if if interest have ever been added and calculate the time until now.
        if(account.getInterestAdditionDate() != null) {
            int yearsSinceLastAddition = Period.between(account.getInterestAdditionDate(), LocalDate.now()).getYears();
            yearsToAdd = Math.min(yearsSinceLastAddition, yearsSinceCreation);
        }
        BigDecimal interestRate = account.getInterestRate();
        BigDecimal amount = account.getBalance().getAmount();

        //Add interest to the saving account
        for(int i = 0; i < yearsToAdd; i++) {
            amount = amount.add(amount.multiply(interestRate));
        }

        account.setInterestAdditionDate(LocalDate.now());
        account.setBalance(new Money(amount));
        return accountRepository.save(account);
    }

    ////Interest on savings accounts is added to the account annually at the rate of specified interestRate per month.
    private Account addCreditCardInterest(CreditCard account) {
        //Time since the account was created until now.
        int monthsSinceCreation = Period.between(account.getCreatedAt(), LocalDate.now()).getMonths();
        int monthsToAdd = monthsSinceCreation;

        //Check if if interest have ever been added and calculate the time until now.
        if(account.getInterestAdditionDate() != null) {
            int monthsSinceLastAddition = Period.between(account.getInterestAdditionDate(), LocalDate.now()).getMonths();
            monthsToAdd = Math.min(monthsSinceCreation, monthsSinceLastAddition);
        }
        BigDecimal interestRate = account.getInterestRate().divide(new BigDecimal("12"), 5, RoundingMode.HALF_UP);
        BigDecimal amount = account.getBalance().getAmount();

        //Add interest to the saving account
        for(int i = 0; i < monthsToAdd; i++){
            amount = amount.add(amount.multiply(interestRate));
        }

        account.setInterestAdditionDate(LocalDate.now());
        account.setBalance(new Money(amount));
        return accountRepository.save(account);
    }

}
