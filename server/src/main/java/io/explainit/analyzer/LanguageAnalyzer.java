package io.explainit.analyzer;

import io.explainit.dto.AnalysisResult;
import io.explainit.util.FileScanner;
import java.nio.file.Path;
import java.util.*;

/**
 * Detects programming languages based on file extensions.
 * Note: Framework detection is handled by FrameworkAnalyzer which is more comprehensive.
 */
public class LanguageAnalyzer implements IProjectAnalyzer {
    
    private static final Map<String, String> EXTENSION_TO_LANGUAGE = Map.ofEntries(
        Map.entry("java", "Java"),
        Map.entry("kt", "Kotlin"),
        Map.entry("py", "Python"),
        Map.entry("js", "JavaScript"),
        Map.entry("ts", "TypeScript"),
        Map.entry("go", "Go"),
        Map.entry("rs", "Rust"),
        Map.entry("cs", "C#"),
        Map.entry("rb", "Ruby"),
        Map.entry("php", "PHP")
    );
    
    @Override
    public AnalysisResult analyze(Path projectRoot) throws Exception {
        AnalysisResult result = new AnalysisResult("Language");
        
        Set<String> extensions = FileScanner.getAllFileExtensions(projectRoot);
        Set<String> languages = new LinkedHashSet<>();
        
        for (String ext : extensions) {
            if (EXTENSION_TO_LANGUAGE.containsKey(ext)) {
                languages.add(EXTENSION_TO_LANGUAGE.get(ext));
            }
        }
        
        result.setSuccess(true);
        return result;
    }
}
