package io.explainit.analyzer;

import io.explainit.dto.AnalysisResult;
import io.explainit.dto.ConfigAnalysisResult;
import io.explainit.dto.ConfigFile;
import io.explainit.util.FileScanner;
import java.nio.file.Path;
import java.util.*;

/**
 * Classifies and scores configuration files by type and complexity.
 */
public class ConfigFileAnalyzer implements IProjectAnalyzer {
    
    private static final Set<String> RUNTIME_CONFIGS = Set.of(
        "application.properties", "application.yml", "application.yaml",
        ".env", ".env.local", "config.json", "settings.json"
    );
    
    private static final Set<String> SECURITY_CONFIGS = Set.of(
        "security.properties", "keystore.jks", ".env", "secrets.yml",
        "auth.config", "jwt.properties"
    );
    
    private static final Set<String> BUILD_CONFIGS = Set.of(
        "pom.xml", "build.gradle", "package.json", "build.properties",
        "gradle.properties", "settings.gradle"
    );
    
    private static final Set<String> INFRA_CONFIGS = Set.of(
        "docker-compose.yml", "Dockerfile", "kubernetes.yml", "docker-compose.yaml",
        "k8s.yml", "terraform.tf", ".github"
    );
    
    @Override
    public AnalysisResult analyze(Path projectRoot) throws Exception {
        ConfigAnalysisResult result = new ConfigAnalysisResult();
        
        List<ConfigFile> runtimeConfigs = new ArrayList<>();
        List<ConfigFile> securityConfigs = new ArrayList<>();
        List<ConfigFile> buildConfigs = new ArrayList<>();
        List<ConfigFile> infraConfigs = new ArrayList<>();
        
        // Scan for configuration files
        for (String filename : RUNTIME_CONFIGS) {
            Optional<Path> found = FileScanner.findFile(projectRoot, filename);
            if (found.isPresent()) {
                runtimeConfigs.add(new ConfigFile(filename, found.get().toString(), "Runtime"));
            }
        }
        
        for (String filename : SECURITY_CONFIGS) {
            Optional<Path> found = FileScanner.findFile(projectRoot, filename);
            if (found.isPresent() && !runtimeConfigs.stream().anyMatch(c -> c.getFilename().equals(filename))) {
                securityConfigs.add(new ConfigFile(filename, found.get().toString(), "Security"));
            }
        }
        
        for (String filename : BUILD_CONFIGS) {
            Optional<Path> found = FileScanner.findFile(projectRoot, filename);
            if (found.isPresent()) {
                buildConfigs.add(new ConfigFile(filename, found.get().toString(), "Build"));
            }
        }
        
        for (String filename : INFRA_CONFIGS) {
            Optional<Path> found = FileScanner.findFile(projectRoot, filename);
            if (found.isPresent()) {
                infraConfigs.add(new ConfigFile(filename, found.get().toString(), "Infrastructure"));
            }
        }
        
        result.setRuntimeConfigs(runtimeConfigs);
        result.setSecurityConfigs(securityConfigs);
        result.setBuildConfigs(buildConfigs);
        result.setInfrastructureConfigs(infraConfigs);
        
        int total = runtimeConfigs.size() + securityConfigs.size() + buildConfigs.size() + infraConfigs.size();
        result.setTotalConfigFiles(total);
        
        // Score complexity
        String complexity = scoreComplexity(total, infraConfigs.size());
        result.setConfigComplexity(complexity);
        
        result.setSuccess(true);
        return result;
    }
    
    private String scoreComplexity(int totalConfigs, int infraConfigs) {
        if (totalConfigs >= 8 || infraConfigs > 2) {
            return "HIGH";
        } else if (totalConfigs >= 4) {
            return "MEDIUM";
        } else {
            return "LOW";
        }
    }
}
