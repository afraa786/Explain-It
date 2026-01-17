package io.explainit.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Base class for all analyzer results.
 * Provides a common contract for all analyzers.
 */
public class AnalysisResult {
    @JsonProperty("analyzerType")
    protected String analyzerType;
    
    @JsonProperty("success")
    protected boolean success;
    
    @JsonProperty("message")
    protected String message;
    
    public AnalysisResult() {
        this.success = true;
    }
    
    public AnalysisResult(String analyzerType) {
        this.analyzerType = analyzerType;
        this.success = true;
    }
    
    public String getAnalyzerType() {
        return analyzerType;
    }
    
    public void setAnalyzerType(String analyzerType) {
        this.analyzerType = analyzerType;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
}
