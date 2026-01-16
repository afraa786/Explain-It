package io.explainit.analyzer;

import io.explainit.dto.*;
import io.explainit.util.FileScanner;
import java.nio.file.Path;
import java.util.*;

public class ConfigFileAnalyzer implements IProjectAnalyzer {
    
    private static final Map<String, Map.Entry<String, String>> CONFIG_FILES = Map.ofEntries(
        Map.entry("pom.xml", Map.entry("Maven Build Configuration", "Dependency management and build automation")),
        Map.entry("build.gradle", Map.entry("Gradle Build Configuration", "Dependency management and build automation")),
        Map.entry("application.properties", Map.entry("Spring Boot Configuration", "Application settings and properties")),
        Map.entry("application.yml", Map.entry("Spring Boot Configuration (YAML)", "Application settings in YAML format")),
        Map.entry("application.yaml", Map.entry("Spring Boot Configuration (YAML)", "Application settings in YAML format")),
        Map.entry("settings.gradle", Map.entry("Gradle Settings", "Multi-project Gradle configuration")),
        Map.entry("package.json", Map.entry("Node.js Configuration", "JavaScript/Node.js dependencies")),
        Map.entry("requirements.txt", Map.entry("Python Dependencies", "Python package requirements")),
        Map.entry(".env", Map.entry("Environment Variables", "Configuration from environment variables")),
        Map.entry("docker-compose.yml", Map.entry("Docker Compose", "Multi-container application definition")),
        Map.entry("Dockerfile", Map.entry("Docker Image Configuration", "Container image definition")),
        Map.entry(".gitignore", Map.entry("Git Configuration", "Specifies files to ignore in version control"))
    );
    
    @Override
    public void analyze(Path projectRoot, ProjectMetadata metadata) throws Exception {
        List<ConfigFile> configFiles = new ArrayList<>();
        
        for (Map.Entry<String, Map.Entry<String, String>> entry : CONFIG_FILES.entrySet()) {
            String filename = entry.getKey();
            Optional<Path> found = FileScanner.findFile(projectRoot, filename);
            
            if (found.isPresent()) {
                String relativePath = found.get().toString().replaceAll("\\\\", "/");
                if (relativePath.contains("expo/")) {
                    relativePath = relativePath.substring(relativePath.indexOf("expo/") + 5);
                }
                
                ConfigFile cf = new ConfigFile(
                    relativePath,
                    entry.getValue().getKey(),
                    entry.getValue().getValue()
                );
                configFiles.add(cf);
            }
        }
        
        metadata.setConfigFiles(configFiles);
    }
}
