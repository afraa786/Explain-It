package io.explainit.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Result from EntryPointAnalyzer with structured entry point data.
 */
public class EntryPointAnalysisResult extends AnalysisResult {
    @JsonProperty("primaryEntryPoint")
    private String primaryEntryPoint;
    
    @JsonProperty("entryType")
    private String entryType;
    
    @JsonProperty("secondaryEntryPoints")
    private List<EntryPoint> secondaryEntryPoints;
    
    @JsonProperty("totalEntryPoints")
    private int totalEntryPoints;
    
    public EntryPointAnalysisResult() {
        super("EntryPoint");
    }
    
    public String getPrimaryEntryPoint() {
        return primaryEntryPoint;
    }
    
    public void setPrimaryEntryPoint(String primaryEntryPoint) {
        this.primaryEntryPoint = primaryEntryPoint;
    }
    
    public String getEntryType() {
        return entryType;
    }
    
    public void setEntryType(String entryType) {
        this.entryType = entryType;
    }
    
    public List<EntryPoint> getSecondaryEntryPoints() {
        return secondaryEntryPoints;
    }
    
    public void setSecondaryEntryPoints(List<EntryPoint> secondaryEntryPoints) {
        this.secondaryEntryPoints = secondaryEntryPoints;
    }
    
    public int getTotalEntryPoints() {
        return totalEntryPoints;
    }
    
    public void setTotalEntryPoints(int totalEntryPoints) {
        this.totalEntryPoints = totalEntryPoints;
    }
}
