package com.example.midtermproject.service.impl;

import com.example.midtermproject.controller.dto.ThirdPartyTransactionDTO;
import com.example.midtermproject.controller.dto.TransactionDTO;
import com.example.midtermproject.enums.Status;
import com.example.midtermproject.model.Accounts.*;
import com.example.midtermproject.model.Users.ThirdParty;
import com.example.midtermproject.model.Users.User;
import com.example.midtermproject.model.shared.Money;
import com.example.midtermproject.repository.AccountRepository;
import com.example.midtermproject.repository.ThirdPartyRepository;
import com.example.midtermproject.repository.TransactionRepository;
import com.example.midtermproject.repository.UserRepository;
import com.example.midtermproject.service.interfaces.ITransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.*;

@Service
public class TransactionService implements ITransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ThirdPartyRepository thirdPartyRepository;

    @Override
    public Transaction newTransaction(TransactionDTO transactionDTO, String userName) {
        User loggedUser = userRepository.findByUsername(userName).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You must be logged in"));
        Account originAccount = accountRepository.findById(transactionDTO.getOriginAccount()).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not valid origin account"));
        Optional<Account> destinationAccount = Optional.empty();
        Optional<ThirdParty> destinationThirdParty = Optional.empty();

        if(transactionDTO.getDestinationAccount() != null && transactionDTO.getThirdPartyDestinationHashedKey() == null) {
            destinationAccount = accountRepository.findById(transactionDTO.getDestinationAccount());
        } else if(transactionDTO.getThirdPartyDestinationHashedKey() != null && transactionDTO.getDestinationAccount() == null) {
            destinationThirdParty = thirdPartyRepository.findByHashedKey(transactionDTO.getThirdPartyDestinationHashedKey());
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Transaction cannot have more than one destination");
        }

        if(checkAccountOwner(originAccount, loggedUser) && checkSufficientFunds(originAccount, transactionDTO.getQuantity())) {
            if(destinationAccount.isPresent()) {
                Transaction transaction = new Transaction(new Money(transactionDTO.getQuantity(), Currency.getInstance(transactionDTO.getCurrency())),
                        originAccount, destinationAccount.get());

                checkFraud(transaction);
                boolean processedTransaction = processLocalTransaction(transaction);
                if(transaction.getOriginAccount() instanceof Savings) transaction.setOriginAccount(applyPenaltyFee((Savings) transaction.getOriginAccount()));
                if(transaction.getOriginAccount() instanceof Checking) transaction.setOriginAccount(applyPenaltyFee((Checking) transaction.getOriginAccount()));
                if(processedTransaction) return transactionRepository.save(transaction);
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "The transaction cannot be processed");
            } else if(destinationThirdParty.isPresent()) {
                Transaction transaction = new Transaction(new Money(transactionDTO.getQuantity(), Currency.getInstance(transactionDTO.getCurrency())),
                        originAccount, destinationThirdParty.get());

                checkFraud(transaction);
                boolean processedTransaction = processToThirdPartyTransaction(transaction);
                if(transaction.getOriginAccount() instanceof Savings) transaction.setOriginAccount(applyPenaltyFee((Savings) transaction.getOriginAccount()));
                if(transaction.getOriginAccount() instanceof Checking) transaction.setOriginAccount(applyPenaltyFee((Checking) transaction.getOriginAccount()));
                if(processedTransaction) return transactionRepository.save(transaction);
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "The transaction cannot be processed");
            }
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not valid destination account");
    }

    @Override
    public Transaction newFromThirdPartyTransaction(ThirdPartyTransactionDTO thirdPartyTransactionDTO, String hashedKey) {
        ThirdParty thirdParty = thirdPartyRepository.findByHashedKey(hashedKey).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not valid hashed key"));
        Account destinationAccount = accountRepository.findById(thirdPartyTransactionDTO.getAccountId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not valid destination account"));
        if(!(destinationAccount instanceof CreditCard)) {
            String destinationSecretKey = null;
            if(destinationAccount instanceof Checking) destinationSecretKey = ((Checking) destinationAccount).getSecretKey();
            if(destinationAccount instanceof Savings) destinationSecretKey = ((Savings) destinationAccount).getSecretKey();
            if(destinationAccount instanceof StudentChecking) destinationSecretKey = ((StudentChecking) destinationAccount).getSecretKey();
            if(destinationSecretKey != null && destinationSecretKey.equals(thirdPartyTransactionDTO.getSecretKey())) {
                Transaction transaction = new Transaction(new Money(thirdPartyTransactionDTO.getAmount()), thirdParty, destinationAccount);
                boolean processedTransaction = processFromThirdPartyTransaction(transaction);
                if(processedTransaction) return transactionRepository.save(transaction);
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "The transaction cannot be processed");
            }
        }
        return null;
    }

    private boolean checkAccountOwner(Account account, User user ) {
        if(account.getPrimaryOwner().getUsername().equals(user.getUsername()) || account.getSecondaryOwner().getUsername().equals(user.getUsername())) {
            return true;
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You cannot operate this account");
    }

    private boolean checkSufficientFunds(Account account, BigDecimal quantity) {
        if(account.getBalance().getAmount().compareTo(quantity) > 0) {
            return true;
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You don't have sufficient funds");
    }

    private void checkFraud(Transaction transaction) {
        Calendar oneSecondBefore = Calendar.getInstance();
        oneSecondBefore.setTimeInMillis(transaction.getTransactionDate().getTime());
        oneSecondBefore.add(Calendar.SECOND, -1);

        Calendar oneDayBefore = Calendar.getInstance();
        oneSecondBefore.setTimeInMillis(transaction.getTransactionDate().getTime());
        oneSecondBefore.add(Calendar.DAY_OF_MONTH, -1);

        Calendar endDate = Calendar.getInstance();
        endDate.setTimeInMillis(transaction.getTransactionDate().getTime());

        // Transactions made in 24 hours that total to more than 150% of the customers highest daily total transactions in any other 24 hour period.
        Integer maxTransactions = transactionRepository.transactionsIn24HoursForAnyAccount();
        List<Transaction> oneDayTransactions = transactionRepository.findByTransactionDateBetween(transaction.getOriginAccount().getId(), oneDayBefore.getTime(), endDate.getTime());

        if(oneDayTransactions.size() > (maxTransactions * 1.5)) {
            freezeAccount(transaction.getOriginAccount());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Your account has been blocked by our fraud checking service");
        }

        // More than 2 transactions occurring on a single account within a 1 second period.
        List<Transaction> transactionList = transactionRepository.findByTransactionDateBetween(transaction.getOriginAccount().getId(), oneSecondBefore.getTime(), endDate.getTime());

        if(transactionList.size() > 0) {
            freezeAccount(transaction.getOriginAccount());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Your account has been blocked by our fraud checking service");
        }
    }

    private void freezeAccount(Account account) {
        if(account instanceof Checking) ((Checking) account).setStatus(Status.FROZEN);
        if(account instanceof Savings) ((Savings) account).setStatus(Status.FROZEN);
        if(account instanceof StudentChecking) ((StudentChecking) account).setStatus(Status.FROZEN);
        accountRepository.save(account);
    }

    private boolean processLocalTransaction(Transaction transaction) {
        // TODO: Puede que se necesite guardar el cambio de cantidad en cada cuenta por separado llamando a su repositorio

        BigDecimal originCurrentBalance = transaction.getOriginAccount().getBalance().getAmount();
        BigDecimal destinationCurrentBalance = transaction.getDestinationAccount().getBalance().getAmount();

        BigDecimal originNewBalance = originCurrentBalance.subtract(transaction.getValue().getAmount());
        BigDecimal destinationNewBalance = destinationCurrentBalance.add(transaction.getValue().getAmount());

        transaction.getOriginAccount().setBalance(new Money(originNewBalance, Currency.getInstance(transaction.getOriginAccount().getBalance().getCurrency().getCurrencyCode())));
        transaction.getDestinationAccount().setBalance(new Money(destinationNewBalance, Currency.getInstance(transaction.getDestinationAccount().getBalance().getCurrency().getCurrencyCode())));

        return true;
    }

    private boolean processFromThirdPartyTransaction(Transaction transaction) {
        BigDecimal destinationCurrentBalance = transaction.getDestinationAccount().getBalance().getAmount();
        BigDecimal destinationNewBalance = destinationCurrentBalance.add(transaction.getValue().getAmount());
        transaction.getDestinationAccount().setBalance(new Money(destinationNewBalance, Currency.getInstance(transaction.getDestinationAccount().getBalance().getCurrency().getCurrencyCode())));

        return true;
    }

    private boolean processToThirdPartyTransaction(Transaction transaction) {
        BigDecimal originCurrentBalance = transaction.getOriginAccount().getBalance().getAmount();
        BigDecimal originNewBalance = originCurrentBalance.subtract(transaction.getValue().getAmount());
        transaction.getOriginAccount().setBalance(new Money(originNewBalance, Currency.getInstance(transaction.getOriginAccount().getBalance().getCurrency().getCurrencyCode())));

        return true;
    }

    private Account applyPenaltyFee(Savings account){
        if(account.getBalance().getAmount().compareTo(account.getMinimumBalance().getAmount()) < 0){
            BigDecimal amount = account.getBalance().getAmount().subtract(account.getPenaltyFee().getAmount());
            account.setBalance(new Money(amount));
        }
        return account;
    }


    private Account applyPenaltyFee(Checking account) {
        if(account.getBalance().getAmount().compareTo(account.getMinimumBalance().getAmount()) < 0){
            BigDecimal amount = account.getBalance().getAmount().subtract(account.getPenaltyFee().getAmount());
            account.setBalance(new Money(amount));
        }
        return account;
    }

}
