package com.example.midtermproject.controller.dto;

import com.example.midtermproject.model.Accounts.Account;
import com.example.midtermproject.model.Users.Role;
import com.example.midtermproject.model.shared.Address;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.sun.istack.NotNull;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public class AccountHolderDTO {

    @NotNull
    private String username;
    @NotNull
    private String password;
    @NotNull
    private String name;
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @NotNull
    private LocalDate birth;
    @NotNull
    private String primaryStreet;
    @NotNull
    private String primaryCity;
    @NotNull
    private String primaryPostalCode;

    private String mailingStreet;
    private String mailingCity;
    private String mailingPostalCode;

    public AccountHolderDTO() {
    }

    public AccountHolderDTO(String username, String password, String name, LocalDate birth, String primaryStreet, String primaryCity, String primaryPostalCode, String mailingStreet, String mailingCity, String mailingPostalCode) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.birth = birth;
        this.primaryStreet = primaryStreet;
        this.primaryCity = primaryCity;
        this.primaryPostalCode = primaryPostalCode;
        this.mailingStreet = mailingStreet;
        this.mailingCity = mailingCity;
        this.mailingPostalCode = mailingPostalCode;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getBirth() {
        return birth;
    }

    public void setBirth(LocalDate birth) {
        this.birth = birth;
    }

    public String getPrimaryStreet() {
        return primaryStreet;
    }

    public void setPrimaryStreet(String primaryStreet) {
        this.primaryStreet = primaryStreet;
    }

    public String getPrimaryCity() {
        return primaryCity;
    }

    public void setPrimaryCity(String primaryCity) {
        this.primaryCity = primaryCity;
    }

    public String getPrimaryPostalCode() {
        return primaryPostalCode;
    }

    public void setPrimaryPostalCode(String primaryPostalCode) {
        this.primaryPostalCode = primaryPostalCode;
    }

    public String getMailingStreet() {
        return mailingStreet;
    }

    public void setMailingStreet(String mailingStreet) {
        this.mailingStreet = mailingStreet;
    }

    public String getMailingCity() {
        return mailingCity;
    }

    public void setMailingCity(String mailingCity) {
        this.mailingCity = mailingCity;
    }

    public String getMailingPostalCode() {
        return mailingPostalCode;
    }

    public void setMailingPostalCode(String mailingPostalCode) {
        this.mailingPostalCode = mailingPostalCode;
    }

}
