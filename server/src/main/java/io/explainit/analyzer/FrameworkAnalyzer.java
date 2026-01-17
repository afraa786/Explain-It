package io.explainit.analyzer;

import io.explainit.dto.AnalysisResult;
import io.explainit.dto.FrameworkAnalysisResult;
import io.explainit.dto.FrameworkDetectionResult;
import io.explainit.util.FileScanner;
import io.explainit.util.PomParser;
import java.nio.file.Path;
import java.util.*;

/**
 * Enriches framework detection with version, confidence, and ecosystem type.
 * Consumes output from FrameworkDetector and adds additional context.
 */
public class FrameworkAnalyzer implements IProjectAnalyzer {
    
    @Override
    public AnalysisResult analyze(Path projectRoot) throws Exception {
        FrameworkAnalysisResult result = new FrameworkAnalysisResult();
        
        // Run framework detection
        FrameworkDetectionResult detection = FrameworkDetector.detect(projectRoot);
        
        // Set primary language and framework
        result.setPrimaryLanguage(detection.getPrimaryLanguage());
        
        if (!detection.getFrameworks().isEmpty()) {
            result.setPrimaryFramework(detection.getFrameworks().get(0).getName());
            result.setConfidence(detection.getFrameworks().get(0).getConfidence().toString());
        }
        
        // Detect version based on primary language
        result.setPrimaryFrameworkVersion(detectFrameworkVersion(projectRoot, detection.getPrimaryLanguage()));
        
        // Determine ecosystem type
        result.setEcosystemType(determineEcosystem(detection.getPrimaryLanguage()));
        
        // Set all detections for completeness
        result.setAllDetections(Arrays.asList(detection));
        
        result.setSuccess(true);
        return result;
    }
    
    private String detectFrameworkVersion(Path projectRoot, String language) {
        try {
            switch(language) {
                case "Java":
                    Optional<Path> pomPath = FileScanner.findFile(projectRoot, "pom.xml");
                    if (pomPath.isPresent()) {
                        return PomParser.getSpringBootVersion(pomPath.get());
                    }
                    return "Unknown";
                default:
                    return "Unknown";
            }
        } catch (Exception e) {
            return "Unknown";
        }
    }
    
    private String determineEcosystem(String language) {
        switch(language) {
            case "Java": return "JVM";
            case "Python": return "Python";
            case "JavaScript":
            case "TypeScript": return "Node.js";
            default: return "Unknown";
        }
    }
}
