package com.campusskills.service;

import com.campusskills.config.JwtTokenProvider;
import com.campusskills.dto.*;
import com.campusskills.entity.*;
import com.campusskills.exception.ApiException;
import com.campusskills.repository.PlacementProfileRepository;
import com.campusskills.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PlacementProfileRepository placementProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final GamificationService gamificationService;
    private final TextModerationService textModerationService;

    public UserService(UserRepository userRepository,
                       PlacementProfileRepository placementProfileRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider tokenProvider,
                       GamificationService gamificationService,
                       TextModerationService textModerationService) {
        this.userRepository = userRepository;
        this.placementProfileRepository = placementProfileRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.gamificationService = gamificationService;
        this.textModerationService = textModerationService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        textModerationService.checkRegistration(request.getEmail(), request.getFullName(), request.getBio());
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ApiException("Email is already registered", HttpStatus.CONFLICT);
        }

        if (userRepository.existsByRollNumber(request.getRollNumber())) {
            throw new ApiException("Roll number is already registered", HttpStatus.CONFLICT);
        }

        User user = User.builder()
                .email(request.getEmail().trim().toLowerCase())
                .rollNumber(request.getRollNumber().trim().toUpperCase())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName().trim())
                .role(Role.STUDENT)
                .status(UserStatus.PENDING) // New students register as PENDING
                .branch(request.getBranch())
                .studyYear(request.getStudyYear() != null ? request.getStudyYear() : 1)
                .bio(request.getBio())
                .preferredMode(request.getPreferredMode() != null ? request.getPreferredMode() : "BOTH")
                .photoUrl(request.getPhotoUrl())
                .skillCredits(3) // New students start with 3 credits
                .xp(0)
                .level("Beginner")
                .dailyStreak(1)
                .lastActiveDate(LocalDate.now())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        User saved = userRepository.save(user);

        // Initialize placement profile
        PlacementProfile profile = PlacementProfile.builder()
                .userId(saved.getId())
                .dsaSolvedCount(0)
                .aptitudeScore(0.0)
                .mockInterviewRating(0.0)
                .resumeScore(0.0)
                .placementReadiness(0.0)
                .streakDays(1)
                .lastChallengeDate(LocalDate.now())
                .updatedAt(LocalDateTime.now())
                .build();
        placementProfileRepository.save(profile);

        String token = tokenProvider.generateToken(saved);
        return new AuthResponse(token, toUserResponse(saved));
    }

    @Transactional
    public AuthResponse login(AuthRequest request) {
        String identifier = request.getEmailOrRollNumber().trim();
        Optional<User> userOpt = userRepository.findByEmail(identifier.toLowerCase());
        if (userOpt.isEmpty()) {
            userOpt = userRepository.findByRollNumber(identifier.toUpperCase());
        }

        if (userOpt.isEmpty()) {
            throw new ApiException("Invalid email/roll number or password", HttpStatus.UNAUTHORIZED);
        }

        User user = userOpt.get();

        if (user.getStatus() == UserStatus.SUSPENDED) {
            throw new ApiException("Your account has been suspended. Please contact the administrator.", HttpStatus.FORBIDDEN);
        }
        if (user.getStatus() == UserStatus.REJECTED) {
            throw new ApiException("Your registration request was rejected.", HttpStatus.FORBIDDEN);
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ApiException("Invalid email/roll number or password", HttpStatus.UNAUTHORIZED);
        }

        // Daily streak calculation
        LocalDate today = LocalDate.now();
        if (user.getLastActiveDate() != null) {
            long days = ChronoUnit.DAYS.between(user.getLastActiveDate(), today);
            if (days == 1) {
                user.setDailyStreak(user.getDailyStreak() != null ? user.getDailyStreak() + 1 : 1);
            } else if (days > 1) {
                user.setDailyStreak(1);
            }
        } else {
            user.setDailyStreak(1);
        }
        user.setLastActiveDate(today);
        userRepository.save(user);

        String token = tokenProvider.generateToken(user);
        return new AuthResponse(token, toUserResponse(user));
    }

    public UserResponse getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND));
        return toUserResponse(user);
    }

    @Transactional
    public UserResponse updateProfile(Long userId, ProfileUpdateRequest req) {
        textModerationService.check(userId, "PROFILE", req.getFullName(), req.getBio());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND));

        if (req.getFullName() != null) user.setFullName(req.getFullName().trim());
        if (req.getBranch() != null) user.setBranch(req.getBranch().trim());
        if (req.getStudyYear() != null) user.setStudyYear(req.getStudyYear());
        if (req.getBio() != null) user.setBio(req.getBio().trim());
        if (req.getPreferredMode() != null) user.setPreferredMode(req.getPreferredMode());
        if (req.getPhotoUrl() != null) user.setPhotoUrl(req.getPhotoUrl().trim());

        User updated = userRepository.save(user);
        return toUserResponse(updated);
    }

    public UserResponse toUserResponse(User user) {
        Double avgRating = gamificationService.calculateAverageRating(user.getId());
        Double reliability = gamificationService.calculateReliabilityScore(user.getId());
        var badges = gamificationService.getBadges(user.getId());

        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .collegeDomain(extractEmailDomain(user.getEmail()))
                .rollNumber(user.getRollNumber())
                .fullName(user.getFullName())
                .role(user.getRole())
                .status(user.getStatus())
                .branch(user.getBranch())
                .studyYear(user.getStudyYear())
                .bio(user.getBio())
                .preferredMode(user.getPreferredMode())
                .photoUrl(user.getPhotoUrl())
                .skillCredits(user.getSkillCredits())
                .xp(user.getXp())
                .level(user.getLevel())
                .dailyStreak(user.getDailyStreak())
                .lastActiveDate(user.getLastActiveDate())
                .averageRating(avgRating)
                .reliabilityScore(reliability)
                .badges(badges)
                .createdAt(user.getCreatedAt())
                .build();
    }

    private String extractEmailDomain(String email) {
        int separator = email.lastIndexOf('@');
        return separator >= 0 ? email.substring(separator + 1).trim().toLowerCase() : "";
    }
}
