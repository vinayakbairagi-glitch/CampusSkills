package com.campusskills.controller;

import com.campusskills.entity.SessionAttendance;
import com.campusskills.entity.User;
import com.campusskills.service.StudyGroupService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/group-sessions")
public class GroupSessionController {

    private final StudyGroupService studyGroupService;

    public GroupSessionController(StudyGroupService studyGroupService) {
        this.studyGroupService = studyGroupService;
    }

    @PostMapping("/{id}/checkin")
    public ResponseEntity<SessionAttendance> checkIn(@AuthenticationPrincipal User user,
                                                     @PathVariable Long id) {
        return ResponseEntity.ok(studyGroupService.checkInSession(id, user.getId()));
    }

    @PutMapping("/{id}/attendance")
    public ResponseEntity<Map<String, String>> markAttendance(@AuthenticationPrincipal User user,
                                                              @PathVariable Long id,
                                                              @RequestBody List<Long> attendedUserIds) {
        studyGroupService.markAttendanceSheet(id, user.getId(), attendedUserIds);
        return ResponseEntity.ok(Map.of("message", "Attendance sheet recorded successfully"));
    }
}
