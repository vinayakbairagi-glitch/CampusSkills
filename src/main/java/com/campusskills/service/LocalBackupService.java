package com.campusskills.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.context.event.EventListener;
import org.springframework.context.annotation.Profile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Service
@Profile("!mysql")
public class LocalBackupService {
    private static final Logger log = LoggerFactory.getLogger(LocalBackupService.class);
    private static final String FILE_PREFIX = "campusskills-";
    private static final Pattern BACKUP_FILENAME = Pattern.compile("campusskills-\\d{8}-\\d{6}-\\d{9}\\.zip");
    private static final DateTimeFormatter FILE_TIMESTAMP = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss-SSSSSSSSS");

    private final JdbcTemplate jdbcTemplate;
    private final Path backupDirectory;
    private final int retentionCount;

    public LocalBackupService(JdbcTemplate jdbcTemplate,
                              @Value("${campusskills.backup.directory:./backups}") String backupDirectory,
                              @Value("${campusskills.backup.retention-count:7}") int retentionCount) {
        this.jdbcTemplate = jdbcTemplate;
        this.backupDirectory = Path.of(backupDirectory).toAbsolutePath().normalize();
        this.retentionCount = Math.max(1, retentionCount);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void createStartupBackup() {
        try {
            BackupInfo backup = createBackup();
            log.info("Created startup database backup: {}", backup.filename());
        } catch (RuntimeException e) {
            log.error("Could not create startup database backup in {}", backupDirectory, e);
        }
    }

    @Scheduled(cron = "${campusskills.backup.schedule:0 0 2 * * *}")
    public void createScheduledBackup() {
        try {
            BackupInfo backup = createBackup();
            log.info("Created scheduled database backup: {}", backup.filename());
        } catch (RuntimeException e) {
            log.error("Could not create scheduled database backup in {}", backupDirectory, e);
        }
    }

    public synchronized BackupInfo createBackup() {
        try {
            Files.createDirectories(backupDirectory);
            String timestamp = LocalDateTime.now().format(FILE_TIMESTAMP);
            Path backupFile = backupDirectory.resolve(FILE_PREFIX + timestamp + ".zip").normalize();
            if (!backupFile.getParent().equals(backupDirectory)) {
                throw new IllegalStateException("Backup path must remain inside the configured backup directory.");
            }

            Path temporaryFile = backupDirectory.resolve(backupFile.getFileName() + ".tmp").normalize();
            String sqlPath = temporaryFile.toString().replace("'", "''");
            try {
                jdbcTemplate.execute("BACKUP TO '" + sqlPath + "'");
                try {
                    Files.move(temporaryFile, backupFile, StandardCopyOption.ATOMIC_MOVE);
                } catch (AtomicMoveNotSupportedException e) {
                    Files.move(temporaryFile, backupFile);
                }
            } catch (RuntimeException | IOException e) {
                Files.deleteIfExists(temporaryFile);
                throw e;
            }
            pruneBackups();
            return toBackupInfo(backupFile);
        } catch (IOException e) {
            throw new IllegalStateException("Could not write the local database backup.", e);
        }
    }

    public List<BackupInfo> listBackups() {
        try (Stream<Path> files = Files.list(backupDirectory)) {
            return files
                    .filter(Files::isRegularFile)
                    .filter(path -> BACKUP_FILENAME.matcher(path.getFileName().toString()).matches())
                    .sorted(Comparator.comparing(this::lastModified).reversed())
                    .map(this::toBackupInfo)
                    .toList();
        } catch (IOException e) {
            if (Files.notExists(backupDirectory)) {
                return List.of();
            }
            throw new IllegalStateException("Could not list local database backups.", e);
        }
    }

    public Path getBackupPath(String filename) {
        if (filename == null || !BACKUP_FILENAME.matcher(filename).matches()) {
            throw new IllegalArgumentException("Invalid backup filename.");
        }
        Path backupFile = backupDirectory.resolve(filename).normalize();
        if (!backupFile.getParent().equals(backupDirectory) || !Files.isRegularFile(backupFile)) {
            throw new IllegalArgumentException("Backup file was not found.");
        }
        return backupFile;
    }

    private void pruneBackups() throws IOException {
        List<Path> backups = listBackups().stream()
                .map(info -> backupDirectory.resolve(info.filename()))
                .toList();
        for (Path expired : backups.stream().skip(retentionCount).toList()) {
            Files.deleteIfExists(expired);
            log.info("Removed expired local database backup: {}", expired.getFileName());
        }
    }

    private BackupInfo toBackupInfo(Path path) {
        try {
            return new BackupInfo(
                    path.getFileName().toString(),
                    Files.size(path),
                    Files.getLastModifiedTime(path).toInstant());
        } catch (IOException e) {
            throw new IllegalStateException("Could not read local database backup metadata.", e);
        }
    }

    private FileTime lastModified(Path path) {
        try {
            return Files.getLastModifiedTime(path);
        } catch (IOException e) {
            throw new IllegalStateException("Could not read backup timestamp.", e);
        }
    }
}
