package io.explainit.analyzer;

import io.explainit.dto.AnalysisResult;
import java.nio.file.Path;

/**
 * Base interface for all project analyzers.
 * Each analyzer is responsible for a specific concern and returns a strongly-typed AnalysisResult.
 */
public interface IProjectAnalyzer {
    /**
     * Analyze a project and return structured results.
     * 
     * @param projectRoot Path to the project root directory
     * @return AnalysisResult containing analyzer-specific structured data
     * @throws Exception if analysis fails
     */
    AnalysisResult analyze(Path projectRoot) throws Exception;
}
