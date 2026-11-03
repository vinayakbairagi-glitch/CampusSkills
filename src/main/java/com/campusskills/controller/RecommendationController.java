package com.campusskills.controller;

import com.campusskills.entity.User;
import com.campusskills.service.RecommendationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getRecommendations(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(recommendationService.getRecommendations(user.getId()));
    }
}
