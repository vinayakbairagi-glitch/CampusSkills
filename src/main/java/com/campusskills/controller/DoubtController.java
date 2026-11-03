package com.campusskills.controller;

import com.campusskills.dto.AnswerCreateRequest;
import com.campusskills.dto.DoubtCreateRequest;
import com.campusskills.entity.Doubt;
import com.campusskills.entity.DoubtAnswer;
import com.campusskills.entity.User;
import com.campusskills.service.DoubtService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class DoubtController {

    private final DoubtService doubtService;

    public DoubtController(DoubtService doubtService) {
        this.doubtService = doubtService;
    }

    @PostMapping("/api/doubts")
    public ResponseEntity<Doubt> createDoubt(@AuthenticationPrincipal User user,
                                             @Valid @RequestBody DoubtCreateRequest request) {
        return ResponseEntity.ok(doubtService.createDoubt(user.getId(), request));
    }

    @GetMapping("/api/doubts")
    public ResponseEntity<List<Map<String, Object>>> getDoubts(@RequestParam(required = false) String query,
                                                               @RequestParam(required = false) String subject,
                                                               @RequestParam(required = false) String status) {
        return ResponseEntity.ok(doubtService.searchDoubts(query, subject, status));
    }

    @GetMapping("/api/doubts/{id}")
    public ResponseEntity<Map<String, Object>> getDoubtDetail(@AuthenticationPrincipal User user,
                                                              @PathVariable Long id) {
        return ResponseEntity.ok(doubtService.getDoubtDetail(id, user.getId()));
    }

    @PostMapping("/api/doubts/{id}/answers")
    public ResponseEntity<DoubtAnswer> addAnswer(@AuthenticationPrincipal User user,
                                                 @PathVariable Long id,
                                                 @Valid @RequestBody AnswerCreateRequest request) {
        return ResponseEntity.ok(doubtService.addAnswer(id, user.getId(), request));
    }

    @PutMapping("/api/answers/{id}/best")
    public ResponseEntity<Doubt> markBestAnswer(@AuthenticationPrincipal User user,
                                                @PathVariable Long id) {
        return ResponseEntity.ok(doubtService.markBestAnswer(id, user.getId()));
    }

    @PostMapping("/api/answers/{id}/upvote")
    public ResponseEntity<Map<String, Object>> upvoteAnswer(@AuthenticationPrincipal User user,
                                                            @PathVariable Long id) {
        return ResponseEntity.ok(doubtService.toggleUpvote(id, user.getId()));
    }

    @PutMapping("/api/answers/{id}/verify")
    public ResponseEntity<DoubtAnswer> verifyAnswer(@AuthenticationPrincipal User user,
                                                    @PathVariable Long id) {
        return ResponseEntity.ok(doubtService.verifyAnswer(id, user.getId()));
    }
}
