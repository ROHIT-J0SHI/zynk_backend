package com.zynk.config;

import com.zynk.entity.User;
import com.zynk.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Data Initializer - Creates default HR users on application startup
 * 
 * HR Credentials:
 * HR 1:
 *   Email: hr1@internflow.com
 *   Password: hr123456
 * 
 * HR 2:
 *   Email: hr2@internflow.com
 *   Password: hr123456
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) throws Exception {
        initializeHRUsers();
    }
    
    private void initializeHRUsers() {
        log.info("Initializing default HR users...");
        
        // HR User 1
        createHRUserIfNotExists(
            "hr1@internflow.com",
            "hr123456",
            "HR Manager 1"
        );
        
        // HR User 2
        createHRUserIfNotExists(
            "hr2@internflow.com",
            "hr123456",
            "HR Manager 2"
        );
        
        log.info("HR users initialization completed.");
    }
    
    private void createHRUserIfNotExists(String email, String password, String name) {
        if (!userRepository.existsByEmail(email)) {
            User hrUser = new User();
            hrUser.setEmail(email);
            hrUser.setPassword(passwordEncoder.encode(password));
            hrUser.setName(name);
            hrUser.setRole(User.UserRole.HR);
            
            userRepository.save(hrUser);
            log.info("Created HR user: {} ({})", name, email);
        } else {
            log.info("HR user already exists: {}", email);
        }
    }
}

