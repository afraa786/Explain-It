package io.explainit.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Represents a single detection with confidence level and supporting evidence.
 * This ensures all findings are trustworthy and explainable.
 */
public class DetectionResult {
    
    public enum Confidence {
        HIGH, MEDIUM, LOW
    }
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("type")
    private String type; // e.g., "Framework", "Database", "ORM", "Authentication"
    
    @JsonProperty("confidence")
    private Confidence confidence;
    
    @JsonProperty("reason")
    private String reason; // Why this was detected (human-readable explanation)
    
    @JsonProperty("evidence")
    private List<String> evidence; // Supporting signals that led to this detection
    
    @JsonProperty("version")
    private String version; // Optional version if detected
    
    public DetectionResult() {
    }
    
    public DetectionResult(String name, String type, Confidence confidence, String reason, List<String> evidence) {
        this.name = name;
        this.type = type;
        this.confidence = confidence;
        this.reason = reason;
        this.evidence = evidence;
    }
    
    public DetectionResult(String name, String type, Confidence confidence, String reason, List<String> evidence, String version) {
        this.name = name;
        this.type = type;
        this.confidence = confidence;
        this.reason = reason;
        this.evidence = evidence;
        this.version = version;
    }
    
    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Confidence getConfidence() {
        return confidence;
    }

    public void setConfidence(Confidence confidence) {
        this.confidence = confidence;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public List<String> getEvidence() {
        return evidence;
    }

    public void setEvidence(List<String> evidence) {
        this.evidence = evidence;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
