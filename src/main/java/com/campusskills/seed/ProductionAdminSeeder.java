package com.campusskills.seed;

import com.campusskills.entity.Role;
import com.campusskills.entity.User;
import com.campusskills.entity.UserStatus;
import com.campusskills.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;

/**
 * Creates the first production administrator and supports an explicitly enabled
 * one-time credential refresh through CAMPUS_ADMIN_RESET_PASSWORD.
 * Demo/student accounts are never seeded by this class.
 */
@Component
@Profile("mysql")
@Order(0)
public class ProductionAdminSeeder implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminEmail;
    private final String adminPassword;
    private final boolean refreshCredentials;

    public ProductionAdminSeeder(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            @Value("${campusskills.admin.email}") String adminEmail,
            @Value("${campusskills.admin.password}") String adminPassword,
            @Value("${CAMPUS_ADMIN_RESET_PASSWORD:false}") boolean refreshCredentials) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminEmail = adminEmail.trim().toLowerCase(Locale.ROOT);
        this.adminPassword = adminPassword;
        this.refreshCredentials = refreshCredentials;
    }

    @Override
    public void run(String... args) {
        if (adminPassword == null || adminPassword.isBlank()) {
            throw new IllegalStateException("Set CAMPUS_ADMIN_PASSWORD before starting the production MySQL profile.");
        }
        if (adminPassword.length() < 12) {
            throw new IllegalStateException("CAMPUS_ADMIN_PASSWORD must contain at least 12 characters.");
        }

        userRepository.findByEmail(adminEmail).ifPresentOrElse(existing -> {
            ensureSuperAdmin(existing);
            if (refreshCredentials && !passwordEncoder.matches(adminPassword, existing.getPassword())) {
                existing.setPassword(passwordEncoder.encode(adminPassword));
                existing.setUpdatedAt(LocalDateTime.now());
                userRepository.save(existing);
            }
        }, () -> userRepository.findByRollNumber("ADM001").ifPresentOrElse(existing -> {
            ensureSuperAdmin(existing);
            if (!userRepository.findByEmail(adminEmail).isPresent()) {
                existing.setEmail(adminEmail);
            }
            if (refreshCredentials) {
                existing.setPassword(passwordEncoder.encode(adminPassword));
            }
            existing.setUpdatedAt(LocalDateTime.now());
            userRepository.save(existing);
        }, () -> createAdmin()));
    }

    private void ensureSuperAdmin(User user) {
        if (user.getRole() != Role.SUPER_ADMIN) {
            throw new IllegalStateException("The configured administrator identity belongs to a non-admin account.");
        }
    }

    private void createAdmin() {
        User admin = User.builder()
                .email(adminEmail)
                .rollNumber("ADM001")
                .password(passwordEncoder.encode(adminPassword))
                .fullName("CampusSkills Administrator")
                .role(Role.SUPER_ADMIN)
                .status(UserStatus.VERIFIED)
                .branch("Administration")
                .studyYear(4)
                .bio("CampusSkills platform administrator")
                .preferredMode("BOTH")
                .skillCredits(10)
                .xp(0)
                .level("Beginner")
                .dailyStreak(1)
                .lastActiveDate(LocalDate.now())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        userRepository.save(admin);
    }
}