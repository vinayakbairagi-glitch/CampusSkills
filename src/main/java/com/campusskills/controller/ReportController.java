package com.campusskills.controller;

import com.campusskills.dto.ReportCreateRequest;
import com.campusskills.entity.Report;
import com.campusskills.entity.User;
import com.campusskills.service.DoubtService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final DoubtService doubtService;

    public ReportController(DoubtService doubtService) {
        this.doubtService = doubtService;
    }

    @PostMapping
    public ResponseEntity<Report> createReport(@AuthenticationPrincipal User user,
                                               @Valid @RequestBody ReportCreateRequest request) {
        return ResponseEntity.ok(doubtService.createReport(user.getId(), request));
    }
}
