package com.campusskills.service;

import com.campusskills.entity.*;
import com.campusskills.repository.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    private final TestAttemptRepository testAttemptRepository;
    private final StudentAnswerRepository studentAnswerRepository;
    private final QuestionRepository questionRepository;
    private final TestRepository testRepository;
    private final StudyGroupRepository studyGroupRepository;
    private final UserSkillRepository userSkillRepository;
    private final AvailabilityRepository availabilityRepository;
    private final UserRepository userRepository;
    private final GamificationService gamificationService;

    public RecommendationService(TestAttemptRepository testAttemptRepository,
                                  StudentAnswerRepository studentAnswerRepository,
                                  QuestionRepository questionRepository,
                                  TestRepository testRepository,
                                  StudyGroupRepository studyGroupRepository,
                                  UserSkillRepository userSkillRepository,
                                  AvailabilityRepository availabilityRepository,
                                  UserRepository userRepository,
                                  GamificationService gamificationService) {
        this.testAttemptRepository = testAttemptRepository;
        this.studentAnswerRepository = studentAnswerRepository;
        this.questionRepository = questionRepository;
        this.testRepository = testRepository;
        this.studyGroupRepository = studyGroupRepository;
        this.userSkillRepository = userSkillRepository;
        this.availabilityRepository = availabilityRepository;
        this.userRepository = userRepository;
        this.gamificationService = gamificationService;
    }

    public Map<String, Object> getRecommendations(Long userId) {
        List<TestAttempt> attempts = testAttemptRepository.findByUserIdOrderByStartTimeDesc(userId);

        Map<String, int[]> topicStats = new HashMap<>(); // [correct, total]
        String latestTestSummary = null;

        if (!attempts.isEmpty()) {
            TestAttempt latest = attempts.get(0);
            Test t = testRepository.findById(latest.getTestId()).orElse(null);
            if (t != null) {
                latestTestSummary = t.getTitle() + " (" + latest.getPercentage() + "%)";
            }
        }

        for (TestAttempt a : attempts) {
            List<StudentAnswer> answers = studentAnswerRepository.findByAttemptId(a.getId());
            for (StudentAnswer sa : answers) {
                questionRepository.findById(sa.getQuestionId()).ifPresent(q -> {
                    String topic = q.getTopic() != null ? q.getTopic() : "General";
                    topicStats.putIfAbsent(topic, new int[]{0, 0});
                    topicStats.get(topic)[1]++;
                    if (Boolean.TRUE.equals(sa.getIsCorrect())) {
                        topicStats.get(topic)[0]++;
                    }
                });
            }
        }

        // If no test history yet, provide default weak topics for demo onboarding
        if (topicStats.isEmpty()) {
            topicStats.put("OOPs", new int[]{2, 5});        // 40%
            topicStats.put("Collections", new int[]{1, 2}); // 50%
            latestTestSummary = "Java Mock (55%)";
        }

        List<Map<String, Object>> weakTopics = new ArrayList<>();
        List<String> weakTopicNames = new ArrayList<>();

        for (Map.Entry<String, int[]> entry : topicStats.entrySet()) {
            String topic = entry.getKey();
            int correct = entry.getValue()[0];
            int total = entry.getValue()[1];
            double accuracy = total > 0 ? Math.round(((double) correct / total) * 100.0) : 0.0;

            if (accuracy < 60.0) { // Weak topic (< 60%)
                weakTopicNames.add(topic);
                Map<String, Object> weakData = new HashMap<>();
                weakData.put("topic", topic);
                weakData.put("accuracy", accuracy);

                // (a) Suggested PRACTICE / ADAPTIVE quizzes
                List<Test> suggestedTests = testRepository.findAll().stream()
                        .filter(t -> (t.getTopic() != null && t.getTopic().equalsIgnoreCase(topic)) ||
                                     (t.getTitle() != null && t.getTitle().toLowerCase().contains(topic.toLowerCase())))
                        .limit(3)
                        .collect(Collectors.toList());
                weakData.put("suggestedTests", suggestedTests);

                // (b) Study groups to join
                List<StudyGroup> suggestedGroups = studyGroupRepository.findAll().stream()
                        .filter(g -> (g.getSubject() != null && g.getSubject().toLowerCase().contains(topic.toLowerCase())) ||
                                     (g.getName() != null && g.getName().toLowerCase().contains(topic.toLowerCase())))
                        .limit(3)
                        .collect(Collectors.toList());
                weakData.put("suggestedGroups", suggestedGroups);

                // (c) Tutors who teach it
                // Match %: 40% rating, 30% reliability, 20% verified, 10% shared availability
                List<Map<String, Object>> suggestedTutors = findTutorsForTopic(userId, topic);
                weakData.put("suggestedTutors", suggestedTutors);

                weakTopics.add(weakData);
            }
        }

        // Weak topic summary string
        StringBuilder summary = new StringBuilder();
        if (latestTestSummary != null) summary.append(latestTestSummary).append(". ");
        if (!weakTopicNames.isEmpty()) {
            summary.append("Weak: ");
            for (int i = 0; i < weakTopics.size(); i++) {
                if (i > 0) summary.append(", ");
                summary.append(weakTopics.get(i).get("topic")).append(" ").append(weakTopics.get(i).get("accuracy")).append("%");
            }
            summary.append(". ");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("weakTopics", weakTopics);
        response.put("summary", summary.toString());
        return response;
    }

    private List<Map<String, Object>> findTutorsForTopic(Long currentUserId, String topic) {
        List<UserSkill> skills = userSkillRepository.findBySkillType("CAN_TEACH").stream()
                .filter(s -> s.getSkillName().toLowerCase().contains(topic.toLowerCase()) ||
                             topic.toLowerCase().contains(s.getSkillName().toLowerCase()))
                .collect(Collectors.toList());

        List<Availability> myAvail = availabilityRepository.findByUserId(currentUserId);
        Set<String> myDays = myAvail.stream().map(Availability::getDayOfWeek).collect(Collectors.toSet());

        List<Map<String, Object>> tutors = new ArrayList<>();
        Set<Long> seen = new HashSet<>();

        for (UserSkill us : skills) {
            if (us.getUserId().equals(currentUserId) || seen.contains(us.getUserId())) continue;
            seen.add(us.getUserId());

            User tutor = userRepository.findById(us.getUserId()).orElse(null);
            if (tutor == null || tutor.getStatus() != UserStatus.VERIFIED) continue;

            double rating = gamificationService.calculateAverageRating(tutor.getId());
            double ratingPart = (rating / 5.0) * 40.0;

            double reliability = gamificationService.calculateReliabilityScore(tutor.getId());
            double reliabilityPart = (reliability / 100.0) * 30.0;

            double verifiedPart = Boolean.TRUE.equals(us.getVerified()) ? 20.0 : 5.0;

            List<Availability> tutorAvail = availabilityRepository.findByUserId(tutor.getId());
            boolean sharedAvail = tutorAvail.stream().anyMatch(a -> myDays.contains(a.getDayOfWeek()));
            double availPart = sharedAvail ? 10.0 : 3.0;

            int matchPercent = (int) Math.min(100, Math.round(ratingPart + reliabilityPart + verifiedPart + availPart));

            Map<String, Object> map = new HashMap<>();
            map.put("tutorId", tutor.getId());
            map.put("fullName", tutor.getFullName());
            map.put("photoUrl", tutor.getPhotoUrl());
            map.put("matchPercent", matchPercent);
            map.put("rating", rating);
            map.put("isVerified", us.getVerified());
            map.put("skillName", us.getSkillName());
            tutors.add(map);
        }

        tutors.sort((a, b) -> Integer.compare((Integer) b.get("matchPercent"), (Integer) a.get("matchPercent")));
        return tutors.stream().limit(3).collect(Collectors.toList());
    }
}
