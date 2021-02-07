package com.example.midtermproject.repository;

import com.example.midtermproject.model.Users.AccountHolder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountHolderRepository extends JpaRepository<AccountHolder, Integer> {
}
