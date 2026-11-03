package com.campusskills.controller;

import com.campusskills.dto.ProfileUpdateRequest;
import com.campusskills.dto.UserResponse;
import com.campusskills.entity.User;
import com.campusskills.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final UserService userService;

    public StudentController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyProfile(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userService.getProfile(user.getId()));
    }

    @PutMapping("/me/profile")
    public ResponseEntity<UserResponse> updateMyProfile(@AuthenticationPrincipal User user,
                                                        @RequestBody ProfileUpdateRequest request) {
        return ResponseEntity.ok(userService.updateProfile(user.getId(), request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getStudentById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getProfile(id));
    }
}
