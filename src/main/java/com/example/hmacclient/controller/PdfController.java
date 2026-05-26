package com.example.hmacclient.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/pdf")
public class PdfController {

    private static final String UPLOAD_DIR = "uploads/";

    @PostMapping("/upload")
    public ResponseEntity<String> uploadPdf(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please select a file to upload.");
        }

        String originalFilename = file.getOriginalFilename();
        if (!"application/pdf".equals(file.getContentType()) ||
            (originalFilename == null || !originalFilename.toLowerCase().endsWith(".pdf"))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Only PDF files are allowed.");
        }

        try {
            // Create the directory if it doesn't exist
            File dir = new File(UPLOAD_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // Sanitize filename to prevent path traversal
            String sanitizedFilename = Paths.get(originalFilename).getFileName().toString();

            // Ensure the normalized path still resides within the target directory
            Path uploadPath = Paths.get(UPLOAD_DIR).toAbsolutePath().normalize();
            Path targetPath = uploadPath.resolve(sanitizedFilename).normalize();

            if (!targetPath.startsWith(uploadPath)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid file path.");
            }

            // Save the file locally
            byte[] bytes = file.getBytes();
            Files.write(targetPath, bytes);

            return ResponseEntity.ok("Successfully uploaded PDF file: " + sanitizedFilename);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload the file: " + e.getMessage());
        }
    }
}
