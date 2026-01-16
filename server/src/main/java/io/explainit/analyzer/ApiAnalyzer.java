package io.explainit.analyzer;

import io.explainit.dto.ProjectMetadata;
import io.explainit.dto.ApiRoute;
import io.explainit.util.FileScanner;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Pattern;

public class ApiAnalyzer implements IProjectAnalyzer {
    
    private static final Pattern REST_CONTROLLER_PATTERN = Pattern.compile("@(Rest)?Controller");
    private static final Pattern REQUEST_MAPPING_PATTERN = Pattern.compile("@(Request|Get|Post|Put|Delete|Patch)Mapping\\s*\\(\\s*[\"']([^\"']+)[\"']");
    private static final Pattern PATH_PATTERN = Pattern.compile("@RequestMapping\\s*\\(\\s*\"([^\"]+)\"");
    
    @Override
    public void analyze(Path projectRoot, ProjectMetadata metadata) throws Exception {
        List<ApiRoute> apiRoutes = new ArrayList<>();
        boolean apiDetected = false;
        
        List<Path> javaFiles = FileScanner.findFilesByExtension(projectRoot, "java");
        
        for (Path javaFile : javaFiles) {
            String content = FileScanner.readFileAsString(javaFile);
            
            if (REST_CONTROLLER_PATTERN.matcher(content).find()) {
                apiDetected = true;
                
                String className = extractClassName(javaFile, content);
                
                
                var matcher = REQUEST_MAPPING_PATTERN.matcher(content);
                while (matcher.find()) {
                    String method = matcher.group(1) != null ? matcher.group(1).toUpperCase() : "GET";
                    if ("REQUEST".equals(method)) method = "GET";
                    String path = matcher.group(2);
                    apiRoutes.add(new ApiRoute(method, path, className));
                }
                
                var pathMatcher = PATH_PATTERN.matcher(content);
                if (pathMatcher.find() && apiRoutes.isEmpty()) {
                    String basePath = pathMatcher.group(1);
                    apiRoutes.add(new ApiRoute("GET", basePath, className));
                }
            }
        }
        
        metadata.setApiDetected(apiDetected);
        metadata.setApiRoutes(apiRoutes);
    }
    
    private String extractClassName(Path filePath, String content) {
        String filename = filePath.getFileName().toString();
        if (filename.endsWith(".java")) {
            return filename.substring(0, filename.length() - 5);
        }
        
        Pattern classPattern = Pattern.compile("public\\s+class\\s+(\\w+)");
        var matcher = classPattern.matcher(content);
        if (matcher.find()) {
            return matcher.group(1);
        }
        
        return filename;
    }
}

