package io.explainit.analyzer;

import io.explainit.dto.*;
import io.explainit.util.FileScanner;
import io.explainit.util.PomParser;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Pattern;

public class EntryPointAnalyzer implements IProjectAnalyzer {
    
    private static final Pattern SPRING_BOOT_APP_PATTERN = Pattern.compile("@SpringBootApplication");
    private static final Pattern MAIN_METHOD_PATTERN = Pattern.compile("public\\s+static\\s+void\\s+main\\s*\\(\\s*String\\s*\\[\\s*\\]");
    
    @Override
    public void analyze(Path projectRoot, ProjectMetadata metadata) throws Exception {
        List<EntryPoint> entryPoints = new ArrayList<>();
        
        List<Path> javaFiles = FileScanner.findFilesByExtension(projectRoot, "java");
        
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
