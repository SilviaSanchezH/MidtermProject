package com.example.midtermproject.repository;

import com.example.midtermproject.model.Users.ThirdParty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ThirdPartyRepository extends JpaRepository <ThirdParty, Integer> {
    public Optional<ThirdParty> findByHashedKey(String hashedKey);
    public Optional<ThirdParty> findByName(String name);
}
