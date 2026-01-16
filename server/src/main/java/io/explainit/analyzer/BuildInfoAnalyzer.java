package io.explainit.analyzer;

import io.explainit.dto.BuildInfo;
import io.explainit.dto.ProjectMetadata;
import io.explainit.util.FileScanner;
import io.explainit.util.PomParser;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BuildInfoAnalyzer implements IProjectAnalyzer {
    
    private static final Pattern DEPENDENCY_PATTERN = Pattern.compile(
        "<artifactId>\\s*([^<]+)\\s*</artifactId>"
    );
    
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
            
            // Extract dependencies
            List<String> dependencies = extractDependenciesFromPom(pomPath.get());
            buildInfo.setDependencies(dependencies);
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
            
            // Extract dependencies from gradle
            List<String> dependencies = extractDependenciesFromGradle(gradleContent);
            buildInfo.setDependencies(dependencies);
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
    
    private List<String> extractDependenciesFromPom(Path pomPath) throws Exception {
        List<String> dependencies = new ArrayList<>();
        String pomContent = FileScanner.readFileAsString(pomPath);
        
        // Simple regex to find artifact IDs
        Matcher matcher = DEPENDENCY_PATTERN.matcher(pomContent);
        Set<String> uniqueDeps = new LinkedHashSet<>();
        
        while (matcher.find() && uniqueDeps.size() < 50) {
            String artifactId = matcher.group(1).trim();
            if (!artifactId.isEmpty()) {
                uniqueDeps.add(artifactId);
            }
        }
        
        dependencies.addAll(uniqueDeps);
        return dependencies;
    }
    
    private List<String> extractDependenciesFromGradle(String gradleContent) {
        List<String> dependencies = new ArrayList<>();
        Set<String> uniqueDeps = new LinkedHashSet<>();
        
        // Look for dependencies block
        Pattern depPattern = Pattern.compile("(implementation|compile|compileOnly|testImplementation)\\s+['\\\"]([^'\\\"]+)['\\\"]");
        Matcher matcher = depPattern.matcher(gradleContent);
        
        while (matcher.find() && uniqueDeps.size() < 50) {
            String dependency = matcher.group(2).trim();
            // Extract just the library name
            String[] parts = dependency.split(":");
            if (parts.length >= 2) {
                uniqueDeps.add(parts[1]);
            } else {
                uniqueDeps.add(dependency);
            }
        }
        
        dependencies.addAll(uniqueDeps);
        return dependencies;
    }
}
