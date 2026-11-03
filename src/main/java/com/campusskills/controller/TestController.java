package com.campusskills.controller;

import com.campusskills.dto.*;
import com.campusskills.entity.Question;
import com.campusskills.entity.QuestionBank;
import com.campusskills.entity.Test;
import com.campusskills.entity.User;
import com.campusskills.service.TestService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class TestController {

    private final TestService testService;

    public TestController(TestService testService) {
        this.testService = testService;
    }

    @GetMapping("/api/question-banks")
    public ResponseEntity<List<QuestionBank>> getAllBanks() {
        return ResponseEntity.ok(testService.getAllBanks());
    }

    @PostMapping("/api/question-banks")
    public ResponseEntity<QuestionBank> createBank(@AuthenticationPrincipal User user,
                                                   @Valid @RequestBody QuestionBankRequest request) {
        return ResponseEntity.ok(testService.createBank(user.getId(), request));
    }

    @PostMapping("/api/questions")
    public ResponseEntity<Question> createQuestion(@AuthenticationPrincipal User user,
                                                   @Valid @RequestBody QuestionRequest request) {
        return ResponseEntity.ok(testService.createQuestion(user.getId(), request));
    }

    @GetMapping("/api/tests")
    public ResponseEntity<List<Map<String, Object>>> getAllTests(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(testService.getAllTests(user.getId()));
    }

    @PostMapping("/api/tests")
    public ResponseEntity<Test> createTest(@AuthenticationPrincipal User user,
                                           @Valid @RequestBody TestCreateRequest request) {
        return ResponseEntity.ok(testService.createTest(user.getId(), request));
    }

    @PostMapping("/api/tests/{id}/start")
    public ResponseEntity<Map<String, Object>> startTest(@AuthenticationPrincipal User user,
                                                         @PathVariable Long id) {
        return ResponseEntity.ok(testService.startTest(id, user.getId()));
    }

    @PostMapping("/api/attempts/{id}/answer")
    public ResponseEntity<Map<String, String>> recordAnswer(@AuthenticationPrincipal User user,
                                                            @PathVariable Long id,
                                                            @Valid @RequestBody AnswerSubmitRequest request) {
        testService.recordAnswer(id, user.getId(), request);
        return ResponseEntity.ok(Map.of("message", "Answer recorded"));
    }

    @PostMapping("/api/attempts/{id}/submit")
    public ResponseEntity<Map<String, Object>> submitTest(@AuthenticationPrincipal User user,
                                                          @PathVariable Long id) {
        return ResponseEntity.ok(testService.submitTest(id, user.getId()));
    }

    @GetMapping("/api/attempts/{id}/result")
    public ResponseEntity<Map<String, Object>> getAttemptResult(@AuthenticationPrincipal User user,
                                                                @PathVariable Long id) {
        return ResponseEntity.ok(testService.getAttemptResult(id, user.getId()));
    }

    @GetMapping("/api/tests/{id}/leaderboard")
    public ResponseEntity<List<Map<String, Object>>> getTestLeaderboard(@PathVariable Long id) {
        return ResponseEntity.ok(testService.getTestLeaderboard(id));
    }
}
