package com.campusskills.controller;

import com.campusskills.dto.RatingRequest;
import com.campusskills.dto.SessionCreateRequest;
import com.campusskills.entity.Rating;
import com.campusskills.entity.TutoringSession;
import com.campusskills.entity.User;
import com.campusskills.service.TutoringSessionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sessions")
public class TutoringSessionController {

    private final TutoringSessionService tutoringSessionService;

    public TutoringSessionController(TutoringSessionService tutoringSessionService) {
        this.tutoringSessionService = tutoringSessionService;
    }

    @PostMapping
    public ResponseEntity<TutoringSession> createSession(@AuthenticationPrincipal User user,
                                                         @Valid @RequestBody SessionCreateRequest request) {
        return ResponseEntity.ok(tutoringSessionService.createSession(user.getId(), request));
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getMySessions(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(tutoringSessionService.getMySessions(user.getId()));
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<TutoringSession> completeSession(@AuthenticationPrincipal User user,
                                                           @PathVariable Long id) {
        return ResponseEntity.ok(tutoringSessionService.completeSession(user.getId(), id));
    }

    @PostMapping("/{id}/rating")
    public ResponseEntity<Rating> rateSession(@AuthenticationPrincipal User user,
                                              @PathVariable Long id,
                                              @Valid @RequestBody RatingRequest request) {
        return ResponseEntity.ok(tutoringSessionService.rateSession(user.getId(), id, request));
    }
}
