package io.explainit.analyzer;

import io.explainit.dto.ProjectMetadata;
import io.explainit.util.FileScanner;
import io.explainit.util.PomParser;
import java.nio.file.Path;
import java.util.*;

public class DataLayerAnalyzer implements IProjectAnalyzer {
    
    private static final Map<String, String> DATASOURCE_HINTS = Map.ofEntries(
        Map.entry("spring-boot-starter-data-jpa", "spring-boot-starter-data-jpa (ORM framework)"),
        Map.entry("spring-data-jpa", "Spring Data JPA"),
        Map.entry("hibernate", "Hibernate ORM"),
        Map.entry("postgresql", "PostgreSQL driver detected"),
        Map.entry("mysql", "MySQL driver detected"),
        Map.entry("mongodb", "MongoDB driver detected"),
        Map.entry("h2", "H2 in-memory database"),
        Map.entry("spring-boot-starter-data-mongodb", "Spring Data MongoDB"),
        Map.entry("spring-boot-starter-data-redis", "Spring Data Redis")
    );
    
    @Override
    public void analyze(Path projectRoot, ProjectMetadata metadata) throws Exception {
        List<String> dataLayerHints = new ArrayList<>();
        
        Optional<Path> pomPath = FileScanner.findFile(projectRoot, "pom.xml");
        if (pomPath.isPresent()) {
            List<PomParser.Dependency> deps = PomParser.parsePomDependencies(pomPath.get());
            
            for (PomParser.Dependency dep : deps) {
                for (Map.Entry<String, String> hint : DATASOURCE_HINTS.entrySet()) {
                    if (dep.artifactId.contains(hint.getKey())) {
                        dataLayerHints.add(hint.getValue());
                    }
                }
            }
        }
        
        Optional<Path> appPropsPath = FileScanner.findFile(projectRoot, "application.properties");
        if (appPropsPath.isPresent()) {
            String content = FileScanner.readFileAsString(appPropsPath.get());
            if (content.contains("datasource")) {
                dataLayerHints.add("Datasource configuration detected");
            }
            if (content.contains("jpa")) {
                dataLayerHints.add("JPA configuration detected");
            }
        }
        
        Optional<Path> appYmlPath = FileScanner.findFile(projectRoot, "application.yml");
        if (appYmlPath.isPresent()) {
            String content = FileScanner.readFileAsString(appYmlPath.get());
            if (content.contains("datasource")) {
                dataLayerHints.add("Datasource configuration detected");
            }
        }
        
        metadata.setDataLayerHints(dataLayerHints);
    }
}
