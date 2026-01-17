package io.explainit.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Result of the framework detection process.
 * Provides primary language, detected frameworks, build system, and confidence scores.
 */
public class FrameworkDetectionResult {
    
    @JsonProperty("primaryLanguage")
    private String primaryLanguage;
    
    @JsonProperty("languageVersion")
    private String languageVersion;
    
    @JsonProperty("frameworks")
    private List<DetectionResult> frameworks;
    
    @JsonProperty("buildSystem")
    private DetectionResult buildSystem;
    
    @JsonProperty("allDetectedLanguages")
    private List<String> allDetectedLanguages;
    
    public FrameworkDetectionResult() {
    }
    
    public FrameworkDetectionResult(String primaryLanguage, String languageVersion, 
                                    List<DetectionResult> frameworks, DetectionResult buildSystem,
                                    List<String> allDetectedLanguages) {
        this.primaryLanguage = primaryLanguage;
        this.languageVersion = languageVersion;
        this.frameworks = frameworks;
        this.buildSystem = buildSystem;
        this.allDetectedLanguages = allDetectedLanguages;
    }
    
    // Getters and Setters
    public String getPrimaryLanguage() {
        return primaryLanguage;
    }

    public void setPrimaryLanguage(String primaryLanguage) {
        this.primaryLanguage = primaryLanguage;
    }

    public String getLanguageVersion() {
        return languageVersion;
    }

    public void setLanguageVersion(String languageVersion) {
        this.languageVersion = languageVersion;
    }

    public List<DetectionResult> getFrameworks() {
        return frameworks;
    }

    public void setFrameworks(List<DetectionResult> frameworks) {
        this.frameworks = frameworks;
    }

    public DetectionResult getBuildSystem() {
        return buildSystem;
    }

    public void setBuildSystem(DetectionResult buildSystem) {
        this.buildSystem = buildSystem;
    }

    public List<String> getAllDetectedLanguages() {
        return allDetectedLanguages;
    }

    public void setAllDetectedLanguages(List<String> allDetectedLanguages) {
        this.allDetectedLanguages = allDetectedLanguages;
    }
}
