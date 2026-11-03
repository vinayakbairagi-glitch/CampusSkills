package com.campusskills.service;

import com.campusskills.dto.ForgotPasswordRequest;
import com.campusskills.dto.ResetPasswordRequest;
import com.campusskills.entity.PasswordResetToken;
import com.campusskills.entity.User;
import com.campusskills.exception.ApiException;
import com.campusskills.repository.PasswordResetTokenRepository;
import com.campusskills.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Optional;

@Service
public class PasswordResetService {
    private static final Logger log = LoggerFactory.getLogger(PasswordResetService.class);
    private static final int TOKEN_BYTES = 32;

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;
    private final SecureRandom secureRandom = new SecureRandom();

    @Value("${campusskills.password-reset.frontend-base-url:http://localhost:8080}")
    private String frontendBaseUrl;

    @Value("${spring.mail.username:}")
    private String mailUsername;

    public PasswordResetService(UserRepository userRepository,
                                PasswordResetTokenRepository tokenRepository,
                                PasswordEncoder passwordEncoder,
                                JavaMailSender mailSender) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
    }

    @Transactional
    public void sendResetLink(ForgotPasswordRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) return;

        User user = userOpt.get();
        byte[] randomBytes = new byte[TOKEN_BYTES];
        secureRandom.nextBytes(randomBytes);
        String rawToken = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
        LocalDateTime now = LocalDateTime.now();

        tokenRepository.deleteByUserIdAndUsedAtIsNull(user.getId());
        tokenRepository.save(new PasswordResetToken(
                user.getId(), hashToken(rawToken), now.plusMinutes(30), now));

        String resetUrl = UriComponentsBuilder.fromUriString(frontendBaseUrl)
                .path("/reset-password")
                .queryParam("token", rawToken)
                .build()
                .toUriString();

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, StandardCharsets.UTF_8.name());
            helper.setTo(user.getEmail());
            if (!mailUsername.isBlank()) helper.setFrom(mailUsername);
            helper.setSubject("Reset your CampusSkills password");
            helper.setText(
                    "<p>Hello " + escapeHtml(user.getFullName()) + ",</p>"
                            + "<p>Use the link below to choose a new CampusSkills password. "
                            + "This link expires in 30 minutes and can only be used once.</p>"
                            + "<p><a href=\"" + resetUrl + "\">Reset password</a></p>"
                            + "<p>If you did not request this, you can ignore this email.</p>",
                    true);
            mailSender.send(message);
        } catch (MailException | MessagingException exception) {
            tokenRepository.deleteByUserIdAndUsedAtIsNull(user.getId());
            log.error("Could not send password reset email for user id {}", user.getId(), exception);
        }
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        String tokenHash = hashToken(request.getToken());
        PasswordResetToken resetToken = tokenRepository
                .findByTokenHashAndUsedAtIsNullAndExpiresAtAfter(tokenHash, LocalDateTime.now())
                .orElseThrow(() -> new ApiException(
                        "This password reset link is invalid or has expired. Request a new one.",
                        HttpStatus.BAD_REQUEST));

        User user = userRepository.findById(resetToken.getUserId())
                .orElseThrow(() -> new ApiException("This password reset link is invalid or has expired.",
                        HttpStatus.BAD_REQUEST));
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        resetToken.setUsedAt(LocalDateTime.now());
        tokenRepository.save(resetToken);
        tokenRepository.deleteByUserIdAndUsedAtIsNull(user.getId());
    }

    private String hashToken(String token) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is not available", exception);
        }
    }

    private String escapeHtml(String value) {
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
