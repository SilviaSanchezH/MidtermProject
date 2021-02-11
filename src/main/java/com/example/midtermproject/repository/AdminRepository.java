package com.example.midtermproject.repository;

import com.example.midtermproject.model.Users.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Integer> {
    public List<Admin> findByName(String name);
}
