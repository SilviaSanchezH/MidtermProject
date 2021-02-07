package com.example.midtermproject.repository;

import com.example.midtermproject.model.Accounts.StudentChecking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentCheckingRepository extends JpaRepository <StudentChecking, Integer> {
}
