package com.campusskills.service;

import com.campusskills.config.CollegeProperties;
import com.campusskills.exception.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path uploadLocation;

    private static final List<String> SAFE_EXTENSIONS = Arrays.asList(
            "pdf", "png", "jpg", "jpeg", "gif", "webp",
            "doc", "docx", "txt", "zip", "rar", "csv", "xlsx",
            "java", "py", "cpp", "c", "js", "ts", "json", "xml", "sql", "md"
    );

    public FileStorageService(CollegeProperties collegeProperties) {
        this.uploadLocation = Paths.get(collegeProperties.getUploadDir()).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.uploadLocation);
        } catch (Exception ex) {
            throw new ApiException("Could not create the directory where uploaded files will be stored.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public String storeFile(MultipartFile file) {
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename() != null ? file.getOriginalFilename() : "file");

        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex == -1) {
            throw new ApiException("File must have an extension.", HttpStatus.BAD_REQUEST);
        }

        String extension = originalFilename.substring(dotIndex + 1).toLowerCase();

        // Reject dangerous files (especially HTML, exe, bat, sh)
        if (extension.equals("html") || extension.equals("htm") || !SAFE_EXTENSIONS.contains(extension)) {
            throw new ApiException("File type ." + extension + " is not permitted for security reasons.", HttpStatus.BAD_REQUEST);
        }

        String safeFileName = UUID.randomUUID().toString() + "_" + originalFilename.replaceAll("[^a-zA-Z0-9._-]", "_");

        try {
            if (safeFileName.contains("..")) {
                throw new ApiException("Filename contains invalid path sequence.", HttpStatus.BAD_REQUEST);
            }

            Path targetLocation = this.uploadLocation.resolve(safeFileName);
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);
            }

            return "/uploads/" + safeFileName;
        } catch (IOException ex) {
            throw new ApiException("Could not store file " + safeFileName + ". Please try again!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
