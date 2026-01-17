package io.explainit.analyzer;

import io.explainit.dto.*;
import io.explainit.util.FileScanner;
import io.explainit.util.PomParser;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Detects and analyzes entry points with proper prioritization:
 * @SpringBootApplication > main method > ApplicationRunner > CommandLineRunner
 */
public class EntryPointAnalyzer implements IProjectAnalyzer {
    
    private static final Pattern SPRING_BOOT_APP_PATTERN = Pattern.compile("@SpringBootApplication");
    private static final Pattern MAIN_METHOD_PATTERN = Pattern.compile("public\\s+static\\s+void\\s+main\\s*\\(\\s*String\\s*\\[\\s*\\]");
    private static final Pattern APPLICATION_RUNNER_PATTERN = Pattern.compile("implements\\s+ApplicationRunner");
    private static final Pattern COMMAND_LINE_RUNNER_PATTERN = Pattern.compile("implements\\s+CommandLineRunner");
    private static final Pattern CONTROLLER_PATTERN = Pattern.compile("@(RestController|Controller)");
    private static final Pattern REQUEST_MAPPING_PATTERN = Pattern.compile("@(GetMapping|PostMapping|PutMapping|DeleteMapping|RequestMapping)\\s*\\(\\s*[\"']([^\"']+)[\"']");
    private static final Pattern METHOD_PATTERN = Pattern.compile("public\\s+\\w+\\s+(\\w+)\\s*\\(");
    
    @Override
    public AnalysisResult analyze(Path projectRoot) throws Exception {
        EntryPointAnalysisResult result = new EntryPointAnalysisResult();
        List<EntryPoint> entryPoints = new ArrayList<>();
        
        List<Path> javaFiles = FileScanner.findFilesByExtension(projectRoot, "java");
        
        // Priority 1: Find @SpringBootApplication with main method
        EntryPoint primaryEntry = findPrimarySpringBootEntry(javaFiles);
        if (primaryEntry != null) {
            result.setPrimaryEntryPoint(primaryEntry.getClassName());
            result.setEntryType("SPRING_BOOT");
            entryPoints.add(primaryEntry);
        }
        
        // Priority 2: Find regular main methods
        if (primaryEntry == null) {
            EntryPoint mainEntry = findMainMethodEntry(javaFiles);
            if (mainEntry != null) {
                result.setPrimaryEntryPoint(mainEntry.getClassName());
                result.setEntryType("JAVA_MAIN");
                entryPoints.add(mainEntry);
            }
        }
        
        // Priority 3: Find ApplicationRunner / CommandLineRunner implementations
        findRunnerImplementations(javaFiles, entryPoints);
        
        // Also detect controller entry points
        findControllerEntryPoints(javaFiles, entryPoints);
        
        result.setSecondaryEntryPoints(entryPoints.size() > 1 ? entryPoints.subList(1, entryPoints.size()) : new ArrayList<>());
        result.setTotalEntryPoints(entryPoints.size());
        result.setSuccess(true);
        
        return result;
    }
    
    private EntryPoint findPrimarySpringBootEntry(List<Path> javaFiles) throws Exception {
        for (Path javaFile : javaFiles) {
            String content = FileScanner.readFileAsString(javaFile);
            
            if (SPRING_BOOT_APP_PATTERN.matcher(content).find() && 
                MAIN_METHOD_PATTERN.matcher(content).find()) {
                return createEntryPoint(javaFile, content, "Spring Boot Application");
            }
        }
        return null;
    }
    
    private EntryPoint findMainMethodEntry(List<Path> javaFiles) throws Exception {
        for (Path javaFile : javaFiles) {
            String content = FileScanner.readFileAsString(javaFile);
            
            if (MAIN_METHOD_PATTERN.matcher(content).find()) {
                return createEntryPoint(javaFile, content, "Java Application");
            }
        }
        return null;
    }
    
    private void findRunnerImplementations(List<Path> javaFiles, List<EntryPoint> entryPoints) throws Exception {
        for (Path javaFile : javaFiles) {
            String content = FileScanner.readFileAsString(javaFile);
            
            if (APPLICATION_RUNNER_PATTERN.matcher(content).find()) {
                EntryPoint ep = createEntryPoint(javaFile, content, "ApplicationRunner");
                if (!entryPoints.stream().anyMatch(e -> e.getClassName().equals(ep.getClassName()))) {
                    entryPoints.add(ep);
                }
            } else if (COMMAND_LINE_RUNNER_PATTERN.matcher(content).find()) {
                EntryPoint ep = createEntryPoint(javaFile, content, "CommandLineRunner");
                if (!entryPoints.stream().anyMatch(e -> e.getClassName().equals(ep.getClassName()))) {
                    entryPoints.add(ep);
                }
            }
        }
    }
    
    private void findControllerEntryPoints(List<Path> javaFiles, List<EntryPoint> entryPoints) throws Exception {
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
                    if (beforeMethod.contains("@GetMapping") || beforeMethod.contains("@PostMapping") ||
                        beforeMethod.contains("@PutMapping") || beforeMethod.contains("@DeleteMapping") ||
                        beforeMethod.contains("@RequestMapping")) {
                        
                        EntryPoint ep = new EntryPoint(
                            relativePath,
                            fullyQualifiedName,
                            methodName + "()",
                            "Spring REST Endpoint"
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
    }
    
    private EntryPoint createEntryPoint(Path javaFile, String content, String description) {
        String className = extractClassName(javaFile, content);
        String packageName = extractPackageName(content);
        String fullyQualifiedName = packageName.isEmpty() ? className : packageName + "." + className;
        
        String relativePath = javaFile.toString().replaceAll("\\\\", "/");
        if (relativePath.contains("expo/")) {
            relativePath = relativePath.substring(relativePath.indexOf("expo/") + 5);
        }
        
        return new EntryPoint(
            relativePath,
            fullyQualifiedName,
            "main(String[] args)",
            description
        );
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
