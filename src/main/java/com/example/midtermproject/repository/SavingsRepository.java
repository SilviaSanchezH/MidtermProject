package com.example.midtermproject.repository;

import com.example.midtermproject.model.Accounts.Savings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SavingsRepository extends JpaRepository<Savings, Integer> {
}
