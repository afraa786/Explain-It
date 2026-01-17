package io.explainit.analyzer;

import io.explainit.dto.AnalysisResult;
import io.explainit.dto.ApiAnalysisResult;
import io.explainit.dto.ApiRoute;
import io.explainit.util.FileScanner;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Analyzes REST APIs: groups endpoints by HTTP method, counts controllers,
 * detects security, and determines REST maturity level.
 */
public class ApiAnalyzer implements IProjectAnalyzer {
    
    private static final Pattern REST_CONTROLLER_PATTERN = Pattern.compile("@(Rest)?Controller");
    private static final Pattern REQUEST_MAPPING_PATTERN = Pattern.compile("@(Request|Get|Post|Put|Delete|Patch)Mapping\\s*\\(\\s*[\"']([^\"']+)[\"']");
    private static final Pattern PATH_PATTERN = Pattern.compile("@RequestMapping\\s*\\(\\s*\"([^\"]+)\"");
    private static final Pattern SECURED_PATTERN = Pattern.compile("@(PreAuthorize|Secured|RolesAllowed)");
    private static final Pattern HATEOAS_PATTERN = Pattern.compile("(EntityModel|CollectionModel|RepresentationModel)");
    
    @Override
    public AnalysisResult analyze(Path projectRoot) throws Exception {
        ApiAnalysisResult result = new ApiAnalysisResult();
        List<ApiRoute> apiRoutes = new ArrayList<>();
        Map<String, Integer> endpointsByMethod = new HashMap<>();
        
        List<Path> javaFiles = FileScanner.findFilesByExtension(projectRoot, "java");
        int controllerCount = 0;
        int securedCount = 0;
        boolean hasHateoas = false;
        
        for (Path javaFile : javaFiles) {
            String content = FileScanner.readFileAsString(javaFile);
            
            if (REST_CONTROLLER_PATTERN.matcher(content).find()) {
                controllerCount++;
                
                String className = extractClassName(javaFile, content);
                
                // Check if controller is secured
                if (SECURED_PATTERN.matcher(content).find()) {
                    securedCount++;
                }
                
                // Check for HATEOAS
                if (HATEOAS_PATTERN.matcher(content).find()) {
                    hasHateoas = true;
                }
                
                // Extract endpoints
                var matcher = REQUEST_MAPPING_PATTERN.matcher(content);
                while (matcher.find()) {
                    String method = matcher.group(1) != null ? matcher.group(1).toUpperCase() : "GET";
                    if ("REQUEST".equals(method)) method = "GET";
                    String path = matcher.group(2);
                    
                    // Count by method
                    endpointsByMethod.put(method, endpointsByMethod.getOrDefault(method, 0) + 1);
                    apiRoutes.add(new ApiRoute(method, path, className));
                }
                
                var pathMatcher = PATH_PATTERN.matcher(content);
                if (pathMatcher.find() && apiRoutes.isEmpty()) {
                    String basePath = pathMatcher.group(1);
                    endpointsByMethod.put("GET", endpointsByMethod.getOrDefault("GET", 0) + 1);
                    apiRoutes.add(new ApiRoute("GET", basePath, className));
                }
            }
        }
        
        result.setControllerCount(controllerCount);
        result.setEndpointCount(apiRoutes.size());
        result.setEndpointsByMethod(endpointsByMethod);
        result.setSecuredEndpoints(securedCount);
        result.setPublicEndpoints(controllerCount - securedCount);
        result.setRoutes(apiRoutes);
        
        // Determine REST maturity level
        String maturityLevel = determineMaturityLevel(apiRoutes, hasHateoas, endpointsByMethod);
        result.setRestMaturityLevel(maturityLevel);
        
        result.setSuccess(true);
        return result;
    }
    
    private String determineMaturityLevel(List<ApiRoute> routes, boolean hasHateoas, Map<String, Integer> methods) {
        if (routes.isEmpty()) {
            return "NONE";
        }
        
        // Check for HATEOAS/HYPERMEDIA
        if (hasHateoas) {
            return "HATEOAS";
        }
        
        // Check for full REST methods (GET, POST, PUT, DELETE)
        boolean hasGet = methods.containsKey("GET");
        boolean hasPost = methods.containsKey("POST");
        boolean hasPut = methods.containsKey("PUT");
        boolean hasDelete = methods.containsKey("DELETE");
        
        if (hasGet && hasPost && hasPut && hasDelete) {
            return "LAYERED";
        }
        
        // Basic CRUD
        return "BASIC_CRUD";
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

