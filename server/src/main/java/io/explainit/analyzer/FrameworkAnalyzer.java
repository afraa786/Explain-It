package io.explainit.analyzer;

import io.explainit.dto.ProjectMetadata;
import io.explainit.util.FileScanner;
import io.explainit.util.PomParser;
import java.nio.file.Path;
import java.util.*;

public class FrameworkAnalyzer implements IProjectAnalyzer {
    
    @Override
    public void analyze(Path projectRoot, ProjectMetadata metadata) throws Exception {
        Set<String> frameworks = new LinkedHashSet<>();
        
        // Check for Maven-based frameworks
        Optional<Path> pomPath = FileScanner.findFile(projectRoot, "pom.xml");
        if (pomPath.isPresent()) {
            List<PomParser.Dependency> deps = PomParser.parsePomDependencies(pomPath.get());
            
            for (PomParser.Dependency dep : deps) {
                if (dep.groupId.startsWith("org.springframework")) {
                    if (dep.artifactId.contains("web") || dep.artifactId.contains("webmvc")) {
                        frameworks.add("Spring MVC");
                    }
                    if (dep.artifactId.contains("data-jpa")) {
                        frameworks.add("Spring Data JPA");
                    }
                    if (dep.artifactId.contains("security")) {
                        frameworks.add("Spring Security");
                    }
                    if (dep.artifactId.contains("boot")) {
                        frameworks.add("Spring Boot");
                    }
                }
                if (dep.groupId.contains("hibernate")) {
                    frameworks.add("Hibernate");
                }
            }
        }
        
        // Check for Gradle-based frameworks
        Optional<Path> gradlePath = FileScanner.findFile(projectRoot, "build.gradle");
        if (gradlePath.isPresent()) {
            String gradleContent = FileScanner.readFileAsString(gradlePath.get());
            if (gradleContent.contains("spring-boot")) {
                frameworks.add("Spring Boot");
            }
            if (gradleContent.contains("spring-web")) {
                frameworks.add("Spring Web");
            }
        }
        
        // Check for Python frameworks
        Optional<Path> requirementsPath = FileScanner.findFile(projectRoot, "requirements.txt");
        if (requirementsPath.isPresent()) {
            String reqs = FileScanner.readFileAsString(requirementsPath.get());
            if (reqs.contains("django")) frameworks.add("Django");
            if (reqs.contains("flask")) frameworks.add("Flask");
            if (reqs.contains("fastapi")) frameworks.add("FastAPI");
        }
        
        // Check for Node.js frameworks
        Optional<Path> packageJsonPath = FileScanner.findFile(projectRoot, "package.json");
        if (packageJsonPath.isPresent()) {
            String pkgJson = FileScanner.readFileAsString(packageJsonPath.get());
            if (pkgJson.contains("express")) frameworks.add("Express.js");
            if (pkgJson.contains("nestjs")) frameworks.add("NestJS");
            if (pkgJson.contains("react")) frameworks.add("React");
            if (pkgJson.contains("next")) frameworks.add("Next.js");
        }
        
        if (frameworks.isEmpty()) {
            frameworks.add("Unknown");
        }
        
        metadata.setFrameworks(new ArrayList<>(frameworks));
    }
}
