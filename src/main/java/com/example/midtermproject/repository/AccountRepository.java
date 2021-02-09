package com.example.midtermproject.repository;

import com.example.midtermproject.model.Accounts.Account;
import com.example.midtermproject.model.Users.AccountHolder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {
    public List<Account> findByPrimaryOwner(AccountHolder accountHolder);
}
