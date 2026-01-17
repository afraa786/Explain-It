package io.explainit.analyzer;

import io.explainit.dto.*;
import java.nio.file.Path;
import java.util.*;

/**
 * Central orchestrator that manages all project analyzers and aggregates results.
 * Ensures single-pass analysis without duplicate directory scanning.
 * Produces a clean, strongly-typed final ProjectMetadata.
 */
public class ProjectAnalysisOrchestrator {
    
    private final List<IProjectAnalyzer> analyzers = Arrays.asList(
        new FrameworkAnalyzer(),
        new BuildInfoAnalyzer(),
        new EntryPointAnalyzer(),
        new ConfigFileAnalyzer(),
        new DataLayerAnalyzer(),
        new ApiAnalyzer()
        // Add other analyzers as needed
    );
    
    /**
     * Orchestrate a complete project analysis, invoking all analyzers once
     * and aggregating results into a strongly-typed ProjectMetadata.
     *
     * @param projectRoot The root path of the project
     * @return Complete ProjectMetadata with all analysis results
     * @throws Exception if any analyzer fails
     */
    public ProjectMetadata analyzeProject(Path projectRoot) throws Exception {
        ProjectMetadata metadata = new ProjectMetadata();
        
        // Extract project root name
        String projectRootName = projectRoot.getFileName().toString();
        metadata.setProjectRootName(projectRootName);
        
        // Invoke all analyzers and aggregate results
        for (IProjectAnalyzer analyzer : analyzers) {
            try {
                AnalysisResult result = analyzer.analyze(projectRoot);
                aggregateResult(metadata, result);
            } catch (Exception e) {
                // Log but continue with other analyzers
                System.err.println("Analyzer failed: " + e.getMessage());
            }
        }
        
        // Generate comprehensive summary
        generateSummary(metadata);
        
        return metadata;
    }
    
    /**
     * Aggregate individual analyzer results into ProjectMetadata.
     */
    private void aggregateResult(ProjectMetadata metadata, AnalysisResult result) {
        if (result instanceof FrameworkAnalysisResult) {
            aggregateFrameworkResult(metadata, (FrameworkAnalysisResult) result);
        } else if (result instanceof BuildAnalysisResult) {
            aggregateBuildResult(metadata, (BuildAnalysisResult) result);
        } else if (result instanceof EntryPointAnalysisResult) {
            aggregateEntryPointResult(metadata, (EntryPointAnalysisResult) result);
        } else if (result instanceof ConfigAnalysisResult) {
            aggregateConfigResult(metadata, (ConfigAnalysisResult) result);
        } else if (result instanceof DataLayerAnalysisResult) {
            aggregateDataLayerResult(metadata, (DataLayerAnalysisResult) result);
        } else if (result instanceof ApiAnalysisResult) {
            aggregateApiResult(metadata, (ApiAnalysisResult) result);
        }
    }
    
    private void aggregateFrameworkResult(ProjectMetadata metadata, FrameworkAnalysisResult result) {
        // Store full framework detection result
        if (result.getAllDetections() != null && !result.getAllDetections().isEmpty()) {
            metadata.setFrameworkDetection(result.getAllDetections().get(0));
        }
        
        // Set languages and frameworks
        if (result.getPrimaryLanguage() != null) {
            metadata.setLanguages(Arrays.asList(result.getPrimaryLanguage()));
        }
        if (result.getPrimaryFramework() != null) {
            metadata.setFrameworks(Arrays.asList(result.getPrimaryFramework()));
        }
    }
    
    private void aggregateBuildResult(ProjectMetadata metadata, BuildAnalysisResult result) {
        BuildInfo buildInfo = new BuildInfo();
        buildInfo.setBuildTool(result.getBuildTool());
        buildInfo.setJavaVersion(result.getJavaVersion());
        buildInfo.setDependencies(new ArrayList<>());
        metadata.setBuildInfo(buildInfo);
        
        // Also store size info in ProjectMetadata
        ProjectSizeInfo sizeInfo = new ProjectSizeInfo(
            (long)(result.getProjectSizeMB() * 1024 * 1024),
            result.getTotalFileCount()
        );
        metadata.setProjectSize(sizeInfo);
    }
    
    private void aggregateEntryPointResult(ProjectMetadata metadata, EntryPointAnalysisResult result) {
        // Create entry point list from result
        List<EntryPoint> entryPoints = new ArrayList<>();
        if (result.getSecondaryEntryPoints() != null) {
            entryPoints.addAll(result.getSecondaryEntryPoints());
        }
        metadata.setEntryPoints(entryPoints);
    }
    
    private void aggregateConfigResult(ProjectMetadata metadata, ConfigAnalysisResult result) {
        // Combine all configs into single list
        List<ConfigFile> allConfigs = new ArrayList<>();
        if (result.getRuntimeConfigs() != null) allConfigs.addAll(result.getRuntimeConfigs());
        if (result.getSecurityConfigs() != null) allConfigs.addAll(result.getSecurityConfigs());
        if (result.getBuildConfigs() != null) allConfigs.addAll(result.getBuildConfigs());
        if (result.getInfrastructureConfigs() != null) allConfigs.addAll(result.getInfrastructureConfigs());
        
        metadata.setConfigFiles(allConfigs);
    }
    
    private void aggregateDataLayerResult(ProjectMetadata metadata, DataLayerAnalysisResult result) {
        metadata.setDataLayerDetections(result.getDetections());
        
        // Set hints based on detections
        List<String> hints = new ArrayList<>();
        if (result.getDatabase() != null) {
            hints.add("Database: " + result.getDatabase());
        }
        if (result.getOrm() != null) {
            hints.add("ORM: " + result.getOrm());
        }
        if (result.getMigrationToolsDetected() != null && !result.getMigrationToolsDetected().isEmpty()) {
            hints.add("Migrations: " + String.join(", ", result.getMigrationToolsDetected()));
        }
        metadata.setDataLayerHints(hints);
    }
    
    private void aggregateApiResult(ProjectMetadata metadata, ApiAnalysisResult result) {
        metadata.setApiDetected(result.getEndpointCount() > 0);
        metadata.setApiRoutes(result.getRoutes());
    }
    
    /**
     * Generate a comprehensive summary including all key metrics.
     */
    private void generateSummary(ProjectMetadata metadata) {
        StringBuilder summary = new StringBuilder();
        
        // Project name
        if (metadata.getProjectRootName() != null && !metadata.getProjectRootName().isEmpty()) {
            summary.append("Project: ").append(metadata.getProjectRootName());
        }
        
        // Language
        if (metadata.getLanguages() != null && !metadata.getLanguages().isEmpty()) {
            summary.append(" | Language: ").append(metadata.getLanguages().get(0));
        }
        
        // Framework
        if (metadata.getFrameworks() != null && !metadata.getFrameworks().isEmpty()) {
            summary.append(" | Framework: ").append(metadata.getFrameworks().get(0));
        }
        
        // Build tool
        if (metadata.getBuildInfo() != null && metadata.getBuildInfo().getBuildTool() != null) {
            summary.append(" | Build: ").append(metadata.getBuildInfo().getBuildTool());
        }
        
        // Project size
        if (metadata.getProjectSize() != null) {
            summary.append(" | Size: ")
                .append(metadata.getProjectSize().getTotalSizeMB())
                .append(" MB (")
                .append(metadata.getProjectSize().getTotalFileCount())
                .append(" files)");
        }
        
        // Entry points
        if (metadata.getEntryPoints() != null && !metadata.getEntryPoints().isEmpty()) {
            summary.append(" | Entry Points: ").append(metadata.getEntryPoints().size());
        }
        
        // APIs
        if (metadata.isApiDetected()) {
            if (metadata.getApiRoutes() != null) {
                summary.append(" | API Endpoints: ").append(metadata.getApiRoutes().size());
            }
        }
        
        metadata.setSummary(summary.toString());
    }
}
