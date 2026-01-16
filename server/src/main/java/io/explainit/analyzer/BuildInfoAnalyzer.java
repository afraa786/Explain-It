package io.explainit.analyzer;

import io.explainit.dto.BuildInfo;
import io.explainit.dto.ProjectMetadata;
import io.explainit.util.FileScanner;
import io.explainit.util.PomParser;
import java.nio.file.Path;
import java.util.*;

public class BuildInfoAnalyzer implements IProjectAnalyzer {
    
    @Override
    public void analyze(Path projectRoot, ProjectMetadata metadata) throws Exception {
        BuildInfo buildInfo = new BuildInfo();
        
        // Check for Maven
        Optional<Path> pomPath = FileScanner.findFile(projectRoot, "pom.xml");
        if (pomPath.isPresent()) {
            buildInfo.setBuildTool("Maven");
            
            // Extract Spring Boot version
            String sbVersion = PomParser.getSpringBootVersion(pomPath.get());
            if (!sbVersion.isEmpty()) {
                buildInfo.setSpringBootVersion(sbVersion);
            }
            
            // Extract Java version
            Map<String, String> props = PomParser.parsePomProperties(pomPath.get());
            if (props.containsKey("java.version")) {
                buildInfo.setJavaVersion(props.get("java.version"));
            } else if (props.containsKey("maven.compiler.source")) {
                buildInfo.setJavaVersion(props.get("maven.compiler.source"));
            }
        }
        
        // Check for Gradle
        Optional<Path> gradlePath = FileScanner.findFile(projectRoot, "build.gradle");
        if (gradlePath.isPresent()) {
            buildInfo.setBuildTool("Gradle");
            
            String gradleContent = FileScanner.readFileAsString(gradlePath.get());
            
            // Try to extract Java version
            if (gradleContent.contains("sourceCompatibility")) {
                String[] lines = gradleContent.split("\n");
                for (String line : lines) {
                    if (line.contains("sourceCompatibility")) {
                        String[] parts = line.split("=");
                        if (parts.length > 1) {
                            buildInfo.setJavaVersion(parts[1].trim().replaceAll("['\"]", ""));
                        }
                    }
                }
            }
        }
        
        // Set defaults if not found
        if (buildInfo.getBuildTool() == null || buildInfo.getBuildTool().isEmpty()) {
            buildInfo.setBuildTool("Unknown");
        }
        if (buildInfo.getJavaVersion() == null || buildInfo.getJavaVersion().isEmpty()) {
            buildInfo.setJavaVersion("Unknown");
        }
        if (buildInfo.getSpringBootVersion() == null || buildInfo.getSpringBootVersion().isEmpty()) {
            buildInfo.setSpringBootVersion("Unknown");
        }
        
        metadata.setBuildInfo(buildInfo);
    }
}
