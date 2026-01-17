# ExplainIt Refactor - Testing & Validation Guide

## Unit Testing Strategy

### Test Classes to Create

#### 1. FrameworkDetectorTest
```java
package io.explainit.analyzer;

import io.explainit.dto.DetectionResult;
import io.explainit.dto.FrameworkDetectionResult;
import org.junit.jupiter.api.Test;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FrameworkDetectorTest {
    
    // Test Spring Boot detection
    @Test
    public void testDetectSpringBoot() throws Exception {
        // Mock project with pom.xml containing Spring Boot
        Path projectRoot = Paths.get("src/test/resources/spring-boot-project");
        FrameworkDetectionResult result = FrameworkDetector.detect(projectRoot);
        
        assert result.getPrimaryLanguage().equals("Java");
        assert result.getFrameworks().stream()
            .anyMatch(f -> f.getName().equals("Spring Boot"));
    }
    
    // Test Django detection
    @Test
    public void testDetectDjango() throws Exception {
        Path projectRoot = Paths.get("src/test/resources/django-project");
        FrameworkDetectionResult result = FrameworkDetector.detect(projectRoot);
        
        assert result.getPrimaryLanguage().equals("Python");
        assert result.getFrameworks().stream()
            .anyMatch(f -> f.getName().equals("Django"));
    }
    
    // Test Node.js/Express detection
    @Test
    public void testDetectExpress() throws Exception {
        Path projectRoot = Paths.get("src/test/resources/express-project");
        FrameworkDetectionResult result = FrameworkDetector.detect(projectRoot);
        
        assert result.getPrimaryLanguage().equals("JavaScript") || 
               result.getPrimaryLanguage().equals("TypeScript");
        assert result.getFrameworks().stream()
            .anyMatch(f -> f.getName().equals("Express.js"));
    }
    
    // Test primary language detection accuracy
    @Test
    public void testPrimaryLanguageDetection() throws Exception {
        // Java project should have Java as primary
        Path javaProject = Paths.get("src/test/resources/java-project");
        FrameworkDetectionResult result = FrameworkDetector.detect(javaProject);
        assert result.getPrimaryLanguage().equals("Java");
    }
    
    // Test version detection
    @Test
    public void testJavaVersionDetection() throws Exception {
        Path projectRoot = Paths.get("src/test/resources/java17-project");
        FrameworkDetectionResult result = FrameworkDetector.detect(projectRoot);
        
        assert result.getLanguageVersion().equals("17");
    }
    
    // Test confidence levels
    @Test
    public void testConfidenceScoring() throws Exception {
        Path projectRoot = Paths.get("src/test/resources/spring-boot-project");
        FrameworkDetectionResult result = FrameworkDetector.detect(projectRoot);
        
        // Spring Boot with explicit dependency should be HIGH confidence
        DetectionResult springBoot = result.getFrameworks().stream()
            .filter(f -> f.getName().equals("Spring Boot"))
            .findFirst()
            .orElseThrow();
        
        assert springBoot.getConfidence() == DetectionResult.Confidence.HIGH;
        assert !springBoot.getEvidence().isEmpty();
    }
}
```

#### 2. ProjectSizeCalculatorTest
```java
package io.explainit.util;

import org.junit.jupiter.api.Test;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ProjectSizeCalculatorTest {
    
    @Test
    public void testProjectSizeCalculation() throws Exception {
        Path projectRoot = Paths.get("src/test/resources/sample-project");
        long[] sizeData = ProjectSizeCalculator.calculateProjectSize(projectRoot);
        
        long sizeBytes = sizeData[0];
        long fileCount = sizeData[1];
        
        assert sizeBytes > 0;
        assert fileCount > 0;
    }
    
    @Test
    public void testExcludedDirectoriesIgnored() throws Exception {
        // Project with node_modules should not count node_modules size
        Path projectRoot = Paths.get("src/test/resources/node-project");
        long[] sizeData = ProjectSizeCalculator.calculateProjectSize(projectRoot);
        
        // Size should be significantly less if node_modules is excluded
        // Compare with a version that includes node_modules
        long actual = sizeData[0];
        assert actual < 100000000; // Should be < 100MB without dependencies
    }
    
    @Test
    public void testBytesToMBConversion() {
        double mb = ProjectSizeCalculator.bytesToMB(1048576); // 1MB
        assert Math.abs(mb - 1.0) < 0.01;
        
        double mb2 = ProjectSizeCalculator.bytesToMB(1073741824); // 1GB
        assert Math.abs(mb2 - 1024.0) < 0.01;
    }
}
```

#### 3. DataLayerAnalyzerTest
```java
package io.explainit.analyzer;

import io.explainit.dto.ProjectMetadata;
import org.junit.jupiter.api.Test;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DataLayerAnalyzerTest {
    
    @Test
    public void testPostgresqlDetection() throws Exception {
        Path projectRoot = Paths.get("src/test/resources/spring-postgres-project");
        ProjectMetadata metadata = new ProjectMetadata();
        
        DataLayerAnalyzer analyzer = new DataLayerAnalyzer();
        analyzer.analyze(projectRoot, metadata);
        
        assert metadata.getDataLayerDetections().stream()
            .anyMatch(d -> d.getName().equals("PostgreSQL"));
    }
    
    @Test
    public void testMongoDBDetection() throws Exception {
        Path projectRoot = Paths.get("src/test/resources/mongo-project");
        ProjectMetadata metadata = new ProjectMetadata();
        
        DataLayerAnalyzer analyzer = new DataLayerAnalyzer();
        analyzer.analyze(projectRoot, metadata);
        
        assert metadata.getDataLayerDetections().stream()
            .anyMatch(d -> d.getName().equals("MongoDB"));
    }
    
    @Test
    public void testPrismaDetection() throws Exception {
        Path projectRoot = Paths.get("src/test/resources/prisma-project");
        ProjectMetadata metadata = new ProjectMetadata();
        
        DataLayerAnalyzer analyzer = new DataLayerAnalyzer();
        analyzer.analyze(projectRoot, metadata);
        
        assert metadata.getDataLayerDetections().stream()
            .anyMatch(d -> d.getName().equals("Prisma"));
    }
    
    @Test
    public void testMigrationToolDetection() throws Exception {
        Path projectRoot = Paths.get("src/test/resources/flyway-project");
        ProjectMetadata metadata = new ProjectMetadata();
        
        DataLayerAnalyzer analyzer = new DataLayerAnalyzer();
        analyzer.analyze(projectRoot, metadata);
        
        assert metadata.getDataLayerDetections().stream()
            .anyMatch(d -> d.getType().equals("Migration Tool"));
    }
    
    @Test
    public void testDjangoORMDetection() throws Exception {
        Path projectRoot = Paths.get("src/test/resources/django-orm-project");
        ProjectMetadata metadata = new ProjectMetadata();
        
        DataLayerAnalyzer analyzer = new DataLayerAnalyzer();
        analyzer.analyze(projectRoot, metadata);
        
        assert metadata.getDataLayerDetections().stream()
            .anyMatch(d -> d.getName().equals("Django ORM"));
    }
}
```

#### 4. SecurityAnalyzerTest
```java
package io.explainit.analyzer;

import io.explainit.dto.ProjectMetadata;
import org.junit.jupiter.api.Test;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SecurityAnalyzerTest {
    
    @Test
    public void testSpringSecurityDetection() throws Exception {
        Path projectRoot = Paths.get("src/test/resources/spring-security-project");
        ProjectMetadata metadata = new ProjectMetadata();
        
        SecurityAnalyzer analyzer = new SecurityAnalyzer();
        analyzer.analyze(projectRoot, metadata);
        
        assert metadata.getSecurityDetections().stream()
            .anyMatch(d -> d.getName().equals("Spring Security"));
    }
    
    @Test
    public void testJWTDetection() throws Exception {
        Path projectRoot = Paths.get("src/test/resources/jwt-project");
        ProjectMetadata metadata = new ProjectMetadata();
        
        SecurityAnalyzer analyzer = new SecurityAnalyzer();
        analyzer.analyze(projectRoot, metadata);
        
        assert metadata.getSecurityDetections().stream()
            .anyMatch(d -> d.getName().contains("JWT"));
    }
    
    @Test
    public void testCORSDetection() throws Exception {
        Path projectRoot = Paths.get("src/test/resources/cors-project");
        ProjectMetadata metadata = new ProjectMetadata();
        
        SecurityAnalyzer analyzer = new SecurityAnalyzer();
        analyzer.analyze(projectRoot, metadata);
        
        assert metadata.getSecurityDetections().stream()
            .anyMatch(d -> d.getName().contains("CORS"));
    }
    
    @Test
    public void testPassportAuthDetection() throws Exception {
        Path projectRoot = Paths.get("src/test/resources/passport-project");
        ProjectMetadata metadata = new ProjectMetadata();
        
        SecurityAnalyzer analyzer = new SecurityAnalyzer();
        analyzer.analyze(projectRoot, metadata);
        
        assert metadata.getSecurityDetections().stream()
            .anyMatch(d -> d.getName().equals("Passport.js"));
    }
}
```

## Integration Testing

### Multi-Project Analysis Test
```java
@SpringBootTest
public class ProjectAnalysisServiceIntegrationTest {
    
    @Autowired
    private ProjectAnalysisService analysisService;
    
    @Test
    public void testSpringBootProjectAnalysis() throws Exception {
        Path projectRoot = Paths.get("src/test/resources/spring-boot-complete-project");
        ProjectMetadata result = analysisService.analyzeProject(projectRoot);
        
        // Verify all components work together
        assertNotNull(result.getFrameworkDetection());
        assertNotNull(result.getProjectSize());
        assertFalse(result.getDataLayerDetections().isEmpty());
        assertFalse(result.getSecurityDetections().isEmpty());
        
        // Verify project size is realistic
        assertTrue(result.getProjectSize().getTotalSizeMB() > 0);
        assertTrue(result.getProjectSize().getTotalFileCount() > 0);
        
        // Verify summary is generated
        assertNotNull(result.getSummary());
        assertTrue(result.getSummary().length() > 0);
    }
    
    @Test
    public void testDjangoProjectAnalysis() throws Exception {
        Path projectRoot = Paths.get("src/test/resources/django-complete-project");
        ProjectMetadata result = analysisService.analyzeProject(projectRoot);
        
        assertEquals("Backend (Python)", result.getProjectType());
        assertEquals("Python", result.getFrameworkDetection().getPrimaryLanguage());
    }
    
    @Test
    public void testNodeJSProjectAnalysis() throws Exception {
        Path projectRoot = Paths.get("src/test/resources/express-complete-project");
        ProjectMetadata result = analysisService.analyzeProject(projectRoot);
        
        assertTrue(result.getProjectType().contains("Node.js"));
        assertTrue(result.getFrameworkDetection().getFrameworks().stream()
            .anyMatch(f -> f.getName().equals("Express.js")));
    }
}
```

## Validation Checklist

### Spring Boot Project Test Cases

- [ ] Detects Spring Boot framework
- [ ] Identifies Maven as build system
- [ ] Detects Java version (17)
- [ ] Finds PostgreSQL database driver
- [ ] Identifies Spring Data JPA ORM
- [ ] Finds Flyway migrations
- [ ] Detects Spring Security
- [ ] Finds JWT authentication
- [ ] Calculates project size correctly
- [ ] Generates meaningful summary

### Django Project Test Cases

- [ ] Detects Django framework
- [ ] Identifies Pip as build system
- [ ] Detects Python version
- [ ] Finds PostgreSQL/MySQL driver
- [ ] Identifies Django ORM
- [ ] Finds Django migrations
- [ ] Detects REST framework auth
- [ ] Identifies BCrypt usage
- [ ] Calculates project size correctly
- [ ] Generates meaningful summary

### Node.js/Express Project Test Cases

- [ ] Detects Express.js framework
- [ ] Identifies NPM as build system
- [ ] Detects Node.js version
- [ ] Finds database driver (MySQL/PostgreSQL)
- [ ] Identifies ORM (TypeORM/Sequelize/Prisma)
- [ ] Detects JWT authentication
- [ ] Finds Helmet security headers
- [ ] Identifies CORS middleware
- [ ] Calculates project size correctly
- [ ] Generates meaningful summary

## Real-World Test Projects

Create minimal test projects in `src/test/resources/`:

### spring-boot-project/
```
pom.xml (with Spring Boot, PostgreSQL, Spring Security, JWT)
src/main/java/com/example/Application.java
src/main/resources/application.properties
src/main/resources/db/migration/V1__Initial.sql
```

### django-project/
```
requirements.txt (Django, psycopg2, djangorestframework, pyjwt)
manage.py
myapp/models.py
myapp/views.py
```

### express-project/
```
package.json (express, typeorm, cors, helmet, jsonwebtoken)
src/index.ts
src/entity/User.ts
src/middleware/auth.ts
```

## Performance Benchmarks

Expected performance metrics:

| Project Type | Size | Files | Analysis Time | Notes |
|--------------|------|-------|----------------|-------|
| Small Spring Boot | 5 MB | 150 | 200-300ms | Typical microservice |
| Medium Spring Boot | 42 MB | 1,284 | 500-700ms | Include DB migrations |
| Large Spring Boot | 200+ MB | 5,000+ | 1500-2000ms | Enterprise app |
| Small Django | 3 MB | 80 | 150-250ms | Basic CRUD app |
| Medium Node.js | 8 MB | 287 | 300-400ms | Full-stack app |

## Regression Testing

After any code changes:

1. Run all unit tests
2. Run integration tests with 3+ real projects
3. Verify confidence scores are appropriate
4. Check no false positives/negatives
5. Validate performance degradation < 10%
6. Review generated summaries for accuracy

## End-to-End Test Workflow

```bash
#!/bin/bash

# Run all tests
mvn clean test

# Run specific test class
mvn test -Dtest=FrameworkDetectorTest

# Run with coverage
mvn test jacoco:report

# Build and verify
mvn clean verify

# Test with real projects
curl -X POST http://localhost:8080/analyze \
  -F "project=@src/test/resources/spring-boot-project" \
  | jq .

# Validate response structure
curl -X POST http://localhost:8080/analyze \
  -F "project=@src/test/resources/django-project" \
  | jq '.frameworkDetection | keys'

# Check all detections present
curl -X POST http://localhost:8080/analyze \
  -F "project=@src/test/resources/express-project" \
  | jq '.dataLayerDetections | length'
```

## Documentation Testing

Verify all examples in ARCHITECTURE_REFACTOR.md:
- [ ] Spring Boot example output is valid JSON
- [ ] Django example output is valid JSON
- [ ] Node.js example output is valid JSON
- [ ] All confidence scores are valid
- [ ] All evidence arrays are populated
- [ ] All versions are correctly formatted

## Continuous Integration

Add to GitHub Actions / GitLab CI:

```yaml
name: Test ExplainIt Refactor

on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - uses: actions/setup-java@v2
        with:
          java-version: '17'
      - run: mvn clean test
      - run: mvn jacoco:report
      - uses: codecov/codecov-action@v2
```

## Known Limitations & Future Testing

- [ ] Nested multi-module projects
- [ ] Monorepos with mixed languages
- [ ] Very large projects (>500MB)
- [ ] Edge cases with unusual naming
- [ ] Performance under concurrent requests
- [ ] Memory usage with very large dependency trees
