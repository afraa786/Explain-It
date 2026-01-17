package io.explainit.analyzer;

import io.explainit.dto.AnalysisResult;
import io.explainit.dto.ProjectStructure;
import io.explainit.util.FileScanner;
import java.nio.file.Path;

/**
 * Analyzes project structure: directory layout, file counts, sizes, and organization.
 */
public class ProjectStructureAnalyzer implements IProjectAnalyzer {
    
    @Override
    public AnalysisResult analyze(Path projectRoot) throws Exception {
        AnalysisResult result = new AnalysisResult("ProjectStructure");
        
        ProjectStructure structure = new ProjectStructure();
        
        // Detect standard source directory
        String sourceDir = "src/main/java";
        if (projectRoot.resolve(sourceDir).toFile().exists()) {
            structure.setSourceDirectory(sourceDir);
        } else if (projectRoot.resolve("src").toFile().exists()) {
            structure.setSourceDirectory("src");
        } else {
            structure.setSourceDirectory("unknown");
        }
        
        // Detect resources directory
        String resourcesDir = "src/main/resources";
        if (projectRoot.resolve(resourcesDir).toFile().exists()) {
            structure.setResourcesDirectory(resourcesDir);
        } else {
            structure.setResourcesDirectory("unknown");
        }
        
        // Detect test directory
        String testDir = "src/test/java";
        if (projectRoot.resolve(testDir).toFile().exists()) {
            structure.setTestDirectory(testDir);
        } else if (projectRoot.resolve("test").toFile().exists()) {
            structure.setTestDirectory("test");
        } else {
            structure.setTestDirectory("unknown");
        }
        
        // Count files and calculate metrics
        long javaClassCount = FileScanner.countFilesByExtension(projectRoot, "java");
        structure.setCurrentClasses((int) javaClassCount);
        
        // Calculate total files, size, and directory count
        long totalFiles = FileScanner.countAllFiles(projectRoot);
        structure.setFileCount((int) totalFiles);
        
        long totalSizeBytes = FileScanner.calculateTotalSize(projectRoot);
        double totalSizeMB = totalSizeBytes / (1024.0 * 1024.0);
        structure.setTotalSizeMB(Math.round(totalSizeMB * 100.0) / 100.0);
        
        long directoryCount = FileScanner.countDirectories(projectRoot);
        structure.setDirectories((int) directoryCount);
        
        structure.setRootPath(projectRoot.toString());
        
        result.setSuccess(true);
        return result;
    }
}
