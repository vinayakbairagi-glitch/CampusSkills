package com.campusskills.service;

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
public class AdminService {

    private final UserRepository userRepository;
    private final ReportRepository reportRepository;
    private final StudyGroupRepository studyGroupRepository;
    private final TutoringSessionRepository tutoringSessionRepository;
    private final DoubtRepository doubtRepository;
    private final DoubtAnswerRepository doubtAnswerRepository;
    private final TestRepository testRepository;
    private final TestAttemptRepository testAttemptRepository;
    private final UserSkillRepository userSkillRepository;
    private final SessionAttendanceRepository sessionAttendanceRepository;
    private final NotificationService notificationService;

    public AdminService(UserRepository userRepository,
                        ReportRepository reportRepository,
                        StudyGroupRepository studyGroupRepository,
                        TutoringSessionRepository tutoringSessionRepository,
                        DoubtRepository doubtRepository,
                        DoubtAnswerRepository doubtAnswerRepository,
                        TestRepository testRepository,
                        TestAttemptRepository testAttemptRepository,
                        UserSkillRepository userSkillRepository,
                        SessionAttendanceRepository sessionAttendanceRepository,
                        NotificationService notificationService) {
        this.userRepository = userRepository;
        this.reportRepository = reportRepository;
        this.studyGroupRepository = studyGroupRepository;
        this.tutoringSessionRepository = tutoringSessionRepository;
        this.doubtRepository = doubtRepository;
        this.doubtAnswerRepository = doubtAnswerRepository;
        this.testRepository = testRepository;
        this.testAttemptRepository = testAttemptRepository;
        this.userSkillRepository = userSkillRepository;
        this.sessionAttendanceRepository = sessionAttendanceRepository;
        this.notificationService = notificationService;
    }

    public List<User> getAllStudents(String query, String status) {
        List<User> list = userRepository.findAll();
        if (status != null && !status.isBlank()) {
            UserStatus st = UserStatus.valueOf(status.toUpperCase());
            list = list.stream().filter(u -> u.getStatus() == st).collect(Collectors.toList());
        }
        if (query != null && !query.isBlank()) {
            String q = query.toLowerCase().trim();
            list = list.stream().filter(u ->
                    u.getFullName().toLowerCase().contains(q) ||
                    u.getEmail().toLowerCase().contains(q) ||
                    u.getRollNumber().toLowerCase().contains(q) ||
                    (u.getBranch() != null && u.getBranch().toLowerCase().contains(q))
            ).collect(Collectors.toList());
        }
        return list;
    }

    @Transactional
    public User verifyStudent(Long targetUserId, boolean approve) {
        User user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND));

        user.setStatus(approve ? UserStatus.VERIFIED : UserStatus.REJECTED);
        User saved = userRepository.save(user);

        notificationService.sendNotification(
                user.getId(),
                approve ? "Account Verified!" : "Registration Rejected",
                approve ? "Your CampusSkills student account has been verified. Welcome aboard!" : "Your registration was not approved by administration.",
                "USER",
                user.getId()
        );

        return saved;
    }

    @Transactional
    public User changeRole(Long targetUserId, String newRoleStr, Long callerId) {
        User caller = userRepository.findById(callerId)
                .orElseThrow(() -> new ApiException("Caller not found", HttpStatus.NOT_FOUND));

        if (caller.getRole() != Role.SUPER_ADMIN) {
            throw new ApiException("Only SUPER_ADMIN can change roles", HttpStatus.FORBIDDEN);
        }

        User target = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND));

        Role newRole = Role.valueOf(newRoleStr.toUpperCase());
        target.setRole(newRole);
        return userRepository.save(target);
    }

    public List<Report> getAllReports(String status) {
        if (status != null && !status.isBlank()) {
            return reportRepository.findByStatus(status.toUpperCase());
        }
        return reportRepository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional
    public Report actionReport(Long reportId, String action) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ApiException("Report not found", HttpStatus.NOT_FOUND));

        String act = action.toUpperCase();
        if (act.equals("DISMISS")) {
            report.setStatus("DISMISSED");
            report.setActionTaken("DISMISSED");
        } else if (act.equals("REMOVE_CONTENT")) {
            report.setStatus("ACTIONED");
            report.setActionTaken("USER".equalsIgnoreCase(report.getReportedType())
                    ? "BLOCKED_SUBMISSION_CONFIRMED"
                    : "REMOVED_CONTENT");

            if ("DOUBT".equalsIgnoreCase(report.getReportedType())) {
                doubtRepository.deleteById(report.getReportedId());
            } else if ("ANSWER".equalsIgnoreCase(report.getReportedType())) {
                doubtAnswerRepository.deleteById(report.getReportedId());
            } else if ("GROUP".equalsIgnoreCase(report.getReportedType())) {
                studyGroupRepository.deleteById(report.getReportedId());
            }
        } else if (act.equals("SUSPEND_USER")) {
            report.setStatus("ACTIONED");
            report.setActionTaken("SUSPENDED_USER");

            if ("USER".equalsIgnoreCase(report.getReportedType())) {
                userRepository.findById(report.getReportedId()).ifPresent(u -> {
                    u.setStatus(UserStatus.SUSPENDED);
                    userRepository.save(u);
                });
            }
        }

        return reportRepository.save(report);
    }

    public Map<String, Object> getAdminStats() {
        long totalStudents = userRepository.count();
        long pendingStudents = userRepository.countByStatus(UserStatus.PENDING);
        long tutors = userRepository.countByRole(Role.TUTOR);
        long groups = studyGroupRepository.count();
        long completedSessions = tutoringSessionRepository.countByStatus("COMPLETED");

        long totalDoubts = doubtRepository.count();
        long solvedDoubts = doubtRepository.findByStatus("SOLVED").size();
        double doubtSolvedPercent = totalDoubts > 0 ? Math.round(((double) solvedDoubts / totalDoubts) * 100.0) : 0.0;

        long testsTaken = testAttemptRepository.count();

        // Group attendance rate overall
        long attended = sessionAttendanceRepository.count();
        long checkedIn = sessionAttendanceRepository.findAll().stream().filter(a -> Boolean.TRUE.equals(a.getCheckedIn())).count();
        double attendanceRate = attended > 0 ? Math.round(((double) checkedIn / attended) * 100.0) : 88.0;

        // Active this week
        LocalDate weekAgo = LocalDate.now().minusDays(7);
        long activeThisWeek = userRepository.findAll().stream()
                .filter(u -> u.getLastActiveDate() != null && !u.getLastActiveDate().isBefore(weekAgo))
                .count();

        // Inactive students (no activity for 14 days)
        LocalDate fourteenDaysAgo = LocalDate.now().minusDays(14);
        List<User> inactiveStudents = userRepository.findAll().stream()
                .filter(u -> u.getLastActiveDate() != null && u.getLastActiveDate().isBefore(fourteenDaysAgo))
                .collect(Collectors.toList());

        // Popular skills (teachers vs learners)
        List<UserSkill> allUserSkills = userSkillRepository.findAll();
        Map<String, int[]> skillMap = new HashMap<>(); // [teachers, learners]
        for (UserSkill us : allUserSkills) {
            String name = us.getSkillName();
            skillMap.putIfAbsent(name, new int[]{0, 0});
            if ("CAN_TEACH".equalsIgnoreCase(us.getSkillType())) skillMap.get(name)[0]++;
            else if ("WANT_TO_LEARN".equalsIgnoreCase(us.getSkillType())) skillMap.get(name)[1]++;
        }

        List<Map<String, Object>> popularSkillsChart = new ArrayList<>();
        for (Map.Entry<String, int[]> entry : skillMap.entrySet()) {
            popularSkillsChart.add(Map.of(
                    "skill", entry.getKey(),
                    "teachers", entry.getValue()[0],
                    "learners", entry.getValue()[1]
            ));
        }

        // Weakest subjects (average test %)
        List<TestAttempt> allAttempts = testAttemptRepository.findAll();
        Map<String, List<Double>> subjectScores = new HashMap<>();
        for (TestAttempt a : allAttempts) {
            testRepository.findById(a.getTestId()).ifPresent(t -> {
                String sub = t.getSubject();
                subjectScores.putIfAbsent(sub, new ArrayList<>());
                if (a.getPercentage() != null) subjectScores.get(sub).add(a.getPercentage());
            });
        }

        List<Map<String, Object>> weakestSubjectsChart = new ArrayList<>();
        for (Map.Entry<String, List<Double>> entry : subjectScores.entrySet()) {
            double avg = entry.getValue().stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
            weakestSubjectsChart.add(Map.of(
                    "subject", entry.getKey(),
                    "averageScore", Math.round(avg * 10.0) / 10.0
            ));
        }
        weakestSubjectsChart.sort(Comparator.comparingDouble(a -> (Double) a.get("averageScore")));

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalStudents", totalStudents);
        stats.put("pendingStudents", pendingStudents);
        stats.put("tutors", tutors);
        stats.put("groups", groups);
        stats.put("completedSessions", completedSessions);
        stats.put("doubtsTotal", totalDoubts);
        stats.put("doubtsSolvedPercent", doubtSolvedPercent);
        stats.put("testsTaken", testsTaken);
        stats.put("groupAttendanceRate", attendanceRate);
        stats.put("activeThisWeek", activeThisWeek);
        stats.put("inactiveCount", inactiveStudents.size());
        stats.put("inactiveStudents", inactiveStudents);
        stats.put("popularSkillsChart", popularSkillsChart);
        stats.put("weakestSubjectsChart", weakestSubjectsChart);

        return stats;
    }

    public List<Map<String, Object>> getOfficialTestResults(Long testId) {
        List<TestAttempt> attempts = testId != null ?
                testAttemptRepository.findByTestIdOrderByScoreDescTimeTakenSecondsAsc(testId) :
                testAttemptRepository.findAll().stream().filter(a -> {
                    Test t = testRepository.findById(a.getTestId()).orElse(null);
                    return t != null && Boolean.TRUE.equals(t.getIsOfficial());
                }).collect(Collectors.toList());

        List<Map<String, Object>> list = new ArrayList<>();
        for (TestAttempt a : attempts) {
            Map<String, Object> map = new HashMap<>();
            map.put("attemptId", a.getId());
            map.put("testId", a.getTestId());
            testRepository.findById(a.getTestId()).ifPresent(t -> map.put("testTitle", t.getTitle()));
            userRepository.findById(a.getUserId()).ifPresent(u -> {
                map.put("studentName", u.getFullName());
                map.put("email", u.getEmail());
                map.put("rollNumber", u.getRollNumber());
                map.put("branch", u.getBranch());
                map.put("studyYear", u.getStudyYear());
            });
            map.put("score", a.getScore());
            map.put("totalMarks", a.getTotalMarks());
            map.put("percentage", a.getPercentage());
            map.put("passed", a.getPassed());
            map.put("submittedAt", a.getSubmittedAt());
            map.put("timeTakenSeconds", a.getTimeTakenSeconds());
            list.add(map);
        }
        return list;
    }

    public String exportStudentsCsv() {
        StringBuilder sb = new StringBuilder();
        sb.append("ID,Full Name,Email,Roll Number,Role,Status,Branch,Study Year,Credits,XP,Level,Daily Streak,Last Active\n");
        for (User u : userRepository.findAll()) {
            sb.append(u.getId()).append(",")
              .append("\"").append(u.getFullName()).append("\",")
              .append(u.getEmail()).append(",")
              .append(u.getRollNumber()).append(",")
              .append(u.getRole()).append(",")
              .append(u.getStatus()).append(",")
              .append("\"").append(u.getBranch() != null ? u.getBranch() : "").append("\",")
              .append(u.getStudyYear()).append(",")
              .append(u.getSkillCredits()).append(",")
              .append(u.getXp()).append(",")
              .append(u.getLevel()).append(",")
              .append(u.getDailyStreak()).append(",")
              .append(u.getLastActiveDate()).append("\n");
        }
        return sb.toString();
    }

    public String exportSessionsCsv() {
        StringBuilder sb = new StringBuilder();
        sb.append("Session ID,Skill,Tutor,Student,Scheduled Time,Duration (min),Mode,Status,Credits\n");
        for (TutoringSession s : tutoringSessionRepository.findAll()) {
            String tutorName = userRepository.findById(s.getTutorId()).map(User::getFullName).orElse("Unknown");
            String studentName = userRepository.findById(s.getStudentId()).map(User::getFullName).orElse("Unknown");
            sb.append(s.getId()).append(",")
              .append("\"").append(s.getSkillName()).append("\",")
              .append("\"").append(tutorName).append("\",")
              .append("\"").append(studentName).append("\",")
              .append(s.getScheduledTime()).append(",")
              .append(s.getDurationMinutes()).append(",")
              .append(s.getMode()).append(",")
              .append(s.getStatus()).append(",")
              .append(s.getCreditsCharged()).append("\n");
        }
        return sb.toString();
    }

    public String exportTestResultsCsv() {
        StringBuilder sb = new StringBuilder();
        sb.append("Attempt ID,Test Title,Student Name,Roll Number,Score,Total Marks,Percentage,Passed,Time Taken (sec),Submitted At\n");
        for (TestAttempt a : testAttemptRepository.findAll()) {
            String testTitle = testRepository.findById(a.getTestId()).map(Test::getTitle).orElse("Unknown");
            User u = userRepository.findById(a.getUserId()).orElse(null);
            sb.append(a.getId()).append(",")
              .append("\"").append(testTitle).append("\",")
              .append("\"").append(u != null ? u.getFullName() : "Unknown").append("\",")
              .append(u != null ? u.getRollNumber() : "").append(",")
              .append(a.getScore()).append(",")
              .append(a.getTotalMarks()).append(",")
              .append(a.getPercentage()).append("%,")
              .append(a.getPassed()).append(",")
              .append(a.getTimeTakenSeconds()).append(",")
              .append(a.getSubmittedAt()).append("\n");
        }
        return sb.toString();
    }
}
