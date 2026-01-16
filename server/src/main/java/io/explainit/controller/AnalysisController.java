package io.explainit.controller;

import io.explainit.dto.ProjectMetadata;
import io.explainit.service.ProjectAnalysisService;
import io.explainit.util.ZipExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/explain")
public class AnalysisController {
    
    @Autowired
    private ProjectAnalysisService projectAnalysisService;
    
    /**
     * Analyze a project ZIP file
     * 
     * @param file The project ZIP file to analyze
     * @return Comprehensive project analysis
     */
    
    @PostMapping(value = "/analyze", consumes = "multipart/form-data")
    public ResponseEntity<?> analyzeProject(
        @RequestParam("file") MultipartFile file
    ) {
        try {
            // Validate file
            if (file.isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "File is empty");
                return ResponseEntity.badRequest().body(error);
            }
            
            if (!file.getOriginalFilename().endsWith(".zip")) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Only ZIP files are accepted");
                return ResponseEntity.badRequest().body(error);
            }
            
            // Extract ZIP
            Path extractedPath = ZipExtractor.extractZip(file.getInputStream());
            
            try {
                // Analyze project
                ProjectMetadata metadata = projectAnalysisService.analyzeProject(extractedPath);
                
                return ResponseEntity.ok(metadata);
            } finally {
                ZipExtractor.deleteDirectory(extractedPath);
            }
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Analysis failed");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    /**
     * Health check endpoint
     * Useful for verifying backend is running
     * 
     * @return Health status
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "ExplainIt Backend");
        response.put("version", "1.0.0");
        return ResponseEntity.ok(response);
    }
}
