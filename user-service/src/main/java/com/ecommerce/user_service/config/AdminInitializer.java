package com.ecommerce.user_service.config;

import com.ecommerce.user_service.entity.Role;
import com.ecommerce.user_service.entity.User;
import com.ecommerce.user_service.repository.RoleRepository;
import com.ecommerce.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

        // Ensure SUPER_ADMIN role exists
        Role superAdminRole = roleRepository.findByName("SUPER_ADMIN");
        if (superAdminRole == null) {
            superAdminRole = new Role();
            superAdminRole.setName("SUPER_ADMIN");
            roleRepository.save(superAdminRole);
            System.out.println("SUPER_ADMIN role created!");
        }

        // Ensure USER role exists
        Role userRole = roleRepository.findByName("USER");
        if (userRole == null) {
            userRole = new Role();
            userRole.setName("USER");
            roleRepository.save(userRole);
            System.out.println("USER role created!");
        }

        // Ensure super admin user exists
        String superAdminEmail = "superadmin@123.com";
        if (userRepository.findByEmail(superAdminEmail) == null) {
            User superAdmin = new User();
            superAdmin.setEmail(superAdminEmail);
            superAdmin.setPassword(passwordEncoder.encode("Admin@123"));
            superAdmin.setRoles(new HashSet<>(Collections.singleton(superAdminRole)));
            superAdmin.setIsDeleted(false);
            superAdmin.setUsername("superadmin");
            userRepository.save(superAdmin);
            System.out.println("Default super admin created with email: " + superAdminEmail + " and password: Admin@123");
        }
    }
}
