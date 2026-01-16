package io.explainit.analyzer;

import io.explainit.dto.ProjectMetadata;
import io.explainit.util.FileScanner;
import io.explainit.util.PomParser;
import java.nio.file.Path;
import java.util.*;

public class SecurityAnalyzer implements IProjectAnalyzer {
    
    @Override
    public void analyze(Path projectRoot, ProjectMetadata metadata) throws Exception {
        List<String> securityHints = new ArrayList<>();
        
        // Check pom.xml for security dependencies
        Optional<Path> pomPath = FileScanner.findFile(projectRoot, "pom.xml");
        if (pomPath.isPresent()) {
            List<PomParser.Dependency> deps = PomParser.parsePomDependencies(pomPath.get());
            
            for (PomParser.Dependency dep : deps) {
                if (dep.artifactId.contains("security")) {
                    securityHints.add("spring-boot-starter-security detected");
                }
                if (dep.artifactId.contains("oauth2") || dep.artifactId.contains("oauth")) {
                    securityHints.add("OAuth2 / OAuth dependency detected");
                }
                if (dep.artifactId.contains("jwt")) {
                    securityHints.add("JWT (JSON Web Token) dependency detected");
                }
            }
        }
        
        // Check for SecurityConfig classes
        List<Path> javaFiles = FileScanner.findFilesByExtension(projectRoot, "java");
        
        for (Path javaFile : javaFiles) {
            String content = FileScanner.readFileAsString(javaFile);
            String filename = javaFile.getFileName().toString();
            
            if (filename.contains("Security") || filename.contains("Security")) {
                securityHints.add(filename + " - Security configuration class detected");
            }
            
            if (content.contains("WebSecurityConfigurerAdapter") || content.contains("SecurityFilterChain")) {
                securityHints.add("WebSecurityConfigurerAdapter / SecurityFilterChain usage detected");
            }
        }
        
        metadata.setSecurityHints(securityHints);
    }
}
