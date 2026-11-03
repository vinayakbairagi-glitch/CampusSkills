package com.campusskills.controller;

import com.campusskills.dto.AvailabilityRequest;
import com.campusskills.dto.UserSkillRequest;
import com.campusskills.entity.Availability;
import com.campusskills.entity.Skill;
import com.campusskills.entity.User;
import com.campusskills.entity.UserSkill;
import com.campusskills.service.SkillService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class SkillController {

    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @GetMapping("/api/skills")
    public ResponseEntity<List<Skill>> getAllSkills() {
        return ResponseEntity.ok(skillService.getAllSkills());
    }

    @GetMapping("/api/skills/my")
    public ResponseEntity<List<UserSkill>> getMySkills(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(skillService.getUserSkills(user.getId()));
    }

    @PostMapping("/api/skills/user")
    public ResponseEntity<UserSkill> addUserSkill(@AuthenticationPrincipal User user,
                                                  @Valid @RequestBody UserSkillRequest request) {
        return ResponseEntity.ok(skillService.addUserSkill(user.getId(), request));
    }

    @DeleteMapping("/api/skills/user/{id}")
    public ResponseEntity<Map<String, String>> deleteUserSkill(@AuthenticationPrincipal User user,
                                                               @PathVariable Long id) {
        skillService.deleteUserSkill(user.getId(), id);
        return ResponseEntity.ok(Map.of("message", "Skill removed successfully"));
    }

    @GetMapping("/api/skills/availability")
    public ResponseEntity<List<Availability>> getMyAvailability(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(skillService.getUserAvailability(user.getId()));
    }

    @PostMapping("/api/skills/availability")
    public ResponseEntity<List<Availability>> saveMyAvailability(@AuthenticationPrincipal User user,
                                                                 @RequestBody AvailabilityRequest request) {
        return ResponseEntity.ok(skillService.saveAvailability(user.getId(), request));
    }

    @GetMapping("/api/matches")
    public ResponseEntity<List<Map<String, Object>>> getMatches(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(skillService.getSmartMatches(user.getId()));
    }

    @GetMapping("/api/tutors")
    public ResponseEntity<List<Map<String, Object>>> getTutors(@AuthenticationPrincipal User user,
                                                               @RequestParam(required = false) String skill) {
        return ResponseEntity.ok(skillService.browseTutors(user.getId(), skill));
    }
}
