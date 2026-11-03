package com.campusskills.controller;

import com.campusskills.dto.*;
import com.campusskills.entity.*;
import com.campusskills.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping("/api/projects")
    public ResponseEntity<List<Map<String, Object>>> getAllProjects(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(projectService.getAllProjects(user.getId()));
    }

    @PostMapping("/api/projects")
    public ResponseEntity<Project> createProject(@AuthenticationPrincipal User user,
                                                 @Valid @RequestBody ProjectCreateRequest request) {
        return ResponseEntity.ok(projectService.createProject(user.getId(), request));
    }

    @GetMapping("/api/projects/{id}")
    public ResponseEntity<Map<String, Object>> getProjectDetail(@AuthenticationPrincipal User user,
                                                                @PathVariable Long id) {
        return ResponseEntity.ok(projectService.getProjectDetail(id, user.getId()));
    }

    @PostMapping("/api/projects/{id}/apply")
    public ResponseEntity<ProjectMember> applyToProject(@AuthenticationPrincipal User user,
                                                        @PathVariable Long id,
                                                        @Valid @RequestBody ProjectApplyRequest request) {
        return ResponseEntity.ok(projectService.applyToProject(id, user.getId(), request));
    }

    @PutMapping("/api/projects/{id}/applications/{memberId}")
    public ResponseEntity<ProjectMember> actionApplication(@AuthenticationPrincipal User user,
                                                           @PathVariable Long id,
                                                           @PathVariable Long memberId,
                                                           @RequestParam String action) {
        return ResponseEntity.ok(projectService.actionApplication(id, user.getId(), memberId, action));
    }

    @PostMapping("/api/projects/{id}/tasks")
    public ResponseEntity<ProjectTask> createTask(@AuthenticationPrincipal User user,
                                                  @PathVariable Long id,
                                                  @Valid @RequestBody ProjectTaskCreateRequest request) {
        return ResponseEntity.ok(projectService.createTask(id, user.getId(), request));
    }

    @PutMapping("/api/projects/tasks/{taskId}")
    public ResponseEntity<ProjectTask> toggleTask(@AuthenticationPrincipal User user,
                                                  @PathVariable Long taskId) {
        return ResponseEntity.ok(projectService.toggleTask(taskId, user.getId()));
    }

    @PutMapping("/api/projects/{id}/complete")
    public ResponseEntity<Project> completeProject(@AuthenticationPrincipal User user,
                                                   @PathVariable Long id) {
        return ResponseEntity.ok(projectService.completeProject(id, user.getId()));
    }

    @PostMapping("/api/projects/{id}/rate")
    public ResponseEntity<Rating> rateTeammate(@AuthenticationPrincipal User user,
                                               @PathVariable Long id,
                                               @Valid @RequestBody RatingRequest request) {
        return ResponseEntity.ok(projectService.rateTeammate(id, user.getId(), request));
    }
}
