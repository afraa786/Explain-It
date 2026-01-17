package io.explainit.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private String type; // Framework, Database, ORM, Security, etc.

    @JsonProperty("confidence")
    private Confidence confidence;

    @JsonProperty("reason")
    private String reason;

    @JsonProperty("evidence")
    private List<String> evidence;

    @JsonProperty("version")
    private String version;

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
        this(name, type, confidence, reason, evidence);
        this.version = version;
    }

    // ===== EXISTING GETTERS =====

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public Confidence getConfidence() {
        return confidence;
    }

    public String getReason() {
        return reason;
    }

    public List<String> getEvidence() {
        return evidence;
    }

    public String getVersion() {
        return version;
    }
    @JsonIgnore
    public String getCategory() {
        return type;
    }
}
