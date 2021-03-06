package com.example.midtermproject.controller.impl;

import com.example.midtermproject.controller.dto.SavingsDTO;
import com.example.midtermproject.enums.RoleEnum;
import com.example.midtermproject.model.Accounts.Account;
import com.example.midtermproject.model.Accounts.CreditCard;
import com.example.midtermproject.model.Accounts.Savings;
import com.example.midtermproject.model.Users.AccountHolder;

import com.example.midtermproject.model.Users.Admin;
import com.example.midtermproject.model.Users.Role;
import com.example.midtermproject.model.shared.Address;
import com.example.midtermproject.model.shared.Money;
import com.example.midtermproject.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysql.cj.protocol.x.XAuthenticationProvider;
import org.apache.logging.log4j.util.Base64Util;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.Base64Utils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.mvc.method.annotation.PrincipalMethodArgumentResolver;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.util.Currency;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class AccountControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private AccountHolderRepository accountHolderRepository;

    @Autowired
    private SavingsRepository savingsRepository;

    @Autowired
    CreditCardRepository creditCardRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AdminRepository adminRepository;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    public AccountHolder accountHolder;
    public AccountHolder accountHolder2;
    public AccountHolder accountHolder3;
    public Savings savings;
    public CreditCard creditCard;
    public CreditCard creditCard2;
    public Admin admin;


    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        Address primaryAddress = new Address("castellana", "madrid", "28888");
        Address secondaryAddress = new Address("goya", "madrid", "28976");
        accountHolder = new AccountHolder("Paco", "123", "Paco", LocalDate.of(1950, 9,8), primaryAddress, secondaryAddress);
        accountHolder2 = new AccountHolder("Paca", "123", "Paca", LocalDate.of(1955, 6,8), primaryAddress, secondaryAddress);
        accountHolder3 = new AccountHolder("Tous", "123", "Tous", LocalDate.of(1955, 6,8), primaryAddress, secondaryAddress);
        accountHolderRepository.saveAll(List.of(accountHolder, accountHolder2, accountHolder3));
        savings = new Savings (new Money(new BigDecimal("78000")) , accountHolder, accountHolder2, "owo", new Money(new BigDecimal("200")), new BigDecimal("0.2"));
        creditCard = new CreditCard(new Money(new BigDecimal("30000")), accountHolder2, accountHolder, new Money(new BigDecimal("60000")), new BigDecimal("0.1"));
        creditCard2 = new CreditCard(new Money(new BigDecimal("34000")), accountHolder3, null, new Money(new BigDecimal("60000")), new BigDecimal("0.1"));


        accountRepository.saveAll(List.of(savings, creditCard, creditCard2));

        admin = new Admin("admin", "123", "elAdministrador");
        Set<Role> roles = new HashSet<>();
        roles.add(new Role(RoleEnum.ADMIN, admin));
        admin.setRoles(roles);
        adminRepository.save(admin);
    }

    @AfterEach
    void tearDown() {
        accountRepository.deleteAll();
        accountHolderRepository.deleteAll();
        adminRepository.deleteAll();
    }

    @Test
    void getAccount_validAccountHolder_account() throws Exception{

        User user = new User("Paco", "123", AuthorityUtils.createAuthorityList("ACCOUNT_HOLDER"));
        TestingAuthenticationToken testingAuthenticationToken = new TestingAuthenticationToken(user,null);

                MvcResult result = this.mockMvc.perform(
                        get("/account/" + savings.getId()).principal(testingAuthenticationToken)
                ).andExpect(status().isOk()).andReturn();

                assertTrue(result.getResponse().getContentAsString().contains("Paco"));
    }

    @Test
    void getAccount_notValidAccountHolder_notAccount() throws Exception{

        User user = new User("Manola", "123", AuthorityUtils.createAuthorityList("ACCOUNT_HOLDER"));
        TestingAuthenticationToken testingAuthenticationToken = new TestingAuthenticationToken(user,null);

        MvcResult result = this.mockMvc.perform(
                get("/account/" + savings.getId()).principal(testingAuthenticationToken)
        ).andExpect(status().isNotFound()).andReturn();
    }

    @Test
    void getAccount_notValidAccountHolderThatExists_notAccount() throws Exception{

        User user = new User("Tous", "123", AuthorityUtils.createAuthorityList("ACCOUNT_HOLDER"));
        TestingAuthenticationToken testingAuthenticationToken = new TestingAuthenticationToken(user,null);

        MvcResult result = this.mockMvc.perform(
                get("/account/" + savings.getId()).principal(testingAuthenticationToken)
        ).andExpect(status().isUnauthorized()).andReturn();
    }

    @Test
    void getAccount_notValidAccountId_notAccount() throws Exception{

        User user = new User("Paco", "123", AuthorityUtils.createAuthorityList("ACCOUNT_HOLDER"));
        TestingAuthenticationToken testingAuthenticationToken = new TestingAuthenticationToken(user,null);

        MvcResult result = this.mockMvc.perform(
                get("/account/" + savings.getId()+520).principal(testingAuthenticationToken)
        ).andExpect(status().isNotFound()).andReturn();
    }

    @Test
    void getAccount_notValidAccountIdThatExists_notAccount() throws Exception{

        User user = new User("Paco", "123", AuthorityUtils.createAuthorityList("ACCOUNT_HOLDER"));
        TestingAuthenticationToken testingAuthenticationToken = new TestingAuthenticationToken(user,null);

        MvcResult result = this.mockMvc.perform(
                get("/account/" + creditCard2.getId()).principal(testingAuthenticationToken)
        ).andExpect(status().isUnauthorized()).andReturn();
    }

    @Test
    void getAccount_ValidAdmin_account() throws Exception{
        User user = new User("admin", "123", AuthorityUtils.createAuthorityList("ADMIN"));
        TestingAuthenticationToken testingAuthenticationToken = new TestingAuthenticationToken(user,null);

        MvcResult result = this.mockMvc.perform(
                get("/account/" + savings.getId()).principal(testingAuthenticationToken)
        ).andExpect(status().isOk()).andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("Paco"));
    }

    @Test
    void getAccount_notvalidAdmin_notAccount() throws Exception{
        User user = new User("administrador", "123", AuthorityUtils.createAuthorityList("ADMIN"));
        TestingAuthenticationToken testingAuthenticationToken = new TestingAuthenticationToken(user,null);

        MvcResult result = this.mockMvc.perform(
                get("/account/" + savings.getId()).principal(testingAuthenticationToken)
        ).andExpect(status().isNotFound()).andReturn();
    }


    @Test
    void getAccountBalance_ValidAccountHolder_account() throws Exception{
        User user = new User("Paco", "123", AuthorityUtils.createAuthorityList("ACCOUNT_HOLDER"));
        TestingAuthenticationToken testingAuthenticationToken = new TestingAuthenticationToken(user,null);

        MvcResult result = this.mockMvc.perform(
                get("/account/balance/" + savings.getId()).principal(testingAuthenticationToken)
        ).andExpect(status().isOk()).andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("78000"));
    }

    @Test
    void getAccountBalance_notValidAccountHolder_notGetAccount() throws Exception{
        User user = new User("Manola", "123", AuthorityUtils.createAuthorityList("ACCOUNT_HOLDER"));
        TestingAuthenticationToken testingAuthenticationToken = new TestingAuthenticationToken(user,null);

        Account account = accountRepository.findByPrimaryOwner(accountHolderRepository.findByName("Paco").get(0)).get(0);

        MvcResult result = this.mockMvc.perform(
                get("/account/balance/" + account.getId()).principal(testingAuthenticationToken)
                //    .header("HASHED_KEY", "contraseñadelthirdparty")
        ).andExpect(status().isNotFound()).andReturn();
    }

    @Test
    void getAccountBalance_notValidAccountHolderThatExists_notGetAccount() throws Exception{
        User user = new User("Tous", "123", AuthorityUtils.createAuthorityList("ACCOUNT_HOLDER"));
        TestingAuthenticationToken testingAuthenticationToken = new TestingAuthenticationToken(user,null);

        MvcResult result = this.mockMvc.perform(
                get("/account/balance/" + savings.getId()).principal(testingAuthenticationToken)
                //    .header("HASHED_KEY", "contraseñadelthirdparty")
        ).andExpect(status().isUnauthorized()).andReturn();
    }

    @Test
    void getAccountBalance_ValidAdmin_account() throws Exception{
        User user = new User("admin", "123", AuthorityUtils.createAuthorityList("ADMIN"));
        TestingAuthenticationToken testingAuthenticationToken = new TestingAuthenticationToken(user,null);

        MvcResult result = this.mockMvc.perform(
                get("/account/balance/" + savings.getId()).principal(testingAuthenticationToken)
        ).andExpect(status().isOk()).andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("78000"));
    }

    @Test
    void getAccountBalance_notValidAdmin_account() throws Exception{
        User user = new User("administrador", "123", AuthorityUtils.createAuthorityList("ADMIN"));
        TestingAuthenticationToken testingAuthenticationToken = new TestingAuthenticationToken(user,null);

        MvcResult result = this.mockMvc.perform(
                get("/account/balance/" + savings.getId()).principal(testingAuthenticationToken)
        ).andExpect(status().isNotFound()).andReturn();
    }

    @Test
    void getAccountBalance_notValidAccountId_notGetAccount() throws Exception{
        User user = new User("admin", "123", AuthorityUtils.createAuthorityList("ADMIN"));
        TestingAuthenticationToken testingAuthenticationToken = new TestingAuthenticationToken(user,null);

        MvcResult result = this.mockMvc.perform(
                get("/account/balance/" + savings.getId()+698).principal(testingAuthenticationToken)
        ).andExpect(status().isNotFound()).andReturn();
    }

    @Test
    void getAccountBalance_notValidAccountIdThatExists_notGetAccount() throws Exception{
        User user = new User("Paco", "123", AuthorityUtils.createAuthorityList("ACCOUNT_HOLDER"));
        TestingAuthenticationToken testingAuthenticationToken = new TestingAuthenticationToken(user,null);

        MvcResult result = this.mockMvc.perform(
                get("/account/balance/" + creditCard2.getId()).principal(testingAuthenticationToken)
        ).andExpect(status().isUnauthorized()).andReturn();
    }

    @Test
    void updateBalance_validAccountId_update() throws Exception{

        String body = "{\"amount\": 6000, \"currency\": \"USD\"}";
        MvcResult result = mockMvc.perform(
                patch("/account/balance/" + savings.getId())
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent()).andReturn();

        assertTrue((new BigDecimal("6000")).compareTo(accountRepository.findByPrimaryOwner(accountHolder).get(0).getBalance().getAmount())==0);
    }


    @Test
    void updateBalance_notValidAccountId_notUpdate() throws Exception{

        String body = "{\"amount\": 6000, \"currency\": \"USD\"}";
        MvcResult result = mockMvc.perform(
                patch("/account/balance/" + savings.getId()+600)
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound()).andReturn();
    }


    @Test
    void getAccount_addInterestSavingsValidUser_account() throws Exception{
        User user = new User("Paco", "123", AuthorityUtils.createAuthorityList("ACCOUNT_HOLDER"));
        TestingAuthenticationToken testingAuthenticationToken = new TestingAuthenticationToken(user,null);

        savings.setCreatedAt(LocalDate.of(2020, 2, 8));
        accountRepository.save(savings);

        MvcResult result = this.mockMvc.perform(
                get("/account/" + savings.getId()).principal(testingAuthenticationToken)
        ).andExpect(status().isOk()).andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("93600"));
        assertEquals((new BigDecimal("93600")).compareTo(accountRepository.findByPrimaryOwner(accountHolder).get(0).getBalance().getAmount()), 0);
    }

    @Test
    void getAccount_addInterestSavingsValidAdmin_account() throws Exception{
        User user = new User("admin", "123", AuthorityUtils.createAuthorityList("ADMIN"));
        TestingAuthenticationToken testingAuthenticationToken = new TestingAuthenticationToken(user,null);

        savings.setCreatedAt(LocalDate.of(2020, 2, 8));
        accountRepository.save(savings);

        MvcResult result = this.mockMvc.perform(
                get("/account/" + savings.getId()).principal(testingAuthenticationToken)
        ).andExpect(status().isOk()).andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("93600"));
        assertEquals((new BigDecimal("93600")).compareTo(accountRepository.findByPrimaryOwner(accountHolder).get(0).getBalance().getAmount()), 0);
    }

    @Test
    void getAccount_addInterestCreditCardValidUser_account() throws Exception{
        User user = new User("Paca", "123", AuthorityUtils.createAuthorityList("ACCOUNT_HOLDER"));
        TestingAuthenticationToken testingAuthenticationToken = new TestingAuthenticationToken(user,null);

        creditCard.setCreatedAt(LocalDate.of(2020, 1, 8));
        accountRepository.save(creditCard);

        MvcResult result = this.mockMvc.perform(
                get("/account/" + creditCard.getId()).principal(testingAuthenticationToken)
        ).andExpect(status().isOk()).andReturn();

        assertEquals((new BigDecimal("30249.90")).compareTo(accountRepository.findByPrimaryOwner(accountHolder2).get(0).getBalance().getAmount()), 0);
    }

    @Test
    void getAccount_addInterestCreditCardValidAdmin_account() throws Exception{
        User user = new User("admin", "123", AuthorityUtils.createAuthorityList("ADMIN"));
        TestingAuthenticationToken testingAuthenticationToken = new TestingAuthenticationToken(user,null);

        creditCard.setCreatedAt(LocalDate.of(2020, 1, 8));
        accountRepository.save(creditCard);

        MvcResult result = this.mockMvc.perform(
                get("/account/" + creditCard.getId()).principal(testingAuthenticationToken)
        ).andExpect(status().isOk()).andReturn();

        assertEquals((new BigDecimal("30249.90")).compareTo(accountRepository.findByPrimaryOwner(accountHolder2).get(0).getBalance().getAmount()), 0);
    }

    @Test
    void getAccountBalance_addInterestSavingsValidAdmin_account() throws Exception{
        User user = new User("admin", "123", AuthorityUtils.createAuthorityList("ADMIN"));
        TestingAuthenticationToken testingAuthenticationToken = new TestingAuthenticationToken(user,null);

        savings.setCreatedAt(LocalDate.of(2020, 1, 8));

        accountRepository.save(savings);

        MvcResult result = this.mockMvc.perform(
                get("/account/balance/" + savings.getId()).principal(testingAuthenticationToken)
        ).andExpect(status().isOk()).andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("93600"));
    }

    @Test
    void getAccountBalance_addInterestSavingsValidUser_account() throws Exception{
        User user = new User("Paco", "123", AuthorityUtils.createAuthorityList("ACCOUNT_HOLDER"));
        TestingAuthenticationToken testingAuthenticationToken = new TestingAuthenticationToken(user,null);

        savings.setCreatedAt(LocalDate.of(2020, 1, 8));

        accountRepository.save(savings);

        MvcResult result = this.mockMvc.perform(
                get("/account/balance/" + savings.getId()).principal(testingAuthenticationToken)
        ).andExpect(status().isOk()).andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("93600"));
    }

    @Test
    void getAccountBalance_addInterestCreditCardValidUser_account() throws Exception{
        User user = new User("Paca", "123", AuthorityUtils.createAuthorityList("ACCOUNT_HOLDER"));
        TestingAuthenticationToken testingAuthenticationToken = new TestingAuthenticationToken(user,null);

        creditCard.setCreatedAt(LocalDate.of(2020, 1, 8));

        accountRepository.save(creditCard);

        MvcResult result = this.mockMvc.perform(
                get("/account/balance/" + creditCard.getId()).principal(testingAuthenticationToken)
        ).andExpect(status().isOk()).andReturn();

        assertEquals((new BigDecimal("30249.90")).compareTo(accountRepository.findByPrimaryOwner(accountHolder2).get(0).getBalance().getAmount()), 0);
    }

    @Test
    void getAccountBalance_addInterestCreditCardValidAdmin_account() throws Exception{
        User user = new User("admin", "123", AuthorityUtils.createAuthorityList("ADMIN"));
        TestingAuthenticationToken testingAuthenticationToken = new TestingAuthenticationToken(user,null);

        creditCard.setCreatedAt(LocalDate.of(2020, 1, 8));

        accountRepository.save(creditCard);

        MvcResult result = this.mockMvc.perform(
                get("/account/balance/" + creditCard.getId()).principal(testingAuthenticationToken)
        ).andExpect(status().isOk()).andReturn();

        assertEquals((new BigDecimal("30249.90")).compareTo(accountRepository.findByPrimaryOwner(accountHolder2).get(0).getBalance().getAmount()), 0);
    }

}
