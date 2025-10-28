package com.template.spring_mvc.config;

import com.template.spring_mvc.model.Permission;
import com.template.spring_mvc.model.Role;
import com.template.spring_mvc.model.User;
import com.template.spring_mvc.repository.PermissionRepository;
import com.template.spring_mvc.repository.RoleRepository;
import com.template.spring_mvc.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Crear permiso
        Permission readPerm = new Permission();
        readPerm.setName("READ_USER");
        permissionRepository.save(readPerm);

        // Crear rol
        Role adminRole = new Role();
        adminRole.setName("ROLE_ADMIN");
        adminRole.getPermissions().add(readPerm);
        roleRepository.save(adminRole);

        // Crear usuario
        User admin = new User();
        admin.setEmail("admin@example.com");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setName("Admin");
        admin.getRoles().add(adminRole);
        userRepository.save(admin);

        // Crear Rol
        Role userRole = new Role();
        userRole.setName("ROLE_ESTUDIANTE");
        roleRepository.save(userRole);

        User readUser = new User();
        readUser.setEmail("student_1@example.com");
        readUser.setPassword(passwordEncoder.encode("student123"));
        readUser.setName("Student One");
        readUser.getRoles().add(userRole);
        userRepository.save(readUser);
    }
}