package com.campusskills.service;

import com.campusskills.dto.ChallengeLogRequest;
import com.campusskills.dto.PlacementReviewRequest;
import com.campusskills.entity.*;
import com.campusskills.exception.ApiException;
import com.campusskills.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PlacementService {

    private final PlacementProfileRepository placementProfileRepository;
    private final DailyChallengeRepository dailyChallengeRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectRepository projectRepository;
    private final TestAttemptRepository testAttemptRepository;
    private final TestRepository testRepository;
    private final UserRepository userRepository;
    private final GamificationService gamificationService;
    private final NotificationService notificationService;

    public PlacementService(PlacementProfileRepository placementProfileRepository,
                            DailyChallengeRepository dailyChallengeRepository,
                            ProjectMemberRepository projectMemberRepository,
                            ProjectRepository projectRepository,
                            TestAttemptRepository testAttemptRepository,
                            TestRepository testRepository,
                            UserRepository userRepository,
                            GamificationService gamificationService,
                            NotificationService notificationService) {
        this.placementProfileRepository = placementProfileRepository;
        this.dailyChallengeRepository = dailyChallengeRepository;
        this.projectMemberRepository = projectMemberRepository;
        this.projectRepository = projectRepository;
        this.testAttemptRepository = testAttemptRepository;
        this.testRepository = testRepository;
        this.userRepository = userRepository;
        this.gamificationService = gamificationService;
        this.notificationService = notificationService;
    }

    public Map<String, Object> getPlacementMe(Long userId) {
        PlacementProfile profile = placementProfileRepository.findByUserId(userId)
                .orElseGet(() -> placementProfileRepository.save(PlacementProfile.builder()
                        .userId(userId)
                        .dsaSolvedCount(25)
                        .aptitudeScore(70.0)
                        .mockInterviewRating(4.0)
                        .resumeScore(75.0)
                        .streakDays(1)
                        .lastChallengeDate(LocalDate.now())
                        .updatedAt(LocalDateTime.now())
                        .build()));

        // 1. DSA progress (solved / 150 * 100)
        int dsaSolved = profile.getDsaSolvedCount() != null ? profile.getDsaSolvedCount() : 0;
        double dsaScore = Math.min(100.0, Math.round(((double) dsaSolved / 150.0 * 100.0) * 10.0) / 10.0);

        // 2. Aptitude score (average best MOCK test %)
        double aptitudeScore = profile.getAptitudeScore() != null ? profile.getAptitudeScore() : 0.0;
        if (aptitudeScore == 0.0) {
            // Check MOCK test attempts
            List<TestAttempt> attempts = testAttemptRepository.findByUserIdOrderByStartTimeDesc(userId);
            double sum = 0.0;
            int count = 0;
            for (TestAttempt a : attempts) {
                Test t = testRepository.findById(a.getTestId()).orElse(null);
                if (t != null && "MOCK".equalsIgnoreCase(t.getTestType())) {
                    sum += a.getPercentage();
                    count++;
                }
            }
            if (count > 0) aptitudeScore = Math.round((sum / count) * 10.0) / 10.0;
            else aptitudeScore = 65.0; // demo default
        }

        // 3. Mock interview rating (x 20)
        double interviewRating = profile.getMockInterviewRating() != null ? profile.getMockInterviewRating() : 3.5;
        double interviewScore = Math.min(100.0, Math.round((interviewRating * 20.0) * 10.0) / 10.0);

        // 4. Resume score (0 - 100)
        double resumeScore = profile.getResumeScore() != null && profile.getResumeScore() > 0 ? profile.getResumeScore() : 70.0;

        // 5. Project work (50 per completed project, capped at 100)
        List<ProjectMember> memberships = projectMemberRepository.findByUserId(userId);
        long completedProjects = memberships.stream()
                .filter(m -> "ACCEPTED".equalsIgnoreCase(m.getStatus()))
                .map(m -> projectRepository.findById(m.getProjectId()).orElse(null))
                .filter(p -> p != null && "COMPLETED".equalsIgnoreCase(p.getStatus()))
                .count();
        double projectScore = Math.min(100.0, completedProjects * 50.0);
        if (projectScore == 0.0 && !memberships.isEmpty()) projectScore = 50.0; // enrolled project bonus

        // Placement Readiness (0-100) = Average of the five parts
        double readiness = Math.round(((dsaScore + aptitudeScore + interviewScore + resumeScore + projectScore) / 5.0) * 10.0) / 10.0;

        profile.setPlacementReadiness(readiness);
        placementProfileRepository.save(profile);

        Map<String, Object> map = new HashMap<>();
        map.put("placementReadiness", readiness);
        map.put("dsaSolved", dsaSolved);
        map.put("dsaTarget", 150);
        map.put("dsaScore", dsaScore);
        map.put("aptitudeScore", aptitudeScore);
        map.put("mockInterviewRating", interviewRating);
        map.put("interviewScore", interviewScore);
        map.put("resumeScore", resumeScore);
        map.put("completedProjectsCount", completedProjects);
        map.put("projectScore", projectScore);
        map.put("streakDays", profile.getStreakDays());
        map.put("recentChallenges", dailyChallengeRepository.findByUserIdOrderByChallengeDateDesc(userId).stream().limit(7).collect(Collectors.toList()));

        return map;
    }

    @Transactional
    public DailyChallenge logDailyChallenge(Long userId, ChallengeLogRequest req) {
        LocalDate today = LocalDate.now();
        int count = req.getProblemCount() != null ? req.getProblemCount() : 1;
        int xp = count * 5;

        DailyChallenge challenge = dailyChallengeRepository.findByUserIdAndChallengeDate(userId, today)
                .orElse(DailyChallenge.builder()
                        .userId(userId)
                        .challengeDate(today)
                        .problemCount(0)
                        .xpEarned(0)
                        .build());

        challenge.setPlatform(req.getPlatform().trim());
        challenge.setProblemTitle(req.getProblemTitle().trim());
        challenge.setProblemCount(challenge.getProblemCount() + count);
        challenge.setXpEarned(challenge.getXpEarned() + xp);
        DailyChallenge saved = dailyChallengeRepository.save(challenge);

        // Update placement profile
        PlacementProfile profile = placementProfileRepository.findByUserId(userId)
                .orElseGet(() -> PlacementProfile.builder().userId(userId).build());

        profile.setDsaSolvedCount((profile.getDsaSolvedCount() != null ? profile.getDsaSolvedCount() : 0) + count);

        // Streak check
        if (profile.getLastChallengeDate() != null) {
            long days = ChronoUnit.DAYS.between(profile.getLastChallengeDate(), today);
            if (days == 1) {
                profile.setStreakDays((profile.getStreakDays() != null ? profile.getStreakDays() : 0) + 1);
            } else if (days > 1) {
                profile.setStreakDays(1);
            }
        } else {
            profile.setStreakDays(1);
        }
        profile.setLastChallengeDate(today);
        placementProfileRepository.save(profile);

        // XP rules: daily challenge +5 each
        gamificationService.awardXp(userId, xp, "Solved " + count + " coding problem(s)");

        return saved;
    }

    @Transactional
    public PlacementProfile reviewStudentPlacement(Long reviewerId, PlacementReviewRequest req) {
        User reviewer = userRepository.findById(reviewerId)
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND));

        // Seniors, tutors and TPO can score resume & mock interview
        boolean isEligible = reviewer.getRole() == Role.TPO ||
                             reviewer.getRole() == Role.SUPER_ADMIN ||
                             reviewer.getRole() == Role.TUTOR ||
                             (reviewer.getStudyYear() != null && reviewer.getStudyYear() >= 3);

        if (!isEligible) {
            throw new ApiException("Only seniors (Year 3+), tutors and TPO can review placement profiles", HttpStatus.FORBIDDEN);
        }

        PlacementProfile profile = placementProfileRepository.findByUserId(req.getStudentId())
                .orElseGet(() -> PlacementProfile.builder().userId(req.getStudentId()).build());

        if (req.getResumeScore() != null) {
            profile.setResumeScore(Math.min(100.0, Math.max(0.0, req.getResumeScore())));
        }
        if (req.getMockInterviewRating() != null) {
            profile.setMockInterviewRating(Math.min(5.0, Math.max(1.0, req.getMockInterviewRating())));
        }

        PlacementProfile saved = placementProfileRepository.save(profile);

        notificationService.sendNotification(
                req.getStudentId(),
                "Placement Review Added",
                reviewer.getFullName() + " submitted your placement review / score update.",
                "RATING",
                saved.getId()
        );

        return saved;
    }
}
