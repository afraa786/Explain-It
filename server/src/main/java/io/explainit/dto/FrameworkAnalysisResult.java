package io.explainit.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Result from FrameworkAnalyzer with enriched framework information.
 */
public class FrameworkAnalysisResult extends AnalysisResult {
    @JsonProperty("primaryLanguage")
    private String primaryLanguage;
    
    @JsonProperty("primaryFramework")
    private String primaryFramework;
    
    @JsonProperty("primaryFrameworkVersion")
    private String primaryFrameworkVersion;
    
    @JsonProperty("confidence")
    private String confidence; // HIGH, MEDIUM, LOW
    
    @JsonProperty("ecosystemType")
    private String ecosystemType;
    
    @JsonProperty("allDetections")
    private List<FrameworkDetectionResult> allDetections;
    
    public FrameworkAnalysisResult() {
        super("Framework");
    }
    
    public String getPrimaryLanguage() {
        return primaryLanguage;
    }
    
    public void setPrimaryLanguage(String primaryLanguage) {
        this.primaryLanguage = primaryLanguage;
    }
    
    public String getPrimaryFramework() {
        return primaryFramework;
    }
    
    public void setPrimaryFramework(String primaryFramework) {
        this.primaryFramework = primaryFramework;
    }
    
    public String getPrimaryFrameworkVersion() {
        return primaryFrameworkVersion;
    }
    
    public void setPrimaryFrameworkVersion(String primaryFrameworkVersion) {
        this.primaryFrameworkVersion = primaryFrameworkVersion;
    }
    
    public String getConfidence() {
        return confidence;
    }
    
    public void setConfidence(String confidence) {
        this.confidence = confidence;
    }
    
    public String getEcosystemType() {
        return ecosystemType;
    }
    
    public void setEcosystemType(String ecosystemType) {
        this.ecosystemType = ecosystemType;
    }
    
    public List<FrameworkDetectionResult> getAllDetections() {
        return allDetections;
    }
    
    public void setAllDetections(List<FrameworkDetectionResult> allDetections) {
        this.allDetections = allDetections;
    }
}
