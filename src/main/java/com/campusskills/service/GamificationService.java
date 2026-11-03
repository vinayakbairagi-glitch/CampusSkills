package com.campusskills.service;

import com.campusskills.entity.*;
import com.campusskills.repository.*;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class GamificationService {

    private final UserRepository userRepository;
    private final XpEventRepository xpEventRepository;
    private final TutoringSessionRepository tutoringSessionRepository;
    private final DoubtAnswerRepository doubtAnswerRepository;
    private final TestAttemptRepository testAttemptRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final UserSkillRepository userSkillRepository;
    private final RatingRepository ratingRepository;
    private final SessionAttendanceRepository sessionAttendanceRepository;
    private final NotificationService notificationService;

    public GamificationService(UserRepository userRepository,
                               XpEventRepository xpEventRepository,
                               TutoringSessionRepository tutoringSessionRepository,
                               DoubtAnswerRepository doubtAnswerRepository,
                               TestAttemptRepository testAttemptRepository,
                               ProjectMemberRepository projectMemberRepository,
                               UserSkillRepository userSkillRepository,
                               RatingRepository ratingRepository,
                               SessionAttendanceRepository sessionAttendanceRepository,
                               @Lazy NotificationService notificationService) {
        this.userRepository = userRepository;
        this.xpEventRepository = xpEventRepository;
        this.tutoringSessionRepository = tutoringSessionRepository;
        this.doubtAnswerRepository = doubtAnswerRepository;
        this.testAttemptRepository = testAttemptRepository;
        this.projectMemberRepository = projectMemberRepository;
        this.userSkillRepository = userSkillRepository;
        this.ratingRepository = ratingRepository;
        this.sessionAttendanceRepository = sessionAttendanceRepository;
        this.notificationService = notificationService;
    }

    public void awardXp(Long userId, int amount, String reason) {
        userRepository.findById(userId).ifPresent(user -> {
            int oldXp = user.getXp() != null ? user.getXp() : 0;
            int newXp = oldXp + amount;
            user.setXp(newXp);

            String oldLevel = user.getLevel();
            String newLevel = computeLevel(newXp);
            user.setLevel(newLevel);

            userRepository.save(user);

            // Record XP Event
            xpEventRepository.save(XpEvent.builder()
                    .userId(userId)
                    .xpAmount(amount)
                    .reason(reason)
                    .createdAt(LocalDateTime.now())
                    .build());

            // Notify on level up
            if (!newLevel.equals(oldLevel)) {
                notificationService.sendNotification(
                        userId,
                        "Level Up!",
                        "Congratulations! You leveled up from " + oldLevel + " to " + newLevel + "!",
                        "LEVEL_UP",
                        null
                );
            }
        });
    }

    public String computeLevel(int xp) {
        if (xp < 100) return "Beginner";
        if (xp < 300) return "Contributor";
        if (xp < 700) return "Mentor";
        return "Campus Expert";
    }

    public List<String> getBadges(Long userId) {
        List<String> badges = new ArrayList<>();

        // Sessions taught
        List<TutoringSession> taughtSessions = tutoringSessionRepository.findByTutorIdAndStatus(userId, "COMPLETED");
        if (!taughtSessions.isEmpty()) {
            badges.add("Skill Sharer");
        }

        // Skill Helpers & DSA Mentor
        long dsaCount = taughtSessions.stream().filter(s -> s.getSkillName() != null && s.getSkillName().equalsIgnoreCase("DSA")).count();
        if (dsaCount >= 3) {
            badges.add("DSA Mentor");
        }
        for (TutoringSession s : taughtSessions) {
            if (s.getSkillName() != null) {
                long count = taughtSessions.stream().filter(ts -> s.getSkillName().equalsIgnoreCase(ts.getSkillName())).count();
                if (count >= 3 && !badges.contains(s.getSkillName() + " Helper")) {
                    badges.add(s.getSkillName() + " Helper");
                }
            }
        }

        // Best Answerer
        long bestAnswers = doubtAnswerRepository.countByAuthorIdAndIsBestTrue(userId);
        if (bestAnswers >= 3) {
            badges.add("Best Answerer");
        }

        // Quiz Master
        long quizAttempts = testAttemptRepository.countByUserId(userId);
        if (quizAttempts >= 5) {
            badges.add("Quiz Master");
        }

        // Perfect Score
        List<TestAttempt> attempts = testAttemptRepository.findByUserIdOrderByStartTimeDesc(userId);
        boolean hasPerfectScore = attempts.stream().anyMatch(a -> a.getPercentage() != null && a.getPercentage() >= 100.0);
        if (hasPerfectScore) {
            badges.add("Perfect Score");
        }

        // Team Player
        if (!projectMemberRepository.findByUserId(userId).isEmpty()) {
            badges.add("Team Player");
        }

        // 7-Day Streak
        userRepository.findById(userId).ifPresent(user -> {
            if (user.getDailyStreak() != null && user.getDailyStreak() >= 7) {
                badges.add("7-Day Streak");
            }
        });

        // Verified Tutors
        List<UserSkill> skills = userSkillRepository.findByUserIdAndSkillType(userId, "CAN_TEACH");
        for (UserSkill us : skills) {
            if (Boolean.TRUE.equals(us.getVerified())) {
                badges.add("Verified " + us.getSkillName() + " Tutor");
            }
        }

        return badges;
    }

    public double calculateAverageRating(Long userId) {
        List<Rating> ratings = ratingRepository.findByRatedUserId(userId);
        if (ratings.isEmpty()) return 5.0; // default initial score
        double sum = 0.0;
        for (Rating r : ratings) {
            sum += r.getRating();
        }
        return Math.round((sum / ratings.size()) * 10.0) / 10.0;
    }

    public double calculateReliabilityScore(Long userId) {
        // 60% average rating + 40% group attendance rate
        double avgRating = calculateAverageRating(userId);
        double ratingPercent = (avgRating / 5.0) * 100.0;

        long attended = sessionAttendanceRepository.countByUserIdAndCheckedInTrue(userId);
        long totalSessions = sessionAttendanceRepository.countByUserId(userId);
        double attendancePercent = totalSessions > 0 ? ((double) attended / totalSessions) * 100.0 : 100.0;

        double reliability = (0.60 * ratingPercent) + (0.40 * attendancePercent);
        return Math.min(100.0, Math.round(reliability * 10.0) / 10.0);
    }
}
