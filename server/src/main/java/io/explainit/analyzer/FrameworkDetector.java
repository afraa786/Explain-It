package io.explainit.analyzer;

import io.explainit.dto.DetectionResult;
import io.explainit.dto.FrameworkDetectionResult;
import io.explainit.util.FileScanner;
import io.explainit.util.PomParser;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Detects frameworks, build systems, and primary language across multiple technology stacks.
 * Works with Java, Python, JavaScript/TypeScript, and more.
 * Returns confidence-scored results for production-grade analysis.
 */
public class FrameworkDetector {
    
    // Language detection
    private static final Map<String, Integer> JAVA_FILE_WEIGHTS = Map.of(
        "java", 3,
        "gradle", 2,
        "maven", 2
    );
    
    private static final Map<String, Integer> PYTHON_FILE_WEIGHTS = Map.of(
        "py", 3,
        "requirements.txt", 2,
        "pyproject.toml", 2,
        "setup.py", 2
    );
    
    private static final Map<String, Integer> JAVASCRIPT_FILE_WEIGHTS = Map.of(
        "js", 2,
        "jsx", 2,
        "package.json", 3,
        "npm", 2
    );
    
    private static final Map<String, Integer> TYPESCRIPT_FILE_WEIGHTS = Map.of(
        "ts", 3,
        "tsx", 3,
        "tsconfig.json", 3
    );
    
    // Framework signatures
    private static final Map<String, FrameworkSignature> JAVA_FRAMEWORKS = Map.ofEntries(
        Map.entry("spring-boot", new FrameworkSignature(
            "Spring Boot",
            Arrays.asList("spring-boot", "spring-boot-starter"),
            Arrays.asList("spring-boot-maven-plugin", "org.springframework.boot")
        )),
        Map.entry("quarkus", new FrameworkSignature(
            "Quarkus",
            Arrays.asList("quarkus-core", "io.quarkus"),
            Arrays.asList("quarkus-maven-plugin")
        )),
        Map.entry("micronaut", new FrameworkSignature(
            "Micronaut",
            Arrays.asList("micronaut-core", "io.micronaut"),
            Arrays.asList("micronaut.version")
        )),
        Map.entry("hibernate", new FrameworkSignature(
            "Hibernate",
            Arrays.asList("hibernate-core", "hibernate-jpa"),
            Arrays.asList()
        )),
        Map.entry("jpa", new FrameworkSignature(
            "JPA",
            Arrays.asList("spring-data-jpa", "javax.persistence", "jakarta.persistence"),
            Arrays.asList()
        ))
    );
    
    private static final Map<String, FrameworkSignature> PYTHON_FRAMEWORKS = Map.ofEntries(
        Map.entry("django", new FrameworkSignature(
            "Django",
            Arrays.asList("django"),
            Arrays.asList("manage.py")
        )),
        Map.entry("flask", new FrameworkSignature(
            "Flask",
            Arrays.asList("flask"),
            Arrays.asList("app.py")
        )),
        Map.entry("fastapi", new FrameworkSignature(
            "FastAPI",
            Arrays.asList("fastapi", "pydantic"),
            Arrays.asList()
        )),
        Map.entry("sqlalchemy", new FrameworkSignature(
            "SQLAlchemy",
            Arrays.asList("sqlalchemy"),
            Arrays.asList()
        )),
        Map.entry("django-orm", new FrameworkSignature(
            "Django ORM",
            Arrays.asList("django"),
            Arrays.asList("models.py")
        ))
    );
    
    private static final Map<String, FrameworkSignature> NODE_FRAMEWORKS = Map.ofEntries(
        Map.entry("express", new FrameworkSignature(
            "Express.js",
            Arrays.asList("express"),
            Arrays.asList()
        )),
        Map.entry("nestjs", new FrameworkSignature(
            "NestJS",
            Arrays.asList("@nestjs/core"),
            Arrays.asList()
        )),
        Map.entry("react", new FrameworkSignature(
            "React",
            Arrays.asList("react"),
            Arrays.asList()
        )),
        Map.entry("nextjs", new FrameworkSignature(
            "Next.js",
            Arrays.asList("next"),
            Arrays.asList()
        )),
        Map.entry("vue", new FrameworkSignature(
            "Vue.js",
            Arrays.asList("vue"),
            Arrays.asList()
        )),
        Map.entry("fastify", new FrameworkSignature(
            "Fastify",
            Arrays.asList("fastify"),
            Arrays.asList()
        )),
        Map.entry("prisma", new FrameworkSignature(
            "Prisma",
            Arrays.asList("@prisma/client"),
            Arrays.asList("prisma.schema")
        )),
        Map.entry("typeorm", new FrameworkSignature(
            "TypeORM",
            Arrays.asList("typeorm"),
            Arrays.asList()
        ))
    );
    
    /**
     * Detects frameworks and build systems in a project.
     * 
     * @param projectRoot The root path of the project
     * @return A FrameworkDetectionResult with confidence scores
     * @throws IOException If an I/O error occurs
     */
    public static FrameworkDetectionResult detect(Path projectRoot) throws IOException {
        // Detect languages with weights
        String primaryLanguage = detectPrimaryLanguage(projectRoot);
        String languageVersion = detectLanguageVersion(projectRoot, primaryLanguage);
        List<String> allLanguages = detectAllLanguages(projectRoot);
        
        // Detect frameworks based on primary language
        List<DetectionResult> frameworks = detectFrameworksByLanguage(projectRoot, primaryLanguage);
        
        // Detect build system
        DetectionResult buildSystem = detectBuildSystem(projectRoot, primaryLanguage);
        
        return new FrameworkDetectionResult(primaryLanguage, languageVersion, frameworks, buildSystem, allLanguages);
    }
    
    private static String detectPrimaryLanguage(Path projectRoot) throws IOException {
        Map<String, Integer> scores = new HashMap<>();
        
        scores.put("Java", scoreLanguage(projectRoot, JAVA_FILE_WEIGHTS));
        scores.put("Python", scoreLanguage(projectRoot, PYTHON_FILE_WEIGHTS));
        scores.put("TypeScript", scoreLanguage(projectRoot, TYPESCRIPT_FILE_WEIGHTS));
        scores.put("JavaScript", scoreLanguage(projectRoot, JAVASCRIPT_FILE_WEIGHTS));
        
        return scores.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("Unknown");
    }
    
    private static int scoreLanguage(Path projectRoot, Map<String, Integer> weights) throws IOException {
        int score = 0;
        Set<String> extensions = FileScanner.getAllFileExtensions(projectRoot);
        
        for (Map.Entry<String, Integer> entry : weights.entrySet()) {
            if (extensions.contains(entry.getKey())) {
                score += entry.getValue();
            }
        }
        
        // Check for specific config files
        for (String ext : extensions) {
            if (weights.containsKey(ext)) {
                score += weights.get(ext);
            }
        }
        
        return score;
    }
    
    private static String detectLanguageVersion(Path projectRoot, String primaryLanguage) throws IOException {
        switch (primaryLanguage) {
            case "Java":
                return detectJavaVersion(projectRoot);
            case "Python":
                return detectPythonVersion(projectRoot);
            case "JavaScript":
            case "TypeScript":
                return detectNodeVersion(projectRoot);
            default:
                return "Unknown";
        }
    }
    
    private static String detectJavaVersion(Path projectRoot) throws IOException {
        // Check pom.xml
        Optional<Path> pomPath = FileScanner.findFile(projectRoot, "pom.xml");
        if (pomPath.isPresent()) {
            String pomContent = FileScanner.readFileAsString(pomPath.get());
            Pattern versionPattern = Pattern.compile("<java\\.version>([^<]+)</java\\.version>");
            var matcher = versionPattern.matcher(pomContent);
            if (matcher.find()) {
                return matcher.group(1);
            }
        }
        
        // Check gradle
        Optional<Path> gradlePath = FileScanner.findFile(projectRoot, "build.gradle");
        if (gradlePath.isPresent()) {
            String gradleContent = FileScanner.readFileAsString(gradlePath.get());
            Pattern versionPattern = Pattern.compile("sourceCompatibility = '([^']+)'|sourceCompatibility = \"([^\"]+)\"");
            var matcher = versionPattern.matcher(gradleContent);
            if (matcher.find()) {
                return matcher.group(1) != null ? matcher.group(1) : matcher.group(2);
            }
        }
        
        return "Unknown";
    }
    
    private static String detectPythonVersion(Path projectRoot) throws IOException {
        // Check pyproject.toml
        Optional<Path> pyprojectPath = FileScanner.findFile(projectRoot, "pyproject.toml");
        if (pyprojectPath.isPresent()) {
            String content = FileScanner.readFileAsString(pyprojectPath.get());
            Pattern versionPattern = Pattern.compile("python\\s*=\\s*\"([^\"]+)\"");
            var matcher = versionPattern.matcher(content);
            if (matcher.find()) {
                return matcher.group(1);
            }
        }
        
        // Check .python-version file
        Optional<Path> pythonVersionPath = FileScanner.findFile(projectRoot, ".python-version");
        if (pythonVersionPath.isPresent()) {
            String version = FileScanner.readFileAsString(pythonVersionPath.get()).trim();
            return version.split("\n")[0]; // First line
        }
        
        return "Unknown";
    }
    
    private static String detectNodeVersion(Path projectRoot) throws IOException {
        Optional<Path> packageJsonPath = FileScanner.findFile(projectRoot, "package.json");
        if (packageJsonPath.isPresent()) {
            String content = FileScanner.readFileAsString(packageJsonPath.get());
            Pattern versionPattern = Pattern.compile("\"engines\"\\s*:\\s*\\{\\s*\"node\"\\s*:\\s*\"([^\"]+)\"");
            var matcher = versionPattern.matcher(content);
            if (matcher.find()) {
                return matcher.group(1);
            }
        }
        
        // Check .nvmrc file
        Optional<Path> nvmrcPath = FileScanner.findFile(projectRoot, ".nvmrc");
        if (nvmrcPath.isPresent()) {
            String version = FileScanner.readFileAsString(nvmrcPath.get()).trim();
            return version.split("\n")[0];
        }
        
        return "Unknown";
    }
    
    private static List<String> detectAllLanguages(Path projectRoot) throws IOException {
        Set<String> extensions = FileScanner.getAllFileExtensions(projectRoot);
        Set<String> languages = new LinkedHashSet<>();
        
        if (extensions.contains("java")) languages.add("Java");
        if (extensions.contains("py")) languages.add("Python");
        if (extensions.contains("ts") || extensions.contains("tsx")) languages.add("TypeScript");
        if (extensions.contains("js") || extensions.contains("jsx")) languages.add("JavaScript");
        if (extensions.contains("go")) languages.add("Go");
        if (extensions.contains("rs")) languages.add("Rust");
        if (extensions.contains("cs")) languages.add("C#");
        if (extensions.contains("rb")) languages.add("Ruby");
        if (extensions.contains("php")) languages.add("PHP");
        
        return new ArrayList<>(languages);
    }
    
    private static List<DetectionResult> detectFrameworksByLanguage(Path projectRoot, String primaryLanguage) throws IOException {
        List<DetectionResult> results = new ArrayList<>();
        
        switch (primaryLanguage) {
            case "Java":
                results.addAll(detectJavaFrameworks(projectRoot));
                break;
            case "Python":
                results.addAll(detectPythonFrameworks(projectRoot));
                break;
            case "JavaScript":
            case "TypeScript":
                results.addAll(detectNodeFrameworks(projectRoot));
                break;
        }
        
        return results;
    }
    
    private static List<DetectionResult> detectJavaFrameworks(Path projectRoot) throws IOException {
        List<DetectionResult> results = new ArrayList<>();
        
        Optional<Path> pomPath = FileScanner.findFile(projectRoot, "pom.xml");
        if (pomPath.isPresent()) {
            List<PomParser.Dependency> deps = PomParser.parsePomDependencies(pomPath.get());
            
            for (FrameworkSignature sig : JAVA_FRAMEWORKS.values()) {
                for (PomParser.Dependency dep : deps) {
                    for (String keyword : sig.dependencies) {
                        if (dep.artifactId.contains(keyword) || dep.groupId.contains(keyword)) {
                            results.add(new DetectionResult(
                                sig.name,
                                "Framework",
                                DetectionResult.Confidence.HIGH,
                                keyword + " dependency found in pom.xml",
                                Arrays.asList("Dependency: " + dep.groupId + ":" + dep.artifactId + ":" + dep.version),
                                dep.version
                            ));
                            break;
                        }
                    }
                }
            }
        }
        
        Optional<Path> gradlePath = FileScanner.findFile(projectRoot, "build.gradle");
        if (gradlePath.isPresent()) {
            String gradleContent = FileScanner.readFileAsString(gradlePath.get());
            
            for (String key : JAVA_FRAMEWORKS.keySet()) {
                if (gradleContent.contains(key)) {
                    FrameworkSignature sig = JAVA_FRAMEWORKS.get(key);
                    results.add(new DetectionResult(
                        sig.name,
                        "Framework",
                        DetectionResult.Confidence.HIGH,
                        key + " found in build.gradle",
                        Arrays.asList("Gradle dependency: " + key)
                    ));
                }
            }
        }
        
        return results;
    }
    
    private static List<DetectionResult> detectPythonFrameworks(Path projectRoot) throws IOException {
        List<DetectionResult> results = new ArrayList<>();
        
        Optional<Path> requirementsPath = FileScanner.findFile(projectRoot, "requirements.txt");
        if (requirementsPath.isPresent()) {
            String content = FileScanner.readFileAsString(requirementsPath.get());
            
            for (String key : PYTHON_FRAMEWORKS.keySet()) {
                if (content.contains(key)) {
                    FrameworkSignature sig = PYTHON_FRAMEWORKS.get(key);
                    results.add(new DetectionResult(
                        sig.name,
                        "Framework",
                        DetectionResult.Confidence.HIGH,
                        key + " found in requirements.txt",
                        Arrays.asList("Package: " + key)
                    ));
                }
            }
        }
        
        // Check for common Python framework files
        Optional<Path> managePyPath = FileScanner.findFile(projectRoot, "manage.py");
        if (managePyPath.isPresent()) {
            results.add(new DetectionResult(
                "Django",
                "Framework",
                DetectionResult.Confidence.HIGH,
                "Django manage.py file found",
                Arrays.asList("File: manage.py")
            ));
        }
        
        Optional<Path> appPyPath = FileScanner.findFile(projectRoot, "app.py");
        if (appPyPath.isPresent()) {
            results.add(new DetectionResult(
                "Flask",
                "Framework",
                DetectionResult.Confidence.MEDIUM,
                "app.py file found (common Flask pattern)",
                Arrays.asList("File: app.py")
            ));
        }
        
        return results;
    }
    
    private static List<DetectionResult> detectNodeFrameworks(Path projectRoot) throws IOException {
        List<DetectionResult> results = new ArrayList<>();
        
        Optional<Path> packageJsonPath = FileScanner.findFile(projectRoot, "package.json");
        if (packageJsonPath.isPresent()) {
            String content = FileScanner.readFileAsString(packageJsonPath.get());
            
            for (String key : NODE_FRAMEWORKS.keySet()) {
                if (content.contains(key)) {
                    FrameworkSignature sig = NODE_FRAMEWORKS.get(key);
                    results.add(new DetectionResult(
                        sig.name,
                        "Framework",
                        DetectionResult.Confidence.HIGH,
                        key + " dependency found in package.json",
                        Arrays.asList("NPM dependency: " + key)
                    ));
                }
            }
        }
        
        return results;
    }
    
    private static DetectionResult detectBuildSystem(Path projectRoot, String primaryLanguage) throws IOException {
        switch (primaryLanguage) {
            case "Java":
                if (FileScanner.fileExists(projectRoot, "pom.xml")) {
                    return new DetectionResult(
                        "Maven",
                        "Build System",
                        DetectionResult.Confidence.HIGH,
                        "pom.xml found",
                        Arrays.asList("File: pom.xml")
                    );
                }
                if (FileScanner.fileExists(projectRoot, "build.gradle")) {
                    return new DetectionResult(
                        "Gradle",
                        "Build System",
                        DetectionResult.Confidence.HIGH,
                        "build.gradle found",
                        Arrays.asList("File: build.gradle")
                    );
                }
                break;
                
            case "Python":
                if (FileScanner.fileExists(projectRoot, "pyproject.toml")) {
                    return new DetectionResult(
                        "Poetry/Pip",
                        "Build System",
                        DetectionResult.Confidence.HIGH,
                        "pyproject.toml found",
                        Arrays.asList("File: pyproject.toml")
                    );
                }
                if (FileScanner.fileExists(projectRoot, "requirements.txt")) {
                    return new DetectionResult(
                        "Pip",
                        "Build System",
                        DetectionResult.Confidence.HIGH,
                        "requirements.txt found",
                        Arrays.asList("File: requirements.txt")
                    );
                }
                break;
                
            case "JavaScript":
            case "TypeScript":
                if (FileScanner.fileExists(projectRoot, "package.json")) {
                    return new DetectionResult(
                        "NPM",
                        "Build System",
                        DetectionResult.Confidence.HIGH,
                        "package.json found",
                        Arrays.asList("File: package.json")
                    );
                }
                break;
        }
        
        return new DetectionResult(
            "Unknown",
            "Build System",
            DetectionResult.Confidence.LOW,
            "No standard build system detected",
            Arrays.asList()
        );
    }
    
    /**
     * Helper class to represent framework signatures.
     */
    private static class FrameworkSignature {
        String name;
        List<String> dependencies;
        List<String> configPatterns;
        
        FrameworkSignature(String name, List<String> dependencies, List<String> configPatterns) {
            this.name = name;
            this.dependencies = dependencies;
            this.configPatterns = configPatterns;
        }
    }
}
