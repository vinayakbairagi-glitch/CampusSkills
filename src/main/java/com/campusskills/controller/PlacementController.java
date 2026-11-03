package com.campusskills.controller;

import com.campusskills.dto.ChallengeLogRequest;
import com.campusskills.dto.PlacementReviewRequest;
import com.campusskills.entity.DailyChallenge;
import com.campusskills.entity.PlacementProfile;
import com.campusskills.entity.User;
import com.campusskills.service.PlacementService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/placement")
public class PlacementController {

    private final PlacementService placementService;

    public PlacementController(PlacementService placementService) {
        this.placementService = placementService;
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getPlacementMe(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(placementService.getPlacementMe(user.getId()));
    }

    @PostMapping("/challenge")
    public ResponseEntity<DailyChallenge> logChallenge(@AuthenticationPrincipal User user,
                                                       @Valid @RequestBody ChallengeLogRequest request) {
        return ResponseEntity.ok(placementService.logDailyChallenge(user.getId(), request));
    }

    @PostMapping("/review")
    public ResponseEntity<PlacementProfile> reviewPlacement(@AuthenticationPrincipal User user,
                                                            @Valid @RequestBody PlacementReviewRequest request) {
        return ResponseEntity.ok(placementService.reviewStudentPlacement(user.getId(), request));
    }
}
