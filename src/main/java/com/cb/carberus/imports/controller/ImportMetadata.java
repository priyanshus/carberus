package com.cb.carberus.imports.controller;

import com.cb.carberus.imports.service.ImportStudentMetadataService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class ImportMetadata {
    private ImportStudentMetadataService studentMetadataService;

    public ImportMetadata(ImportStudentMetadataService studentMetadataService) {
        this.studentMetadataService = studentMetadataService;
    }

    @PostMapping("/import/students")
    public ResponseEntity<String> uploadStudents(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("No file uploaded");
        }

        // Process the file (Excel parsing, etc.)
        return ResponseEntity.ok("File received successfully");
    }
}
