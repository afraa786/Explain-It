package io.explainit.service;

import io.explainit.analyzer.*;
import io.explainit.dto.ProjectMetadata;
import org.springframework.stereotype.Service;
import java.nio.file.Path;

/**
 * Service layer for project analysis.
 * Delegates to ProjectAnalysisOrchestrator which manages all analyzers.
 */
@Service
public class ProjectAnalysisService {
    
    private final ProjectAnalysisOrchestrator orchestrator = new ProjectAnalysisOrchestrator();
    
    /**
     * Analyze a project and return comprehensive metadata.
     * Uses the ProjectAnalysisOrchestrator to invoke all analyzers
     * and aggregate their results into a single ProjectMetadata object.
     *
     * @param projectRoot The root path of the project
     * @return Complete ProjectMetadata with all analysis results
     * @throws Exception if analysis fails
     */
    public ProjectMetadata analyzeProject(Path projectRoot) throws Exception {
        return orchestrator.analyzeProject(projectRoot);
    }
}
