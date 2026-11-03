package com.campusskills.controller;

import com.campusskills.dto.*;
import com.campusskills.entity.*;
import com.campusskills.service.StudyGroupService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/groups")
public class StudyGroupController {

    private final StudyGroupService studyGroupService;

    public StudyGroupController(StudyGroupService studyGroupService) {
        this.studyGroupService = studyGroupService;
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> searchGroups(@AuthenticationPrincipal User user,
                                                                  @RequestParam(required = false) String query,
                                                                  @RequestParam(required = false) String subject,
                                                                  @RequestParam(required = false) String category,
                                                                  @RequestParam(required = false) String branch,
                                                                  @RequestParam(required = false) Integer studyYear,
                                                                  @RequestParam(required = false) String mode,
                                                                  @RequestParam(required = false) Boolean myGroups) {
        return ResponseEntity.ok(studyGroupService.searchGroups(user.getId(), query, subject, category, branch, studyYear, mode, myGroups));
    }

    @PostMapping
    public ResponseEntity<StudyGroup> createGroup(@AuthenticationPrincipal User user,
                                                  @Valid @RequestBody StudyGroupCreateRequest request) {
        return ResponseEntity.ok(studyGroupService.createGroup(user.getId(), request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getGroupDetail(@AuthenticationPrincipal User user,
                                                              @PathVariable Long id) {
        return ResponseEntity.ok(studyGroupService.getGroupDetail(id, user.getId()));
    }

    @PostMapping("/{id}/join")
    public ResponseEntity<GroupMember> joinGroup(@AuthenticationPrincipal User user,
                                                 @PathVariable Long id) {
        return ResponseEntity.ok(studyGroupService.joinGroup(id, user.getId()));
    }

    @PostMapping("/{id}/sessions")
    public ResponseEntity<GroupSession> scheduleSession(@AuthenticationPrincipal User user,
                                                        @PathVariable Long id,
                                                        @Valid @RequestBody GroupSessionCreateRequest request) {
        return ResponseEntity.ok(studyGroupService.scheduleGroupSession(id, user.getId(), request));
    }

    @PostMapping("/{id}/resources")
    public ResponseEntity<GroupResource> addResource(@AuthenticationPrincipal User user,
                                                     @PathVariable Long id,
                                                     @Valid @RequestBody GroupResourceCreateRequest request) {
        return ResponseEntity.ok(studyGroupService.addResource(id, user.getId(), request));
    }

    @GetMapping("/{id}/messages")
    public ResponseEntity<List<GroupMessage>> getMessages(@PathVariable Long id) {
        return ResponseEntity.ok(studyGroupService.getGroupMessages(id));
    }

    @PostMapping("/{id}/messages")
    public ResponseEntity<GroupMessage> postMessage(@AuthenticationPrincipal User user,
                                                    @PathVariable Long id,
                                                    @Valid @RequestBody GroupMessageRequest request) {
        return ResponseEntity.ok(studyGroupService.postGroupMessage(id, user.getId(), request.getContent()));
    }

    @PutMapping("/{id}/members/{targetUserId}")
    public ResponseEntity<Map<String, String>> manageMember(@AuthenticationPrincipal User user,
                                                            @PathVariable Long id,
                                                            @PathVariable Long targetUserId,
                                                            @RequestParam String action) {
        studyGroupService.updateMemberRoleOrStatus(id, user.getId(), targetUserId, action);
        return ResponseEntity.ok(Map.of("message", "Member updated successfully"));
    }
}
