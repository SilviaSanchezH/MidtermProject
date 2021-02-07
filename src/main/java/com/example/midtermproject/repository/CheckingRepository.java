package com.example.midtermproject.repository;

import com.example.midtermproject.model.Accounts.Checking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CheckingRepository extends JpaRepository <Checking, Integer> {
}
