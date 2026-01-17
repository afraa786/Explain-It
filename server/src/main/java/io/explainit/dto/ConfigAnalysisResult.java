package io.explainit.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Result from ConfigFileAnalyzer with classified configs and complexity scoring.
 */
public class ConfigAnalysisResult extends AnalysisResult {
    @JsonProperty("totalConfigFiles")
    private int totalConfigFiles;
    
    @JsonProperty("configComplexity")
    private String configComplexity; // LOW, MEDIUM, HIGH
    
    @JsonProperty("runtimeConfigs")
    private List<ConfigFile> runtimeConfigs;
    
    @JsonProperty("securityConfigs")
    private List<ConfigFile> securityConfigs;
    
    @JsonProperty("buildConfigs")
    private List<ConfigFile> buildConfigs;
    
    @JsonProperty("infrastructureConfigs")
    private List<ConfigFile> infrastructureConfigs;
    
    public ConfigAnalysisResult() {
        super("Config");
    }
    
    public int getTotalConfigFiles() {
        return totalConfigFiles;
    }
    
    public void setTotalConfigFiles(int totalConfigFiles) {
        this.totalConfigFiles = totalConfigFiles;
    }
    
    public String getConfigComplexity() {
        return configComplexity;
    }
    
    public void setConfigComplexity(String configComplexity) {
        this.configComplexity = configComplexity;
    }
    
    public List<ConfigFile> getRuntimeConfigs() {
        return runtimeConfigs;
    }
    
    public void setRuntimeConfigs(List<ConfigFile> runtimeConfigs) {
        this.runtimeConfigs = runtimeConfigs;
    }
    
    public List<ConfigFile> getSecurityConfigs() {
        return securityConfigs;
    }
    
    public void setSecurityConfigs(List<ConfigFile> securityConfigs) {
        this.securityConfigs = securityConfigs;
    }
    
    public List<ConfigFile> getBuildConfigs() {
        return buildConfigs;
    }
    
    public void setBuildConfigs(List<ConfigFile> buildConfigs) {
        this.buildConfigs = buildConfigs;
    }
    
    public List<ConfigFile> getInfrastructureConfigs() {
        return infrastructureConfigs;
    }
    
    public void setInfrastructureConfigs(List<ConfigFile> infrastructureConfigs) {
        this.infrastructureConfigs = infrastructureConfigs;
    }
}
