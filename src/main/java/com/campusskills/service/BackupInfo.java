package com.campusskills.service;

import java.time.Instant;

public record BackupInfo(String filename, long sizeBytes, Instant createdAt) {}
