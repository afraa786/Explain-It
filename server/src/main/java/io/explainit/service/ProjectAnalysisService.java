package io.explainit.service;

import io.explainit.analyzer.*;
import io.explainit.dto.ProjectMetadata;
import io.explainit.util.ZipExtractor;
import org.springframework.stereotype.Service;
import java.nio.file.Path;
import java.util.*;

@Service
public class ProjectAnalysisService {
    
    private final List<IProjectAnalyzer> analyzers = Arrays.asList(
        new LanguageAnalyzer(),
        new FrameworkAnalyzer(),
        new ConfigFileAnalyzer(),
        new EntryPointAnalyzer(),
        new BuildInfoAnalyzer(),
        new ApiAnalyzer(),
        new DataLayerAnalyzer(),
        new SecurityAnalyzer(),
        new ProjectStructureAnalyzer()
    );
    
    public ProjectMetadata analyzeProject(Path projectRoot) throws Exception {
        ProjectMetadata metadata = new ProjectMetadata();
        
        // Run all analyzers
        for (IProjectAnalyzer analyzer : analyzers) {
            analyzer.analyze(projectRoot, metadata);
        }
        
        // Determine project type
        determineProjectType(metadata);
        
        // Generate summary
        generateSummary(metadata);
        
        return metadata;
    }
    
    private void determineProjectType(ProjectMetadata metadata) {
        // Check if it's a backend
        if (!metadata.getConfigFiles().isEmpty() || !metadata.getLanguages().isEmpty()) {
            boolean isMaven = metadata.getConfigFiles().stream()
                .anyMatch(cf -> cf.getFile().contains("pom.xml"));
            
            boolean isGradle = metadata.getConfigFiles().stream()
                .anyMatch(cf -> cf.getFile().contains("build.gradle"));
            
            boolean hasJava = metadata.getLanguages().contains("Java");
            
            if ((isMaven || isGradle) && hasJava) {
                metadata.setProjectType("Backend (REST API)");
                return;
            }
            
            boolean isNode = metadata.getConfigFiles().stream()
                .anyMatch(cf -> cf.getFile().contains("package.json"));
            
            boolean hasJs = metadata.getLanguages().contains("JavaScript") || 
                           metadata.getLanguages().contains("TypeScript");
            
            if (isNode && hasJs) {
                metadata.setProjectType("Backend / Full-Stack (Node.js)");
                return;
            }
            
            boolean isPython = metadata.getLanguages().contains("Python");
            if (isPython) {
                metadata.setProjectType("Backend (Python)");
                return;
            }
        }
        
        metadata.setProjectType("Unknown Project Type");
    }
    
    private void generateSummary(ProjectMetadata metadata) {
        StringBuilder summary = new StringBuilder();
        
        if (!metadata.getFrameworks().isEmpty()) {
            summary.append("This backend project uses ");
            summary.append(String.join(", ", metadata.getFrameworks()));
        }
        
        if (metadata.isApiDetected()) {
            summary.append(" with REST APIs");
        }
        
        if (!metadata.getDataLayerHints().isEmpty()) {
            summary.append(", ");
            summary.append(metadata.getDataLayerHints().get(0).toLowerCase());
        }
        
        if (!metadata.getSecurityHints().isEmpty()) {
            summary.append(", and includes security configurations");
        }
        
        if (metadata.getBuildInfo() != null && 
            !"Unknown".equals(metadata.getBuildInfo().getBuildTool())) {
            summary.append(". It is structured using ")
                .append(metadata.getBuildInfo().getBuildTool());
        }
        
        summary.append(" and follows standard framework conventions.");
        
        metadata.setSummary(summary.toString());
    }
}
