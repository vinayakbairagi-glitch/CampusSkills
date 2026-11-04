package com.campusskills.controller;

import com.campusskills.entity.*;
import com.campusskills.repository.*;
import com.campusskills.service.GamificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class GamificationController {

    private final UserRepository userRepository;
    private final TutoringSessionRepository tutoringSessionRepository;
    private final GroupSessionRepository groupSessionRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final SwapRequestRepository swapRequestRepository;
    private final TestAttemptRepository testAttemptRepository;
    private final TestRepository testRepository;
    private final GamificationService gamificationService;

    public GamificationController(UserRepository userRepository,
                                  TutoringSessionRepository tutoringSessionRepository,
                                  GroupSessionRepository groupSessionRepository,
                                  GroupMemberRepository groupMemberRepository,
                                  SwapRequestRepository swapRequestRepository,
                                  TestAttemptRepository testAttemptRepository,
                                  TestRepository testRepository,
                                  GamificationService gamificationService) {
        this.userRepository = userRepository;
        this.tutoringSessionRepository = tutoringSessionRepository;
        this.groupSessionRepository = groupSessionRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.swapRequestRepository = swapRequestRepository;
        this.testAttemptRepository = testAttemptRepository;
        this.testRepository = testRepository;
        this.gamificationService = gamificationService;
    }

    @GetMapping("/api/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard(@AuthenticationPrincipal User user) {
        // XP Bar to next level
        int xp = user.getXp() != null ? user.getXp() : 0;
        int nextLevelTarget = 100;
        int prevLevelBase = 0;
        if (xp < 100) {
            nextLevelTarget = 100;
            prevLevelBase = 0;
        } else if (xp < 300) {
            nextLevelTarget = 300;
            prevLevelBase = 100;
        } else if (xp < 700) {
            nextLevelTarget = 700;
            prevLevelBase = 300;
        } else {
            nextLevelTarget = 1500;
            prevLevelBase = 700;
        }
        int progress = (int) Math.min(100, Math.round(((double)(xp - prevLevelBase) / (nextLevelTarget - prevLevelBase)) * 100.0));

        // Upcoming tutoring sessions
        LocalDateTime now = LocalDateTime.now();
        List<TutoringSession> upcomingTutoring = tutoringSessionRepository.findByTutorIdOrStudentIdOrderByScheduledTimeDesc(user.getId(), user.getId())
                .stream()
                .filter(s -> "SCHEDULED".equalsIgnoreCase(s.getStatus()) && (s.getScheduledTime() == null || s.getScheduledTime().isAfter(now.minusHours(2))))
                .limit(3)
                .collect(Collectors.toList());

        // Upcoming group sessions
        List<GroupMember> myGroups = groupMemberRepository.findByUserIdAndStatus(user.getId(), "ACTIVE");
        Set<Long> myGroupIds = myGroups.stream().map(GroupMember::getGroupId).collect(Collectors.toSet());
        List<GroupSession> upcomingGroupSessions = myGroupIds.isEmpty()
                ? Collections.emptyList()
                : groupSessionRepository.findByGroupIdInOrderByScheduledTimeAsc(myGroupIds).stream()
                    .filter(gs -> gs.getScheduledTime() == null || gs.getScheduledTime().isAfter(now.minusHours(2)))
                    .limit(3)
                    .collect(Collectors.toList());

        // Pending requests count
        List<SwapRequest> pendingRequests = swapRequestRepository.findByReceiverIdAndStatus(user.getId(), "PENDING");

        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("xpProgressPercent", progress);
        dashboard.put("nextLevelTarget", nextLevelTarget);
        dashboard.put("upcomingTutoring", upcomingTutoring);
        dashboard.put("upcomingGroupSessions", upcomingGroupSessions);
        dashboard.put("pendingRequestsCount", pendingRequests.size());
        dashboard.put("pendingRequests", pendingRequests);

        return ResponseEntity.ok(dashboard);
    }

    @GetMapping("/api/leaderboard")
    public ResponseEntity<List<Map<String, Object>>> getLeaderboard(@RequestParam(defaultValue = "all") String period) {
        List<User> users = userRepository.findByStatus(UserStatus.VERIFIED);

        if ("week".equalsIgnoreCase(period)) {
            LocalDate weekAgo = LocalDate.now().minusDays(7);
            users = users.stream()
                    .filter(u -> u.getLastActiveDate() != null && !u.getLastActiveDate().isBefore(weekAgo))
                    .collect(Collectors.toList());
        }

        users.sort((a, b) -> Integer.compare(
                b.getXp() != null ? b.getXp() : 0,
                a.getXp() != null ? a.getXp() : 0
        ));

        List<Map<String, Object>> leaderboard = new ArrayList<>();
        int rank = 1;
        for (User u : users) {
            Map<String, Object> map = new HashMap<>();
            map.put("rank", rank++);
            map.put("userId", u.getId());
            map.put("fullName", u.getFullName());
            map.put("email", u.getEmail());
            map.put("photoUrl", u.getPhotoUrl());
            map.put("branch", u.getBranch());
            map.put("studyYear", u.getStudyYear());
            map.put("xp", u.getXp() != null ? u.getXp() : 0);
            map.put("level", u.getLevel() != null ? u.getLevel() : "Beginner");
            map.put("badges", gamificationService.getBadges(u.getId()));
            map.put("averageRating", gamificationService.calculateAverageRating(u.getId()));
            map.put("reliabilityScore", gamificationService.calculateReliabilityScore(u.getId()));
            leaderboard.add(map);
        }

        return ResponseEntity.ok(leaderboard);
    }

    @GetMapping("/api/certificates")
    public ResponseEntity<List<Map<String, Object>>> getCertificates(@AuthenticationPrincipal User user) {
        List<Map<String, Object>> list = new ArrayList<>();

        // Milestone certificates: 10, 25, 50 sessions
        List<TutoringSession> completedSessions = tutoringSessionRepository.findByTutorIdOrStudentIdOrderByScheduledTimeDesc(user.getId(), user.getId())
                .stream().filter(s -> "COMPLETED".equalsIgnoreCase(s.getStatus())).collect(Collectors.toList());

        int sessionCount = completedSessions.size();
        int[] milestones = {10, 25, 50};
        for (int m : milestones) {
            if (sessionCount >= m) {
                list.add(Map.of(
                        "id", "SESSION_" + m,
                        "title", "Peer-Learning Milestone Certificate (" + m + " Sessions)",
                        "type", "SESSION_MILESTONE",
                        "recipientName", user.getFullName(),
                        "recipientRoll", user.getRollNumber(),
                        "date", LocalDate.now().toString(),
                        "description", "Awarded for completing " + m + " peer tutoring and learning sessions with distinction.",
                        "badge", m + " Sessions Pioneer"
                ));
            }
        }

        // Test certificates: passed MOCK or SKILL_VERIFICATION or official tests
        List<TestAttempt> passedAttempts = testAttemptRepository.findByUserIdOrderByStartTimeDesc(user.getId()).stream()
                .filter(a -> Boolean.TRUE.equals(a.getPassed()))
                .collect(Collectors.toList());

        for (TestAttempt a : passedAttempts) {
            testRepository.findById(a.getTestId()).ifPresent(t -> {
                if ("MOCK".equalsIgnoreCase(t.getTestType()) ||
                    "SKILL_VERIFICATION".equalsIgnoreCase(t.getTestType()) ||
                    Boolean.TRUE.equals(t.getIsOfficial())) {
                    list.add(Map.of(
                            "id", "TEST_" + a.getId(),
                            "title", "Certificate of Competence: " + t.getTitle(),
                            "type", "TEST_ACHIEVEMENT",
                            "recipientName", user.getFullName(),
                            "recipientRoll", user.getRollNumber(),
                            "subject", t.getSubject(),
                            "score", a.getScore() + " / " + a.getTotalMarks() + " (" + a.getPercentage() + "%)",
                            "date", a.getSubmittedAt() != null ? a.getSubmittedAt().toLocalDate().toString() : LocalDate.now().toString(),
                            "description", "Successfully passed the official assessment in " + t.getSubject() + " with a score of " + a.getPercentage() + "%."
                    ));
                }
            });
        }

        return ResponseEntity.ok(list);
    }
}
