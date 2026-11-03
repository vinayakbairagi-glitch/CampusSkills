package com.campusskills.controller;

import com.campusskills.entity.Report;
import com.campusskills.entity.User;
import com.campusskills.service.AdminService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'TPO')")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getAdminStats() {
        return ResponseEntity.ok(adminService.getAdminStats());
    }

    @GetMapping("/students")
    public ResponseEntity<List<User>> getAllStudents(@RequestParam(required = false) String query,
                                                     @RequestParam(required = false) String status) {
        return ResponseEntity.ok(adminService.getAllStudents(query, status));
    }

    @PutMapping("/students/{id}/verify")
    public ResponseEntity<User> verifyStudent(@PathVariable Long id,
                                              @RequestParam(defaultValue = "true") boolean approve) {
        return ResponseEntity.ok(adminService.verifyStudent(id, approve));
    }

    @PutMapping("/students/{id}/role")
    public ResponseEntity<User> changeRole(@PathVariable Long id,
                                           @RequestParam String role,
                                           @AuthenticationPrincipal User caller) {
        return ResponseEntity.ok(adminService.changeRole(id, role, caller.getId()));
    }

    @GetMapping("/reports")
    public ResponseEntity<List<Report>> getReports(@RequestParam(required = false) String status) {
        return ResponseEntity.ok(adminService.getAllReports(status));
    }

    @PutMapping("/reports/{id}")
    public ResponseEntity<Report> actionReport(@PathVariable Long id,
                                               @RequestParam String action) {
        return ResponseEntity.ok(adminService.actionReport(id, action));
    }

    @GetMapping("/official-tests")
    public ResponseEntity<List<Map<String, Object>>> getOfficialResults(@RequestParam(required = false) Long testId) {
        return ResponseEntity.ok(adminService.getOfficialTestResults(testId));
    }

    @GetMapping(value = "/export/students.csv", produces = "text/csv")
    public ResponseEntity<String> exportStudentsCsv() {
        String csv = adminService.exportStudentsCsv();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"campusskills_students.csv\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv);
    }

    @GetMapping(value = "/export/sessions.csv", produces = "text/csv")
    public ResponseEntity<String> exportSessionsCsv() {
        String csv = adminService.exportSessionsCsv();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"campusskills_sessions.csv\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv);
    }

    @GetMapping(value = "/export/tests.csv", produces = "text/csv")
    public ResponseEntity<String> exportTestsCsv() {
        String csv = adminService.exportTestResultsCsv();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"campusskills_test_results.csv\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv);
    }
}
