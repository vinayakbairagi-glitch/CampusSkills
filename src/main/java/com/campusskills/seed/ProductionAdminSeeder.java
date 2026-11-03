package com.campusskills.seed;

import com.campusskills.entity.Role;
import com.campusskills.entity.User;
import com.campusskills.entity.UserStatus;
import com.campusskills.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;

/**
 * Creates only the first production administrator.
 * Demo/student accounts are deliberately not seeded in the MySQL profile.
 */
@Component
@Profile("mysql")
public class ProductionAdminSeeder implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminEmail;
    private final String adminPassword;

    public ProductionAdminSeeder(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            @Value("${campusskills.admin.email}") String adminEmail,
            @Value("${campusskills.admin.password}") String adminPassword) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminEmail = adminEmail.trim().toLowerCase(Locale.ROOT);
        this.adminPassword = adminPassword;
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
            if (existing.getRole() != Role.SUPER_ADMIN) {
                throw new IllegalStateException("CAMPUS_ADMIN_EMAIL already belongs to a non-admin account.");
            }
        }, () -> {
            if (userRepository.existsByRollNumber("ADM001")) {
                throw new IllegalStateException("Roll number ADM001 is already in use. Change the production admin roll number.");
            }

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
        });
    }
}
