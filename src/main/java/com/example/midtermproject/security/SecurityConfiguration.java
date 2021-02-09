package com.example.midtermproject.security;

import com.example.midtermproject.enums.RoleEnum;
import com.example.midtermproject.service.impl.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder);

    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.httpBasic();
        http.csrf().disable().authorizeRequests()
                .mvcMatchers("/user/accountholder").hasRole(RoleEnum.ADMIN.toString())
                .mvcMatchers("/user/admin").hasRole(RoleEnum.ADMIN.toString())
                .mvcMatchers("/user/thirdparty").hasRole(RoleEnum.ADMIN.toString())
                .mvcMatchers("/account/{id}").hasAnyRole(RoleEnum.ADMIN.toString(), RoleEnum.ACCOUNT_HOLDER.toString())
                .mvcMatchers(HttpMethod.GET, "/account/balance/{id}").hasAnyRole(RoleEnum.ADMIN.toString(), RoleEnum.ACCOUNT_HOLDER.toString())
                .mvcMatchers(HttpMethod.PATCH, "/account/balance/{id}").hasRole(RoleEnum.ADMIN.toString())
                .mvcMatchers("/account/checking").hasRole(RoleEnum.ADMIN.toString())
                .mvcMatchers("/account/creditcard").hasRole(RoleEnum.ADMIN.toString())
                .mvcMatchers("/account/savings").hasRole(RoleEnum.ADMIN.toString())
                .mvcMatchers("/transaction").hasRole(RoleEnum.ACCOUNT_HOLDER.toString())
                .anyRequest().permitAll();
    }
}
