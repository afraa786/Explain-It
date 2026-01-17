package io.explainit.analyzer;

import io.explainit.dto.AnalysisResult;
import io.explainit.dto.DetectionResult;
import io.explainit.util.FileScanner;
import io.explainit.util.PomParser;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Enhanced SecurityAnalyzer that detects security mechanisms across Java, Python, and Node.js.
 * Detects authentication methods, authorization patterns, encryption, and security frameworks.
 */
public class SecurityAnalyzer implements IProjectAnalyzer {
    
    // Security framework signatures
    private static final Map<String, SecuritySignature> SECURITY_FRAMEWORKS = Map.ofEntries(
        Map.entry("spring-security", new SecuritySignature(
            "Spring Security",
            "Framework",
            Arrays.asList("spring-boot-starter-security", "spring-security-core")
        )),
        Map.entry("oauth2", new SecuritySignature(
            "OAuth 2.0",
            "Authentication",
            Arrays.asList("oauth2-client", "spring-security-oauth2", "@nestjs/passport")
        )),
        Map.entry("jwt", new SecuritySignature(
            "JWT (JSON Web Tokens)",
            "Authentication",
            Arrays.asList("jjwt", "jsonwebtoken", "python-jose", "pyjwt")
        )),
        Map.entry("auth0", new SecuritySignature(
            "Auth0",
            "Authentication",
            Arrays.asList("auth0", "auth0-java", "auth0-python")
        )),
        Map.entry("bcrypt", new SecuritySignature(
            "BCrypt (Password Hashing)",
            "Encryption",
            Arrays.asList("bcrypt", "spring-security-crypto", "bcryptjs", "bcrypt-python")
        )),
        Map.entry("cors", new SecuritySignature(
            "CORS Configuration",
            "Security",
            Arrays.asList("cors", "CorsConfig")
        )),
        Map.entry("helmet", new SecuritySignature(
            "Helmet (Security Headers)",
            "Security",
            Arrays.asList("helmet")
        )),
        Map.entry("csrf", new SecuritySignature(
            "CSRF Protection",
            "Security",
            Arrays.asList("csrf", "CsrfFilter")
        ))
    );
    
    @Override
    public AnalysisResult analyze(Path projectRoot) throws Exception {
        AnalysisResult result = new AnalysisResult("Security");
        List<DetectionResult> securityDetections = new ArrayList<>();
        
        // Detect authentication mechanisms
        securityDetections.addAll(detectAuthentication(projectRoot));
        
        // Detect encryption
        securityDetections.addAll(detectEncryption(projectRoot));
        
        // Detect CORS configuration
        securityDetections.addAll(detectCORS(projectRoot));
        
        // Detect security configuration files
        securityDetections.addAll(detectSecurityConfigs(projectRoot));
        
        result.setSuccess(true);
        return result;
    }
    
    private List<DetectionResult> detectAuthentication(Path projectRoot) throws IOException {
        List<DetectionResult> results = new ArrayList<>();
        
        // Check Maven dependencies
        Optional<Path> pomPath = FileScanner.findFile(projectRoot, "pom.xml");
        if (pomPath.isPresent()) {
            List<PomParser.Dependency> deps = PomParser.parsePomDependencies(pomPath.get());
            
            for (PomParser.Dependency dep : deps) {
                // Spring Security
                if (dep.artifactId.contains("security") && dep.groupId.contains("spring")) {
                    results.add(new DetectionResult(
                        "Spring Security",
                        "Authentication Framework",
                        DetectionResult.Confidence.HIGH,
                        "Spring Security dependency detected in pom.xml",
                        Arrays.asList("Dependency: " + dep.groupId + ":" + dep.artifactId),
                        dep.version
                    ));
                }
                
                // OAuth2
                if (dep.artifactId.contains("oauth2") || dep.artifactId.contains("oauth")) {
                    results.add(new DetectionResult(
                        "OAuth 2.0",
                        "Authentication",
                        DetectionResult.Confidence.HIGH,
                        "OAuth 2.0 dependency detected",
                        Arrays.asList("Dependency: " + dep.groupId + ":" + dep.artifactId),
                        dep.version
                    ));
                }
                
                // JWT
                if (dep.artifactId.contains("jjwt") || dep.artifactId.contains("jwt")) {
                    results.add(new DetectionResult(
                        "JWT (JSON Web Tokens)",
                        "Authentication",
                        DetectionResult.Confidence.HIGH,
                        "JWT library dependency detected",
                        Arrays.asList("Dependency: " + dep.groupId + ":" + dep.artifactId),
                        dep.version
                    ));
                }
            }
        }
        
        // Check npm dependencies
        Optional<Path> packageJsonPath = FileScanner.findFile(projectRoot, "package.json");
        if (packageJsonPath.isPresent()) {
            String content = FileScanner.readFileAsString(packageJsonPath.get());
            
            if (content.contains("jsonwebtoken")) {
                results.add(new DetectionResult(
                    "JWT (JSON Web Tokens)",
                    "Authentication",
                    DetectionResult.Confidence.HIGH,
                    "JWT package found in package.json",
                    Arrays.asList("NPM package: jsonwebtoken")
                ));
            }
            
            if (content.contains("passport")) {
                results.add(new DetectionResult(
                    "Passport.js",
                    "Authentication",
                    DetectionResult.Confidence.HIGH,
                    "Passport authentication middleware detected",
                    Arrays.asList("NPM package: passport")
                ));
            }
            
            if (content.contains("auth0")) {
                results.add(new DetectionResult(
                    "Auth0",
                    "Authentication",
                    DetectionResult.Confidence.HIGH,
                    "Auth0 package found",
                    Arrays.asList("NPM package: auth0")
                ));
            }
        }
        
        // Check Python requirements
        Optional<Path> requirementsPath = FileScanner.findFile(projectRoot, "requirements.txt");
        if (requirementsPath.isPresent()) {
            String content = FileScanner.readFileAsString(requirementsPath.get());
            
            if (content.contains("django-rest-framework") || content.contains("djangorestframework")) {
                results.add(new DetectionResult(
                    "Django REST Framework Auth",
                    "Authentication",
                    DetectionResult.Confidence.HIGH,
                    "Django REST Framework (has built-in auth)",
                    Arrays.asList("Python package: djangorestframework")
                ));
            }
            
            if (content.contains("pyjwt") || content.contains("python-jose")) {
                results.add(new DetectionResult(
                    "JWT (JSON Web Tokens)",
                    "Authentication",
                    DetectionResult.Confidence.HIGH,
                    "JWT library found in requirements.txt",
                    Arrays.asList("Python package: pyjwt/python-jose")
                ));
            }
            
            if (content.contains("python-dotenv")) {
                results.add(new DetectionResult(
                    "Environment-based Configuration",
                    "Authentication",
                    DetectionResult.Confidence.MEDIUM,
                    "Likely storing secrets in .env file",
                    Arrays.asList("Python package: python-dotenv")
                ));
            }
        }
        
        return results;
    }
    
    private List<DetectionResult> detectEncryption(Path projectRoot) throws IOException {
        List<DetectionResult> results = new ArrayList<>();
        
        // Check Maven dependencies
        Optional<Path> pomPath = FileScanner.findFile(projectRoot, "pom.xml");
        if (pomPath.isPresent()) {
            List<PomParser.Dependency> deps = PomParser.parsePomDependencies(pomPath.get());
            
            for (PomParser.Dependency dep : deps) {
                // BCrypt
                if (dep.artifactId.contains("bcprov") || dep.artifactId.contains("bcrypt")) {
                    results.add(new DetectionResult(
                        "BCrypt (Password Hashing)",
                        "Encryption",
                        DetectionResult.Confidence.HIGH,
                        "BCrypt password hashing library detected",
                        Arrays.asList("Dependency: " + dep.groupId + ":" + dep.artifactId),
                        dep.version
                    ));
                }
                
                // Bouncy Castle (Cryptography)
                if (dep.groupId.contains("bouncycastle")) {
                    results.add(new DetectionResult(
                        "Bouncy Castle",
                        "Encryption",
                        DetectionResult.Confidence.HIGH,
                        "Cryptography library (Bouncy Castle) detected",
                        Arrays.asList("Dependency: " + dep.groupId + ":" + dep.artifactId),
                        dep.version
                    ));
                }
            }
        }
        
        // Check npm dependencies
        Optional<Path> packageJsonPath = FileScanner.findFile(projectRoot, "package.json");
        if (packageJsonPath.isPresent()) {
            String content = FileScanner.readFileAsString(packageJsonPath.get());
            
            if (content.contains("bcryptjs")) {
                results.add(new DetectionResult(
                    "BCryptjs (Password Hashing)",
                    "Encryption",
                    DetectionResult.Confidence.HIGH,
                    "BCryptjs library found for password hashing",
                    Arrays.asList("NPM package: bcryptjs")
                ));
            }
            
            if (content.contains("crypto")) {
                results.add(new DetectionResult(
                    "Node.js Crypto Module",
                    "Encryption",
                    DetectionResult.Confidence.MEDIUM,
                    "Using built-in Node.js crypto module",
                    Arrays.asList("NPM: built-in crypto")
                ));
            }
        }
        
        // Check Python requirements
        Optional<Path> requirementsPath = FileScanner.findFile(projectRoot, "requirements.txt");
        if (requirementsPath.isPresent()) {
            String content = FileScanner.readFileAsString(requirementsPath.get());
            
            if (content.contains("bcrypt")) {
                results.add(new DetectionResult(
                    "BCrypt (Password Hashing)",
                    "Encryption",
                    DetectionResult.Confidence.HIGH,
                    "BCrypt library found for password hashing",
                    Arrays.asList("Python package: bcrypt")
                ));
            }
            
            if (content.contains("cryptography")) {
                results.add(new DetectionResult(
                    "Cryptography Library",
                    "Encryption",
                    DetectionResult.Confidence.HIGH,
                    "Cryptography library for encryption/decryption",
                    Arrays.asList("Python package: cryptography")
                ));
            }
        }
        
        return results;
    }
    
    private List<DetectionResult> detectCORS(Path projectRoot) throws IOException {
        List<DetectionResult> results = new ArrayList<>();
        
        // Check for CORS config files
        Optional<Path> corsConfigPath = FileScanner.findFile(projectRoot, "CorsConfig.java");
        if (corsConfigPath.isPresent()) {
            String content = FileScanner.readFileAsString(corsConfigPath.get());
            results.add(new DetectionResult(
                "CORS Configuration",
                "Security",
                DetectionResult.Confidence.HIGH,
                "CORS configuration class detected",
                Arrays.asList("File: CorsConfig.java")
            ));
        }
        
        // Check Maven for CORS
        Optional<Path> pomPath = FileScanner.findFile(projectRoot, "pom.xml");
        if (pomPath.isPresent()) {
            String content = FileScanner.readFileAsString(pomPath.get());
            if (content.contains("cors")) {
                results.add(new DetectionResult(
                    "CORS Support",
                    "Security",
                    DetectionResult.Confidence.MEDIUM,
                    "CORS mentioned in pom.xml",
                    Arrays.asList("Config: pom.xml")
                ));
            }
        }
        
        // Check npm for helmet (security headers)
        Optional<Path> packageJsonPath = FileScanner.findFile(projectRoot, "package.json");
        if (packageJsonPath.isPresent()) {
            String content = FileScanner.readFileAsString(packageJsonPath.get());
            if (content.contains("helmet")) {
                results.add(new DetectionResult(
                    "Helmet.js (Security Headers)",
                    "Security",
                    DetectionResult.Confidence.HIGH,
                    "Helmet.js HTTP security headers middleware detected",
                    Arrays.asList("NPM package: helmet")
                ));
            }
            
            if (content.contains("cors")) {
                results.add(new DetectionResult(
                    "CORS Middleware",
                    "Security",
                    DetectionResult.Confidence.HIGH,
                    "CORS middleware package detected",
                    Arrays.asList("NPM package: cors")
                ));
            }
        }
        
        return results;
    }
    
    private List<DetectionResult> detectSecurityConfigs(Path projectRoot) throws IOException {
        List<DetectionResult> results = new ArrayList<>();
        
        // Check for environment file (.env)
        Optional<Path> envPath = FileScanner.findFile(projectRoot, ".env");
        if (envPath.isPresent()) {
            results.add(new DetectionResult(
                "Environment-based Secrets",
                "Configuration",
                DetectionResult.Confidence.HIGH,
                ".env file found (likely contains secrets)",
                Arrays.asList("File: .env")
            ));
        }
        
        // Check application properties
        Optional<Path> appPropsPath = FileScanner.findFile(projectRoot, "application.properties");
        if (appPropsPath.isPresent()) {
            String content = FileScanner.readFileAsString(appPropsPath.get());
            if (content.contains("security") || content.contains("secret") || content.contains("password")) {
                results.add(new DetectionResult(
                    "Security Configuration",
                    "Configuration",
                    DetectionResult.Confidence.HIGH,
                    "Security settings in application.properties",
                    Arrays.asList("File: application.properties")
                ));
            }
        }
        
        // Check for KeyStore usage
        Optional<Path> keystorePath = FileScanner.findFile(projectRoot, "keystore.jks");
        if (keystorePath.isPresent()) {
            results.add(new DetectionResult(
                "KeyStore (SSL/TLS)",
                "Encryption",
                DetectionResult.Confidence.HIGH,
                "Java KeyStore file detected (likely for SSL/TLS)",
                Arrays.asList("File: keystore.jks")
            ));
        }
        
        return results;
    }
    
    /**
     * Helper class for security signature matching
     */
    private static class SecuritySignature {
        String name;
        String type;
        List<String> keywords;
        
        SecuritySignature(String name, String type, List<String> keywords) {
            this.name = name;
            this.type = type;
            this.keywords = keywords;
        }
    }
}
