package com.campusskills.controller;

import com.campusskills.dto.AuthRequest;
import com.campusskills.dto.AuthResponse;
import com.campusskills.dto.ForgotPasswordRequest;
import com.campusskills.dto.RegisterRequest;
import com.campusskills.dto.ResetPasswordRequest;
import com.campusskills.service.PasswordResetService;
import com.campusskills.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final PasswordResetService passwordResetService;

    public AuthController(UserService userService, PasswordResetService passwordResetService) {
        this.userService = userService;
        this.passwordResetService = passwordResetService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(userService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        passwordResetService.sendResetLink(request);
        return ResponseEntity.ok(Map.of(
                "message", "If the email is registered, a reset link will be sent if email delivery is configured. "
                        + "Check your spam folder or contact an administrator if it does not arrive."));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        passwordResetService.resetPassword(request);
        return ResponseEntity.ok(Map.of("message", "Password reset successfully. You can now sign in."));
    }
}
