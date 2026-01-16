package io.explainit.analyzer;

import io.explainit.dto.ProjectMetadata;
import java.nio.file.Path;

public interface IProjectAnalyzer {
    void analyze(Path projectRoot, ProjectMetadata metadata) throws Exception;
}
