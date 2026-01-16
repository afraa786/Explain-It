package io.explainit.analyzer;

import io.explainit.dto.*;
import io.explainit.util.FileScanner;
import io.explainit.util.PomParser;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EntryPointAnalyzer implements IProjectAnalyzer {
    
    private static final Pattern SPRING_BOOT_APP_PATTERN = Pattern.compile("@SpringBootApplication");
    private static final Pattern MAIN_METHOD_PATTERN = Pattern.compile("public\\s+static\\s+void\\s+main\\s*\\(\\s*String\\s*\\[\\s*\\]");
    private static final Pattern CONTROLLER_PATTERN = Pattern.compile("@(RestController|Controller)");
    private static final Pattern REQUEST_MAPPING_PATTERN = Pattern.compile("@(GetMapping|PostMapping|PutMapping|DeleteMapping|RequestMapping)\\s*\\(\\s*[\"']([^\"']+)[\"']");
    private static final Pattern METHOD_PATTERN = Pattern.compile("public\\s+\\w+\\s+(\\w+)\\s*\\(");
    
    @Override
    public void analyze(Path projectRoot, ProjectMetadata metadata) throws Exception {
        List<EntryPoint> entryPoints = new ArrayList<>();
        
        List<Path> javaFiles = FileScanner.findFilesByExtension(projectRoot, "java");
        
        // First, find Spring Boot main applications
        for (Path javaFile : javaFiles) {
            String content = FileScanner.readFileAsString(javaFile);
            
            if (SPRING_BOOT_APP_PATTERN.matcher(content).find() && 
                MAIN_METHOD_PATTERN.matcher(content).find()) {
                
                String className = extractClassName(javaFile, content);
                String packageName = extractPackageName(content);
                String fullyQualifiedName = packageName.isEmpty() ? className : packageName + "." + className;
                
                String relativePath = javaFile.toString().replaceAll("\\\\", "/");
                if (relativePath.contains("expo/")) {
                    relativePath = relativePath.substring(relativePath.indexOf("expo/") + 5);
                }
                
                EntryPoint ep = new EntryPoint(
                    relativePath,
                    fullyQualifiedName,
                    "main(String[] args)",
                    "Spring Boot Application"
                );
                entryPoints.add(ep);
            }
        }
        
        // If no Spring Boot app found, look for regular main methods
        if (entryPoints.isEmpty()) {
            for (Path javaFile : javaFiles) {
                String content = FileScanner.readFileAsString(javaFile);
                
                if (MAIN_METHOD_PATTERN.matcher(content).find()) {
                    String className = extractClassName(javaFile, content);
                    String packageName = extractPackageName(content);
                    String fullyQualifiedName = packageName.isEmpty() ? className : packageName + "." + className;
                    
                    String relativePath = javaFile.toString().replaceAll("\\\\", "/");
                    if (relativePath.contains("expo/")) {
                        relativePath = relativePath.substring(relativePath.indexOf("expo/") + 5);
                    }
                    
                    EntryPoint ep = new EntryPoint(
                        relativePath,
                        fullyQualifiedName,
                        "main(String[] args)",
                        "Java Application"
                    );
                    entryPoints.add(ep);
                }
            }
        }
        
        // Also detect REST controller entry points (request handlers)
        for (Path javaFile : javaFiles) {
            String content = FileScanner.readFileAsString(javaFile);
            
            if (CONTROLLER_PATTERN.matcher(content).find()) {
                String className = extractClassName(javaFile, content);
                String packageName = extractPackageName(content);
                String fullyQualifiedName = packageName.isEmpty() ? className : packageName + "." + className;
                
                String relativePath = javaFile.toString().replaceAll("\\\\", "/");
                if (relativePath.contains("expo/")) {
                    relativePath = relativePath.substring(relativePath.indexOf("expo/") + 5);
                }
                
                // Extract request mapping methods
                Matcher methodMatcher = METHOD_PATTERN.matcher(content);
                while (methodMatcher.find()) {
                    String methodName = methodMatcher.group(1);
                    
                    // Check if this method has a request mapping annotation
                    int methodStart = methodMatcher.start();
                    String beforeMethod = content.substring(Math.max(0, methodStart - 200), methodStart);
                    if (REQUEST_MAPPING_PATTERN.matcher(beforeMethod).find()) {
                        EntryPoint ep = new EntryPoint(
                            relativePath,
                            fullyQualifiedName,
                            methodName + "()",
                            "Spring REST Controller"
                        );
                        
                        // Only add if not already in list (avoid duplicates)
                        if (!entryPoints.stream().anyMatch(e -> 
                            e.getClassName().equals(fullyQualifiedName) && 
                            e.getMethodName().equals(methodName + "()"))) {
                            entryPoints.add(ep);
                        }
                    }
                }
            }
        }
        
        metadata.setEntryPoints(entryPoints);
    }
    
    private String extractClassName(Path filePath, String content) {
        String filename = filePath.getFileName().toString();
        if (filename.endsWith(".java")) {
            return filename.substring(0, filename.length() - 5);
        }
        return filename;
    }
    
    private String extractPackageName(String content) {
        Pattern pattern = Pattern.compile("package\\s+([\\w.]+)\\s*;");
        var matcher = pattern.matcher(content);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }
}
