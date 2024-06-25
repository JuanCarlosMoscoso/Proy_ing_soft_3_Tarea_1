package com.project.demo.logic.entity.user;

import com.project.demo.logic.entity.rol.Role;
import com.project.demo.logic.entity.rol.RoleEnum;
import com.project.demo.logic.entity.rol.RoleRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@DependsOn("roleSeeder")
public class AdminSeeder implements ApplicationListener<ContextRefreshedEvent> {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public AdminSeeder(
            RoleRepository roleRepository,
            UserRepository  userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        this.createSuperAdminRole();
        this.createUser();
    }

    private void createUser() {
        User user = new User();
        user.setName("User");
        user.setLastname("User");
        user.setEmail("user.account@gamil.com");
        user.setPassword("user123");

        Optional<Role> optionalRole = roleRepository.findByName(RoleEnum.USER);
        Optional<User> optionalUser = userRepository.findByEmail(user.getEmail());

        // If role does not exist or user already exists, return
        if (optionalRole.isEmpty() || optionalUser.isPresent()) {
            return;
        }

        // Change password to encrypted password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(optionalRole.get());

        userRepository.save(user);
    }

    private void createSuperAdminRole() {
        User userSuperAdminRole = new User();
        userSuperAdminRole.setName("Super Admin");
        userSuperAdminRole.setLastname("Role");
        userSuperAdminRole.setEmail("super_admin.account@gamil.com");
        userSuperAdminRole.setPassword("super_admin123");

        Optional<Role> optionalRole = roleRepository.findByName(RoleEnum.SUPER_ADMIN_ROLE);
        Optional<User> optionalUser = userRepository.findByEmail(userSuperAdminRole.getEmail());

        if (optionalRole.isEmpty() || optionalUser.isPresent()) {
            return;
        }

        // Change password to encrypted password and set role
        userSuperAdminRole.setPassword(passwordEncoder.encode(userSuperAdminRole.getPassword()));
        userSuperAdminRole.setRole(optionalRole.get());

        userRepository.save(userSuperAdminRole);
    }
}
