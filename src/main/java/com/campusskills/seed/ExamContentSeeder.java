package com.campusskills.seed;

import com.campusskills.entity.Question;
import com.campusskills.entity.QuestionBank;
import com.campusskills.entity.Role;
import com.campusskills.entity.Test;
import com.campusskills.entity.User;
import com.campusskills.repository.QuestionBankRepository;
import com.campusskills.repository.QuestionRepository;
import com.campusskills.repository.TestRepository;
import com.campusskills.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Seeds the ready-to-use subject-wise practice and mock exams in the MySQL profile.
 * Every set is isolated in its own question bank and is safe to seed repeatedly.
 */
@Component
@Profile("mysql")
@Order(10)
public class ExamContentSeeder implements CommandLineRunner {

    private static final String MIXED_TOPIC = "Mixed Topics";

    private final QuestionBankRepository questionBankRepository;
    private final QuestionRepository questionRepository;
    private final TestRepository testRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final String adminEmail;

    public ExamContentSeeder(
            QuestionBankRepository questionBankRepository,
            QuestionRepository questionRepository,
            TestRepository testRepository,
            UserRepository userRepository,
            ObjectMapper objectMapper,
            @Value("${campusskills.admin.email}") String adminEmail) {
        this.questionBankRepository = questionBankRepository;
        this.questionRepository = questionRepository;
        this.testRepository = testRepository;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
        this.adminEmail = adminEmail.trim().toLowerCase(Locale.ROOT);
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        User admin = userRepository.findByEmail(adminEmail)
                .filter(user -> user.getRole() == Role.SUPER_ADMIN)
                .orElseThrow(() -> new IllegalStateException(
                        "The CampusSkills administrator must be initialized before exam content is seeded."));

        JsonNode sets;
        try (InputStream input = new ClassPathResource("seed/exam-content.json").getInputStream()) {
            sets = objectMapper.readTree(input);
        }

        int createdTests = 0;
        int createdQuestions = 0;

        for (JsonNode set : sets) {
            String subject = requiredText(set, "subject");
            String setName = requiredText(set, "set");
            String testType = requiredText(set, "type").toUpperCase(Locale.ROOT);
            String title = subject + " | " + setName;

            if (!"PRACTICE".equals(testType) && !"MOCK".equals(testType)) {
                throw new IllegalStateException("Unsupported seeded test type: " + testType);
            }

            QuestionBank bank = questionBankRepository
                    .findFirstByTitleAndSubjectAndTopic(title, subject, MIXED_TOPIC)
                    .orElseGet(() -> questionBankRepository.save(QuestionBank.builder()
                            .title(title)
                            .subject(subject)
                            .topic(MIXED_TOPIC)
                            .createdBy(admin.getId())
                            .createdAt(LocalDateTime.now())
                            .build()));

            List<Question> existing = questionRepository.findByBankId(bank.getId());
            Set<String> existingQuestionTexts = new HashSet<>();
            for (Question question : existing) {
                existingQuestionTexts.add(question.getQuestionText());
            }

            JsonNode questions = set.path("questions");
            if (!questions.isArray() || questions.isEmpty()) {
                throw new IllegalStateException("Exam set has no questions: " + title);
            }

            for (JsonNode item : questions) {
                String questionText = requiredText(item, "questionText");
                if (existingQuestionTexts.contains(questionText)) {
                    continue;
                }

                JsonNode optionNodes = item.path("options");
                if (!optionNodes.isArray() || optionNodes.size() != 4) {
                    throw new IllegalStateException("Every seeded MCQ must have four options: " + questionText);
                }

                List<String> options = new ArrayList<>();
                optionNodes.forEach(option -> options.add(option.asText()));
                int correctIndex = item.path("correctIndex").asInt(-1);
                if (correctIndex < 0 || correctIndex >= options.size()) {
                    throw new IllegalStateException("Invalid answer key for: " + questionText);
                }

                String optionsJson = objectMapper.writeValueAsString(options);
                Question question = Question.builder()
                        .bankId(bank.getId())
                        .subject(subject)
                        .topic(requiredText(item, "topic"))
                        .questionType("MCQ")
                        .difficulty(requiredText(item, "difficulty").toUpperCase(Locale.ROOT))
                        .questionText(questionText)
                        .optionsJson(optionsJson)
                        .correctAnswer(options.get(correctIndex))
                        .explanation(requiredText(item, "explanation"))
                        .marks(1.0)
                        .negativeMarks(0.25)
                        .build();

                questionRepository.save(question);
                existingQuestionTexts.add(questionText);
                createdQuestions++;
            }

            if (!testRepository.existsByTitleAndSubject(title, subject)) {
                Test test = Test.builder()
                        .title(title)
                        .description("Subject-wise " + setName + " with " + questions.size()
                                + " questions, answer explanations and automatic scoring.")
                        .subject(subject)
                        .topic(MIXED_TOPIC)
                        .testType(testType)
                        .durationMinutes(set.path("durationMinutes").asInt(20))
                        .maxAttempts(set.path("maxAttempts").asInt(3))
                        .passingMarks(set.path("passingMarks").asDouble(50.0))
                        .negativeMarking(set.path("negativeMarking").asBoolean(false))
                        .isOfficial(false)
                        .bankId(bank.getId())
                        .createdBy(admin.getId())
                        .createdAt(LocalDateTime.now())
                        .build();
                testRepository.save(test);
                createdTests++;
            }
        }

        System.out.println("CampusSkills exam content ready: " + createdTests
                + " new tests and " + createdQuestions + " new questions.");
    }

    private String requiredText(JsonNode node, String field) {
        String value = node.path(field).asText("").trim();
        if (value.isEmpty()) {
            throw new IllegalStateException("Missing required exam content field: " + field);
        }
        return value;
    }
}
