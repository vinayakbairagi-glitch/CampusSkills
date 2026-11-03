package com.campusskills.service;

import com.campusskills.dto.*;
import com.campusskills.entity.*;
import com.campusskills.entity.Test;
import com.campusskills.exception.ApiException;
import com.campusskills.repository.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TestService {

    private final TestRepository testRepository;
    private final TestAttemptRepository testAttemptRepository;
    private final StudentAnswerRepository studentAnswerRepository;
    private final QuestionBankRepository questionBankRepository;
    private final QuestionRepository questionRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final UserSkillRepository userSkillRepository;
    private final UserRepository userRepository;
    private final PlacementProfileRepository placementProfileRepository;
    private final GamificationService gamificationService;
    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;
    private final TextModerationService textModerationService;

    public TestService(TestRepository testRepository,
                       TestAttemptRepository testAttemptRepository,
                       StudentAnswerRepository studentAnswerRepository,
                       QuestionBankRepository questionBankRepository,
                       QuestionRepository questionRepository,
                       GroupMemberRepository groupMemberRepository,
                       UserSkillRepository userSkillRepository,
                       UserRepository userRepository,
                       PlacementProfileRepository placementProfileRepository,
                       GamificationService gamificationService,
                       NotificationService notificationService,
                       ObjectMapper objectMapper,
                       TextModerationService textModerationService) {
        this.testRepository = testRepository;
        this.testAttemptRepository = testAttemptRepository;
        this.studentAnswerRepository = studentAnswerRepository;
        this.questionBankRepository = questionBankRepository;
        this.questionRepository = questionRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.userSkillRepository = userSkillRepository;
        this.userRepository = userRepository;
        this.placementProfileRepository = placementProfileRepository;
        this.gamificationService = gamificationService;
        this.notificationService = notificationService;
        this.objectMapper = objectMapper;
        this.textModerationService = textModerationService;
    }

    // Question banks
    public List<QuestionBank> getAllBanks() {
        return questionBankRepository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional
    public QuestionBank createBank(Long userId, QuestionBankRequest req) {
        verifyBankCreator(userId);
        textModerationService.check(userId, "QUESTION_BANK", req.getTitle(), req.getSubject(), req.getTopic());
        QuestionBank bank = QuestionBank.builder()
                .title(req.getTitle().trim())
                .subject(req.getSubject().trim())
                .topic(req.getTopic().trim())
                .createdBy(userId)
                .createdAt(LocalDateTime.now())
                .build();
        return questionBankRepository.save(bank);
    }

    @Transactional
    public Question createQuestion(Long userId, QuestionRequest req) {
        verifyBankCreator(userId);
        textModerationService.check(userId, "QUESTION",
                req.getSubject(), req.getTopic(), req.getQuestionText(),
                req.getOptions() != null ? String.join("\n", req.getOptions()) : null,
                req.getCorrectAnswer(), req.getExplanation(), req.getMatchPairsJson());
        String optionsJson = null;
        if (req.getOptions() != null) {
            try {
                optionsJson = objectMapper.writeValueAsString(req.getOptions());
            } catch (Exception e) {
                optionsJson = "[]";
            }
        }

        Question q = Question.builder()
                .bankId(req.getBankId())
                .subject(req.getSubject().trim())
                .topic(req.getTopic().trim())
                .questionType(req.getQuestionType().toUpperCase().trim())
                .difficulty(req.getDifficulty() != null ? req.getDifficulty().toUpperCase() : "MEDIUM")
                .questionText(req.getQuestionText().trim())
                .optionsJson(optionsJson)
                .correctAnswer(req.getCorrectAnswer() != null ? req.getCorrectAnswer().trim() : "")
                .explanation(req.getExplanation())
                .marks(req.getMarks() != null ? req.getMarks() : 1.0)
                .negativeMarks(req.getNegativeMarks() != null ? req.getNegativeMarks() : 0.0)
                .matchPairsJson(req.getMatchPairsJson())
                .build();

        return questionRepository.save(q);
    }

    // Tests
    public List<Map<String, Object>> getAllTests(Long currentUserId) {
        List<Test> tests = testRepository.findAllByOrderByCreatedAtDesc();
        List<Map<String, Object>> result = new ArrayList<>();

        for (Test t : tests) {
            // Group test check
            if ("GROUP".equalsIgnoreCase(t.getTestType()) && t.getGroupId() != null) {
                boolean inGroup = groupMemberRepository.existsByGroupIdAndUserIdAndStatus(t.getGroupId(), currentUserId, "ACTIVE");
                if (!inGroup) continue;
            }

            long myAttempts = testAttemptRepository.countByTestIdAndUserId(t.getId(), currentUserId);
            Optional<TestAttempt> best = testAttemptRepository.findFirstByTestIdAndUserIdOrderByScoreDesc(t.getId(), currentUserId);

            Map<String, Object> map = new HashMap<>();
            map.put("id", t.getId());
            map.put("title", t.getTitle());
            map.put("description", t.getDescription());
            map.put("subject", t.getSubject());
            map.put("topic", t.getTopic());
            map.put("testType", t.getTestType());
            map.put("durationMinutes", t.getDurationMinutes());
            map.put("passingMarks", t.getPassingMarks());
            map.put("maxAttempts", t.getMaxAttempts());
            map.put("isOfficial", t.getIsOfficial());
            map.put("myAttemptsCount", myAttempts);
            map.put("bestScore", best.map(TestAttempt::getScore).orElse(null));
            map.put("passed", best.map(TestAttempt::getPassed).orElse(false));

            result.add(map);
        }

        return result;
    }

    @Transactional
    public Test createTest(Long userId, TestCreateRequest req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND));

        boolean isTpo = user.getRole() == Role.TPO || user.getRole() == Role.SUPER_ADMIN;
        boolean isTutor = user.getRole() == Role.TUTOR;
        boolean isGroupAdmin = req.getGroupId() != null && groupMemberRepository.findByGroupIdAndUserId(req.getGroupId(), userId)
                .map(m -> "ADMIN".equalsIgnoreCase(m.getRole())).orElse(false);

        if (!isTpo && !isTutor && !isGroupAdmin) {
            throw new ApiException("Only tutors, group admins and TPO can create tests", HttpStatus.FORBIDDEN);
        }

        if (Boolean.TRUE.equals(req.getIsOfficial()) && !isTpo) {
            throw new ApiException("Only TPO can flag tests as official", HttpStatus.FORBIDDEN);
        }

        if ("SKILL_VERIFICATION".equalsIgnoreCase(req.getTestType()) && !isTpo) {
            throw new ApiException("Only TPO can create skill verification tests", HttpStatus.FORBIDDEN);
        }

        textModerationService.check(userId, "TEST",
                req.getTitle(), req.getDescription(), req.getSubject(), req.getTopic());

        String questionIdsJson = null;
        if (req.getQuestionIds() != null && !req.getQuestionIds().isEmpty()) {
            try {
                questionIdsJson = objectMapper.writeValueAsString(req.getQuestionIds());
            } catch (Exception e) {
                questionIdsJson = "[]";
            }
        }

        Test test = Test.builder()
                .title(req.getTitle().trim())
                .description(req.getDescription())
                .subject(req.getSubject().trim())
                .topic(req.getTopic())
                .testType(req.getTestType().toUpperCase().trim())
                .groupId(req.getGroupId())
                .skillId(req.getSkillId())
                .durationMinutes(req.getDurationMinutes() != null ? req.getDurationMinutes() : 30)
                .windowStart(req.getWindowStart())
                .windowEnd(req.getWindowEnd())
                .maxAttempts(req.getMaxAttempts() != null ? req.getMaxAttempts() : 1)
                .negativeMarking(req.getNegativeMarking() != null ? req.getNegativeMarking() : false)
                .passingMarks(req.getPassingMarks() != null ? req.getPassingMarks() : 40.0)
                .isOfficial(Boolean.TRUE.equals(req.getIsOfficial()))
                .bankId(req.getBankId())
                .selectedQuestionIdsJson(questionIdsJson)
                .createdBy(userId)
                .createdAt(LocalDateTime.now())
                .build();

        return testRepository.save(test);
    }

    @Transactional
    public Map<String, Object> startTest(Long testId, Long userId) {
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new ApiException("Test not found", HttpStatus.NOT_FOUND));

        if ("GROUP".equalsIgnoreCase(test.getTestType()) && test.getGroupId() != null) {
            boolean inGroup = groupMemberRepository.existsByGroupIdAndUserIdAndStatus(test.getGroupId(), userId, "ACTIVE");
            if (!inGroup) {
                throw new ApiException("Only members of this group can take this test", HttpStatus.FORBIDDEN);
            }
        }

        long existingCount = testAttemptRepository.countByTestIdAndUserId(testId, userId);
        if (existingCount >= test.getMaxAttempts()) {
            throw new ApiException("You have reached the maximum allowed attempts (" + test.getMaxAttempts() + ") for this test", HttpStatus.BAD_REQUEST);
        }

        // Check if an attempt is currently IN_PROGRESS
        List<TestAttempt> userAttempts = testAttemptRepository.findByTestIdAndUserIdOrderByAttemptNumberDesc(testId, userId);
        for (TestAttempt a : userAttempts) {
            if ("IN_PROGRESS".equalsIgnoreCase(a.getStatus())) {
                if (LocalDateTime.now().isBefore(a.getEndTime())) {
                    return resumeAttempt(a, test);
                } else {
                    submitTest(a.getId(), userId); // auto submit expired attempt
                }
            }
        }

        // Select questions
        List<Long> questionIds = new ArrayList<>();
        if (test.getSelectedQuestionIdsJson() != null) {
            try {
                questionIds = objectMapper.readValue(test.getSelectedQuestionIdsJson(), new TypeReference<List<Long>>() {});
            } catch (Exception ignored) {}
        }

        if (questionIds.isEmpty()) {
            List<Question> bankQuestions;
            if (test.getBankId() != null) {
                bankQuestions = questionRepository.findByBankId(test.getBankId());
            } else if (test.getTopic() != null) {
                bankQuestions = questionRepository.findBySubjectIgnoreCaseAndTopicIgnoreCase(test.getSubject(), test.getTopic());
            } else {
                bankQuestions = questionRepository.findBySubjectIgnoreCase(test.getSubject());
            }

            if ("ADAPTIVE".equalsIgnoreCase(test.getTestType())) {
                // Adaptive starts with medium questions
                Collections.shuffle(bankQuestions);
                questionIds = bankQuestions.stream().limit(10).map(Question::getId).collect(Collectors.toList());
            } else {
                Collections.shuffle(bankQuestions);
                int limit = Math.min(bankQuestions.size(), 10);
                questionIds = bankQuestions.stream().limit(limit).map(Question::getId).collect(Collectors.toList());
            }
        } else {
            Collections.shuffle(questionIds);
        }

        if (questionIds.isEmpty()) {
            throw new ApiException("No questions found in this question bank/subject", HttpStatus.BAD_REQUEST);
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime deadline = now.plusMinutes(test.getDurationMinutes());

        String orderJson;
        try {
            orderJson = objectMapper.writeValueAsString(questionIds);
        } catch (Exception e) {
            orderJson = "[]";
        }

        TestAttempt attempt = TestAttempt.builder()
                .testId(testId)
                .userId(userId)
                .attemptNumber((int) existingCount + 1)
                .startTime(now)
                .endTime(deadline)
                .status("IN_PROGRESS")
                .adaptiveDifficulty("MEDIUM")
                .currentQuestionIndex(0)
                .questionOrderJson(orderJson)
                .build();

        TestAttempt saved = testAttemptRepository.save(attempt);

        return formatAttemptResponse(saved, test, questionIds);
    }

    private Map<String, Object> resumeAttempt(TestAttempt attempt, Test test) {
        List<Long> questionIds = getQuestionIdsFromAttempt(attempt);
        return formatAttemptResponse(attempt, test, questionIds);
    }

    private List<Long> getQuestionIdsFromAttempt(TestAttempt attempt) {
        try {
            return objectMapper.readValue(attempt.getQuestionOrderJson(), new TypeReference<List<Long>>() {});
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private Map<String, Object> formatAttemptResponse(TestAttempt attempt, Test test, List<Long> questionIds) {
        List<Question> questions = questionRepository.findAllById(questionIds);
        Map<Long, Question> qMap = questions.stream().collect(Collectors.toMap(Question::getId, q -> q));

        // Get saved student answers for this attempt
        List<StudentAnswer> answers = studentAnswerRepository.findByAttemptId(attempt.getId());
        Map<Long, String> ansMap = answers.stream().collect(Collectors.toMap(StudentAnswer::getQuestionId, a -> a.getStudentAnswer() != null ? a.getStudentAnswer() : ""));

        // Build question payload (WITHOUT correct answers and explanations!)
        List<Map<String, Object>> safeQuestions = new ArrayList<>();
        for (Long qId : questionIds) {
            Question q = qMap.get(qId);
            if (q == null) continue;

            Map<String, Object> qData = new HashMap<>();
            qData.put("id", q.getId());
            qData.put("subject", q.getSubject());
            qData.put("topic", q.getTopic());
            qData.put("questionType", q.getQuestionType());
            qData.put("difficulty", q.getDifficulty());
            qData.put("questionText", q.getQuestionText());
            qData.put("marks", q.getMarks());
            qData.put("negativeMarks", q.getNegativeMarks());
            qData.put("matchPairsJson", q.getMatchPairsJson());

            // Options for MCQ / TRUE_FALSE (shuffle options stably or provide list)
            if (q.getOptionsJson() != null) {
                try {
                    List<String> options = objectMapper.readValue(q.getOptionsJson(), new TypeReference<List<String>>() {});
                    qData.put("options", options);
                } catch (Exception ignored) {
                    qData.put("options", Collections.emptyList());
                }
            }

            qData.put("savedAnswer", ansMap.get(q.getId()));
            safeQuestions.add(qData);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("attemptId", attempt.getId());
        response.put("testId", test.getId());
        response.put("title", test.getTitle());
        response.put("durationMinutes", test.getDurationMinutes());
        response.put("startTime", attempt.getStartTime());
        response.put("endTime", attempt.getEndTime());
        long remainingSeconds = ChronoUnit.SECONDS.between(LocalDateTime.now(), attempt.getEndTime());
        response.put("remainingSeconds", Math.max(0, remainingSeconds));
        response.put("testType", test.getTestType());
        response.put("questions", safeQuestions);

        return response;
    }

    @Transactional
    public void recordAnswer(Long attemptId, Long userId, AnswerSubmitRequest req) {
        TestAttempt attempt = testAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new ApiException("Attempt not found", HttpStatus.NOT_FOUND));

        if (!attempt.getUserId().equals(userId)) {
            throw new ApiException("Unauthorized access to this test attempt", HttpStatus.FORBIDDEN);
        }

        if (!"IN_PROGRESS".equalsIgnoreCase(attempt.getStatus())) {
            throw new ApiException("This test has already been submitted", HttpStatus.BAD_REQUEST);
        }

        if (LocalDateTime.now().isAfter(attempt.getEndTime())) {
            submitTest(attemptId, userId);
            throw new ApiException("Time expired. Test has been auto-submitted.", HttpStatus.BAD_REQUEST);
        }

        Optional<StudentAnswer> existing = studentAnswerRepository.findByAttemptIdAndQuestionId(attemptId, req.getQuestionId());
        StudentAnswer sa = existing.orElse(StudentAnswer.builder()
                .attemptId(attemptId)
                .questionId(req.getQuestionId())
                .build());

        sa.setStudentAnswer(req.getAnswer() != null ? req.getAnswer().trim() : "");
        studentAnswerRepository.save(sa);
    }

    @Transactional
    public Map<String, Object> submitTest(Long attemptId, Long userId) {
        TestAttempt attempt = testAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new ApiException("Attempt not found", HttpStatus.NOT_FOUND));

        if (!attempt.getUserId().equals(userId)) {
            throw new ApiException("Unauthorized attempt access", HttpStatus.FORBIDDEN);
        }

        Test test = testRepository.findById(attempt.getTestId())
                .orElseThrow(() -> new ApiException("Test not found", HttpStatus.NOT_FOUND));

        List<Long> questionIds = getQuestionIdsFromAttempt(attempt);
        List<Question> questions = questionRepository.findAllById(questionIds);
        Map<Long, Question> qMap = questions.stream().collect(Collectors.toMap(Question::getId, q -> q));

        List<StudentAnswer> answers = studentAnswerRepository.findByAttemptId(attemptId);
        Map<Long, StudentAnswer> ansMap = answers.stream().collect(Collectors.toMap(StudentAnswer::getQuestionId, a -> a));

        double totalMarks = 0.0;
        double score = 0.0;
        Map<String, int[]> topicAccuracy = new HashMap<>(); // [correct, total]

        for (Long qId : questionIds) {
            Question q = qMap.get(qId);
            if (q == null) continue;

            double marks = q.getMarks() != null ? q.getMarks() : 1.0;
            double negMarks = Boolean.TRUE.equals(test.getNegativeMarking()) && q.getNegativeMarks() != null ? q.getNegativeMarks() : 0.0;
            totalMarks += marks;

            String topic = q.getTopic() != null ? q.getTopic() : "General";
            topicAccuracy.putIfAbsent(topic, new int[]{0, 0});
            topicAccuracy.get(topic)[1]++;

            StudentAnswer sa = ansMap.get(qId);
            String studentAns = sa != null && sa.getStudentAnswer() != null ? sa.getStudentAnswer().trim() : "";

            boolean correct = checkAnswer(q, studentAns);
            if (correct) {
                score += marks;
                topicAccuracy.get(topic)[0]++;
            } else if (!studentAns.isEmpty() && negMarks > 0) {
                score -= negMarks;
            }

            if (sa == null) {
                sa = StudentAnswer.builder().attemptId(attemptId).questionId(qId).studentAnswer("").build();
            }
            sa.setIsCorrect(correct);
            sa.setMarksAwarded(correct ? marks : (!studentAns.isEmpty() && negMarks > 0 ? -negMarks : 0.0));
            studentAnswerRepository.save(sa);
        }

        score = Math.max(0.0, Math.round(score * 100.0) / 100.0);
        double percentage = totalMarks > 0 ? Math.round((score / totalMarks * 100.0) * 10.0) / 10.0 : 0.0;
        boolean passed = percentage >= (test.getPassingMarks() != null ? test.getPassingMarks() : 40.0);

        LocalDateTime now = LocalDateTime.now();
        int timeTaken = (int) ChronoUnit.SECONDS.between(attempt.getStartTime(), now);

        attempt.setStatus("SUBMITTED");
        attempt.setSubmittedAt(now);
        attempt.setTimeTakenSeconds(timeTaken);
        attempt.setScore(score);
        attempt.setTotalMarks(totalMarks);
        attempt.setPercentage(percentage);
        attempt.setPassed(passed);
        testAttemptRepository.save(attempt);

        // Update rank
        List<TestAttempt> allAttempts = testAttemptRepository.findByTestIdOrderByScoreDescTimeTakenSecondsAsc(test.getId());
        int rank = 1;
        for (TestAttempt a : allAttempts) {
            a.setRankInTest(rank++);
            testAttemptRepository.save(a);
        }

        // Gamification XP: quiz completed +10 (+20 if passed, first attempt only)
        if (attempt.getAttemptNumber() != null && attempt.getAttemptNumber() == 1) {
            int xpEarned = 10 + (passed ? 20 : 0);
            gamificationService.awardXp(userId, xpEarned, "Completed " + test.getTitle() + (passed ? " (Passed)" : ""));
        }

        // If SKILL_VERIFICATION test passed -> verify user's skill!
        if (passed && "SKILL_VERIFICATION".equalsIgnoreCase(test.getTestType()) && test.getSkillId() != null) {
            userSkillRepository.findByUserIdAndSkillId(userId, test.getSkillId()).ifPresent(us -> {
                us.setVerified(true);
                userSkillRepository.save(us);
                notificationService.sendNotification(
                        userId,
                        "Skill Verified!",
                        "Congratulations! You passed the verification test for " + us.getSkillName() + " and are now a verified tutor!",
                        "BADGE",
                        us.getId()
                );
            });
        }

        // Update Placement Profile aptitude if MOCK test
        if ("MOCK".equalsIgnoreCase(test.getTestType())) {
            updatePlacementAptitude(userId);
        }

        return getAttemptResult(attemptId, userId);
    }

    private boolean checkAnswer(Question q, String studentAns) {
        if (studentAns == null || studentAns.isBlank()) return false;
        String correct = q.getCorrectAnswer() != null ? q.getCorrectAnswer().trim() : "";
        String type = q.getQuestionType() != null ? q.getQuestionType().toUpperCase() : "MCQ";

        switch (type) {
            case "MCQ":
            case "TRUE_FALSE":
            case "SHORT":
                return AnswerMatcher.matchesShortAnswer(studentAns, correct);

            case "FILL_BLANK":
                // Alternatives separated by '|'
                String[] alternatives = correct.split("\\|");
                for (String alt : alternatives) {
                    if (alt.trim().equalsIgnoreCase(studentAns.trim())) return true;
                }
                return false;

            case "NUMERIC":
                return AnswerMatcher.matchesNumeric(studentAns, correct, new java.math.BigDecimal("0.01"));

            case "MATCH":
                return studentAns.equalsIgnoreCase(correct);

            default:
                return studentAns.equalsIgnoreCase(correct);
        }
    }

    public Map<String, Object> getAttemptResult(Long attemptId, Long userId) {
        TestAttempt attempt = testAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new ApiException("Attempt not found", HttpStatus.NOT_FOUND));

        if (!attempt.getUserId().equals(userId)) {
            throw new ApiException("Unauthorized access to result", HttpStatus.FORBIDDEN);
        }

        Test test = testRepository.findById(attempt.getTestId())
                .orElseThrow(() -> new ApiException("Test not found", HttpStatus.NOT_FOUND));

        List<Long> questionIds = getQuestionIdsFromAttempt(attempt);
        List<Question> questions = questionRepository.findAllById(questionIds);
        Map<Long, Question> qMap = questions.stream().collect(Collectors.toMap(Question::getId, q -> q));

        List<StudentAnswer> answers = studentAnswerRepository.findByAttemptId(attemptId);
        Map<Long, StudentAnswer> ansMap = answers.stream().collect(Collectors.toMap(StudentAnswer::getQuestionId, a -> a));

        List<Map<String, Object>> reviews = new ArrayList<>();
        Map<String, int[]> topicStats = new HashMap<>(); // [correct, total]

        for (Long qId : questionIds) {
            Question q = qMap.get(qId);
            if (q == null) continue;

            String topic = q.getTopic() != null ? q.getTopic() : "General";
            topicStats.putIfAbsent(topic, new int[]{0, 0});
            topicStats.get(topic)[1]++;

            StudentAnswer sa = ansMap.get(qId);
            boolean isCorrect = sa != null && Boolean.TRUE.equals(sa.getIsCorrect());
            if (isCorrect) topicStats.get(topic)[0]++;

            Map<String, Object> r = new HashMap<>();
            r.put("id", q.getId());
            r.put("questionText", q.getQuestionText());
            r.put("topic", q.getTopic());
            r.put("questionType", q.getQuestionType());
            r.put("studentAnswer", sa != null ? sa.getStudentAnswer() : "");
            r.put("correctAnswer", q.getCorrectAnswer()); // EXPOSED ONLY AFTER SUBMIT
            r.put("explanation", q.getExplanation());
            r.put("isCorrect", isCorrect);
            r.put("marksAwarded", sa != null ? sa.getMarksAwarded() : 0.0);
            reviews.add(r);
        }

        Map<String, Double> topicAccuracy = new HashMap<>();
        for (Map.Entry<String, int[]> entry : topicStats.entrySet()) {
            double acc = entry.getValue()[1] > 0 ? ((double) entry.getValue()[0] / entry.getValue()[1]) * 100.0 : 0.0;
            topicAccuracy.put(entry.getKey(), Math.round(acc * 10.0) / 10.0);
        }

        boolean canHaveCertificate = Boolean.TRUE.equals(attempt.getPassed()) &&
                ("MOCK".equalsIgnoreCase(test.getTestType()) ||
                 "SKILL_VERIFICATION".equalsIgnoreCase(test.getTestType()) ||
                 Boolean.TRUE.equals(test.getIsOfficial()));

        Map<String, Object> result = new HashMap<>();
        result.put("attemptId", attempt.getId());
        result.put("testId", test.getId());
        result.put("title", test.getTitle());
        result.put("subject", test.getSubject());
        result.put("score", attempt.getScore());
        result.put("totalMarks", attempt.getTotalMarks());
        result.put("percentage", attempt.getPercentage());
        result.put("passed", attempt.getPassed());
        result.put("rank", attempt.getRankInTest() != null ? attempt.getRankInTest() : 1);
        result.put("timeTakenSeconds", attempt.getTimeTakenSeconds());
        result.put("topicAccuracy", topicAccuracy);
        result.put("canHaveCertificate", canHaveCertificate);
        result.put("questionsReview", reviews);

        return result;
    }

    public List<Map<String, Object>> getTestLeaderboard(Long testId) {
        List<TestAttempt> attempts = testAttemptRepository.findByTestIdOrderByScoreDescTimeTakenSecondsAsc(testId);

        // Keep best attempt per student
        Map<Long, TestAttempt> bestMap = new LinkedHashMap<>();
        for (TestAttempt a : attempts) {
            if (!bestMap.containsKey(a.getUserId())) {
                bestMap.put(a.getUserId(), a);
            }
        }

        List<Map<String, Object>> leaderboard = new ArrayList<>();
        int rank = 1;
        for (TestAttempt a : bestMap.values()) {
            Map<String, Object> map = new HashMap<>();
            map.put("rank", rank++);
            map.put("score", a.getScore());
            map.put("percentage", a.getPercentage());
            map.put("timeTakenSeconds", a.getTimeTakenSeconds());
            map.put("submittedAt", a.getSubmittedAt());

            userRepository.findById(a.getUserId()).ifPresent(u -> {
                map.put("userId", u.getId());
                map.put("fullName", u.getFullName());
                map.put("photoUrl", u.getPhotoUrl());
                map.put("branch", u.getBranch());
                map.put("studyYear", u.getStudyYear());
            });

            leaderboard.add(map);
        }

        return leaderboard;
    }

    private void updatePlacementAptitude(Long userId) {
        List<TestAttempt> mockAttempts = testAttemptRepository.findByUserIdOrderByStartTimeDesc(userId).stream()
                .filter(a -> {
                    Test t = testRepository.findById(a.getTestId()).orElse(null);
                    return t != null && "MOCK".equalsIgnoreCase(t.getTestType());
                }).collect(Collectors.toList());

        if (!mockAttempts.isEmpty()) {
            double avg = mockAttempts.stream().mapToDouble(a -> a.getPercentage() != null ? a.getPercentage() : 0.0).average().orElse(0.0);
            placementProfileRepository.findByUserId(userId).ifPresent(p -> {
                p.setAptitudeScore(Math.round(avg * 10.0) / 10.0);
                placementProfileRepository.save(p);
            });
        }
    }

    private void verifyBankCreator(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND));
        if (user.getRole() != Role.TUTOR && user.getRole() != Role.TPO && user.getRole() != Role.SUPER_ADMIN) {
            throw new ApiException("Only tutors, group admins and faculty can create question banks and questions", HttpStatus.FORBIDDEN);
        }
    }
}
