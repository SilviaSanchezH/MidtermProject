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
import org.springframework.security.config.annotation.authentication.configurers.ldap.LdapAuthenticationProviderConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    //This method allows to do a new transaction to other account or third party
    public Transaction newTransaction(TransactionDTO transactionDTO, String userName) {
        User loggedUser = userRepository.findByUsername(userName).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You must be logged in"));
        Account originAccount = accountRepository.findById(transactionDTO.getOriginAccount()).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not valid origin account"));
        Optional<Account> destinationAccount = Optional.empty();
        Optional<ThirdParty> destinationThirdParty = Optional.empty();

        //Check if the destination account is a third party or an account.
        if(transactionDTO.getDestinationAccount() != null && transactionDTO.getThirdPartyDestinationId() == null) {
            destinationAccount = accountRepository.findById(transactionDTO.getDestinationAccount());
        } else if(transactionDTO.getThirdPartyDestinationId() != null && transactionDTO.getDestinationAccount() == null) {
            destinationThirdParty = thirdPartyRepository.findById(transactionDTO.getThirdPartyDestinationId());
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Transaction cannot have more than one destination");
        }

        //Check if the user is the owner of the origin account and if he has sufficients funds and not is blocked and finally do the transaction.
        if(checkAccountOwner(originAccount, loggedUser) && checkSufficientFundsAndNotBlocked(originAccount, transactionDTO.getQuantity())) {
            if(destinationAccount.isPresent() && checkDestinationOwnerName(destinationAccount.get(), transactionDTO.getDestinationOwnerName())) {
                Transaction transaction = new Transaction(new Money(transactionDTO.getQuantity(), Currency.getInstance(transactionDTO.getCurrency())),
                        originAccount, destinationAccount.get());

                checkFraud(transaction);
                return processLocalTransaction(transaction);
            } else if(destinationThirdParty.isPresent() && checkDestinationOwnerNameThirdParty(destinationThirdParty.get(), transactionDTO.getDestinationOwnerName())) {
                Transaction transaction = new Transaction(new Money(transactionDTO.getQuantity(), Currency.getInstance(transactionDTO.getCurrency())),
                        originAccount, destinationThirdParty.get());

                checkFraud(transaction);
                return processToThirdPartyTransaction(transaction);
            }
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not valid destination account");
    }

    @Override
    //This method allows thirdparty do transactions to other account.
    public Transaction newFromThirdPartyTransaction(ThirdPartyTransactionDTO thirdPartyTransactionDTO, String hashedKey) {
        ThirdParty thirdParty = thirdPartyRepository.findById(thirdPartyTransactionDTO.getThirdPartyId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not valid hashed key"));
        Account destinationAccount = accountRepository.findById(thirdPartyTransactionDTO.getAccountId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not valid destination account"));
        //Compare if the hashed key that introduce the thirdparty exits in the database.
        if(!passwordEncoder.matches(hashedKey, thirdParty.getHashedKey())) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Your hashed key is not valid");
        if(!(destinationAccount instanceof CreditCard)) {
            String destinationSecretKey = null;

            if(destinationAccount instanceof Checking) destinationSecretKey =  ((Checking) destinationAccount).getSecretKey();
            if(destinationAccount instanceof Savings) destinationSecretKey = ((Savings) destinationAccount).getSecretKey();
            if(destinationAccount instanceof StudentChecking) destinationSecretKey = ((StudentChecking) destinationAccount).getSecretKey();

            if(destinationSecretKey != null && destinationSecretKey.equals(thirdPartyTransactionDTO.getSecretKey())) {
                Transaction transaction = new Transaction(new Money(thirdPartyTransactionDTO.getAmount()), thirdParty, destinationAccount);
                return processFromThirdPartyTransaction(transaction);
            }else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The secret key don't match with the destination account");
            }
        }
        return null;
    }

    //This method check if the user is the owner account.
    private boolean checkAccountOwner(Account account, User user ) {
        if(account.getPrimaryOwner().getUsername().equals(user.getUsername()) ||
                (account.getSecondaryOwner() != null && account.getSecondaryOwner().getUsername().equals(user.getUsername()))) {
            return true;
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You cannot operate this account");
    }

    //TODO NO SE SI ESTO ESTA BIEN
    private boolean checkDestinationOwnerName(Account account, String destinationName) {
        if (account.getPrimaryOwner().getName().equals(destinationName) ||
                (account.getSecondaryOwner() != null && account.getSecondaryOwner().getName().equals(destinationName))) {
            return true;
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The destination owner name doesn't match with the destinatition account Id");
    }

    private boolean checkDestinationOwnerNameThirdParty(ThirdParty thirdParty, String destinationName){
        if(thirdParty.getName().equals(destinationName)){
            return true;
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The destination owner name doesn't match with the destinatition third party Id");
    }

    //This method check if the account is not blocked and has sufficient funds.
    private boolean checkSufficientFundsAndNotBlocked(Account account, BigDecimal quantity) {
        boolean blocked = false;
        if(account instanceof Checking) blocked = ((Checking) account).getStatus().equals(Status.FROZEN);
        if(account instanceof Savings) blocked = ((Savings) account).getStatus().equals(Status.FROZEN);
        if(account instanceof StudentChecking) blocked = ((StudentChecking) account).getStatus().equals(Status.FROZEN);

        if(account.getBalance().getAmount().compareTo(quantity) >= 0 && !blocked) {
            return true;
        }
        if(blocked) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Your account is blocked");
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You don't have sufficient funds");
    }


    private void checkFraud(Transaction transaction) {
        Calendar oneSecondBefore = Calendar.getInstance();
        oneSecondBefore.setTimeInMillis(transaction.getTransactionDate().getTime());
        oneSecondBefore.add(Calendar.SECOND, -1);

        Calendar oneDayBefore = Calendar.getInstance();
        oneDayBefore.setTimeInMillis(transaction.getTransactionDate().getTime());
        oneDayBefore.add(Calendar.DAY_OF_MONTH, -1);

        Calendar endDate = Calendar.getInstance();
        endDate.setTimeInMillis(transaction.getTransactionDate().getTime());

        //Check the transactions made in 24 hours that total to more than 150% of the customers highest daily total transactions in any other 24 hour period.
        //First, we get the max amount that the account transfers in 24h.
        BigDecimal maxTransactions = transactionRepository.transactionsValueInAny24Hours(transaction.getOriginAccount().getId());
        //Get the value of the transactions that make the account in 24h.
        BigDecimal oneDayTransactionsValue = transactionRepository.transactionsValueInRange(transaction.getOriginAccount().getId(), oneDayBefore.getTime(), endDate.getTime());

        //Add the value of the current transaction to the total value of transactions in the day for this account and then check if its bigger than 150% of max value.
        if(oneDayTransactionsValue != null) oneDayTransactionsValue = oneDayTransactionsValue.add(transaction.getValue().getAmount());
        if(maxTransactions != null && oneDayTransactionsValue != null && oneDayTransactionsValue.compareTo(maxTransactions.multiply(new BigDecimal("1.5"))) > 0) {
            freezeAccount(transaction.getOriginAccount());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Your account has been blocked by our fraud checking service");
        }

        // Check if more than 2 transactions occurring on a single account within a 1 second period.
        List<Transaction> transactionList = transactionRepository.findByTransactionDateBetween(transaction.getOriginAccount().getId(), oneSecondBefore.getTime(), endDate.getTime());

        if(transactionList.size() > 0) {
            freezeAccount(transaction.getOriginAccount());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Your account has been blocked by our fraud checking service | More than one operation in one second");
        }
    }

    //Check if the originAccount of the transaction is frozen.
    private void freezeAccount(Account account) {
        if(account instanceof Checking) ((Checking) account).setStatus(Status.FROZEN);
        if(account instanceof Savings) ((Savings) account).setStatus(Status.FROZEN);
        if(account instanceof StudentChecking) ((StudentChecking) account).setStatus(Status.FROZEN);
        accountRepository.save(account);
    }

    //This method subtract the value of the transaction to the origin account and add the same value to the destination account.
    private Transaction processLocalTransaction(Transaction transaction) {
        BigDecimal originCurrentBalance = transaction.getOriginAccount().getBalance().getAmount();
        BigDecimal destinationCurrentBalance = transaction.getDestinationAccount().getBalance().getAmount();

        BigDecimal originNewBalance = originCurrentBalance.subtract(transaction.getValue().getAmount());
        BigDecimal destinationNewBalance = destinationCurrentBalance.add(transaction.getValue().getAmount());

        transaction.getOriginAccount().setBalance(new Money(originNewBalance, Currency.getInstance(transaction.getOriginAccount().getBalance().getCurrency().getCurrencyCode())));
        transaction.getDestinationAccount().setBalance(new Money(destinationNewBalance, Currency.getInstance(transaction.getDestinationAccount().getBalance().getCurrency().getCurrencyCode())));

        accountRepository.saveAll(List.of(transaction.getOriginAccount(), transaction.getDestinationAccount()));

        if(transaction.getOriginAccount() instanceof Savings) transaction.setOriginAccount(applyPenaltyFee((Savings) transaction.getOriginAccount()));
        if(transaction.getOriginAccount() instanceof Checking) transaction.setOriginAccount(applyPenaltyFee((Checking) transaction.getOriginAccount()));
        return transactionRepository.save(transaction);
    }

    //This method add the value of the transaction to the destination account when the sender is a thirdparty.
    private Transaction processFromThirdPartyTransaction(Transaction transaction) {
        BigDecimal destinationCurrentBalance = transaction.getDestinationAccount().getBalance().getAmount();
        BigDecimal destinationNewBalance = destinationCurrentBalance.add(transaction.getValue().getAmount());
        transaction.getDestinationAccount().setBalance(new Money(destinationNewBalance, Currency.getInstance(transaction.getDestinationAccount().getBalance().getCurrency().getCurrencyCode())));

        accountRepository.save(transaction.getDestinationAccount());

        return transactionRepository.save(transaction);
    }

    //This method subtract the value of the transaction to the origin account when the receiver is a thirdparty.
    private Transaction processToThirdPartyTransaction(Transaction transaction) {
        BigDecimal originCurrentBalance = transaction.getOriginAccount().getBalance().getAmount();
        BigDecimal originNewBalance = originCurrentBalance.subtract(transaction.getValue().getAmount());
        transaction.getOriginAccount().setBalance(new Money(originNewBalance, Currency.getInstance(transaction.getOriginAccount().getBalance().getCurrency().getCurrencyCode())));

        accountRepository.save(transaction.getOriginAccount());

        if(transaction.getOriginAccount() instanceof Savings) transaction.setOriginAccount(applyPenaltyFee((Savings) transaction.getOriginAccount()));
        if(transaction.getOriginAccount() instanceof Checking) transaction.setOriginAccount(applyPenaltyFee((Checking) transaction.getOriginAccount()));
        return transactionRepository.save(transaction);
    }

    //If saving account drops below the minimumBalance, the penaltyFee should be deducted from the balance
    private Account applyPenaltyFee(Savings account){
        if(account.getBalance().getAmount().compareTo(account.getMinimumBalance().getAmount()) < 0){
            BigDecimal amount = account.getBalance().getAmount().subtract(account.getPenaltyFee().getAmount());
            account.setBalance(new Money(amount));
        }
        accountRepository.save(account);
        return account;
    }

    //If checking account drops below the minimumBalance, the penaltyFee should be deducted from the balance
    private Account applyPenaltyFee(Checking account) {
        if(account.getBalance().getAmount().compareTo(account.getMinimumBalance().getAmount()) < 0){
            BigDecimal amount = account.getBalance().getAmount().subtract(account.getPenaltyFee().getAmount());
            account.setBalance(new Money(amount));
        }
        accountRepository.save(account);
        return account;
    }

}
