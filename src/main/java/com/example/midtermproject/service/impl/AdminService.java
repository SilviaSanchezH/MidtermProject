package com.example.midtermproject.service.impl;

import com.example.midtermproject.enums.RoleEnum;
import com.example.midtermproject.model.Users.Admin;
import com.example.midtermproject.model.Users.Role;
import com.example.midtermproject.repository.AdminRepository;
import com.example.midtermproject.service.interfaces.IAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class AdminService implements IAdminService {

    @Autowired
    private AdminRepository adminRepository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    //Create a new admin
    public Admin newAdmin(Admin admin) {
        String password = passwordEncoder.encode(admin.getPassword());

        admin.setPassword(password);

        Set<Role> roles = new HashSet<>();
        roles.add(new Role(RoleEnum.ADMIN, admin));
        admin.setRoles(roles);

        return adminRepository.save(admin);
    }
}
