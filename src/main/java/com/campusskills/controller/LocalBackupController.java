package com.campusskills.controller;

import com.campusskills.service.BackupInfo;
import com.campusskills.service.LocalBackupService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/backups")
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class LocalBackupController {
    private final LocalBackupService backupService;

    public LocalBackupController(LocalBackupService backupService) {
        this.backupService = backupService;
    }

    @GetMapping
    public ResponseEntity<List<BackupInfo>> listBackups() {
        return ResponseEntity.ok(backupService.listBackups());
    }

    @PostMapping
    public ResponseEntity<BackupInfo> createBackup() {
        return ResponseEntity.ok(backupService.createBackup());
    }

    @GetMapping("/{filename}")
    public ResponseEntity<Resource> downloadBackup(@PathVariable String filename) {
        Path backupPath = backupService.getBackupPath(filename);
        Resource resource = new FileSystemResource(backupPath);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/zip"))
                .contentLength(backupPath.toFile().length())
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(backupPath.getFileName().toString()).build().toString())
                .body(resource);
    }
}
