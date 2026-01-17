package io.explainit.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Result from BuildInfoAnalyzer with project size, dependency count, and multi-module info.
 */
public class BuildAnalysisResult extends AnalysisResult {
    @JsonProperty("buildTool")
    private String buildTool;
    
    @JsonProperty("projectSizeKB")
    private long projectSizeKB;
    
    @JsonProperty("projectSizeMB")
    private double projectSizeMB;
    
    @JsonProperty("totalFileCount")
    private long totalFileCount;
    
    @JsonProperty("dependencyCount")
    private int dependencyCount;
    
    @JsonProperty("multiModule")
    private boolean multiModule;
    
    @JsonProperty("moduleCount")
    private int moduleCount;
    
    @JsonProperty("javaVersion")
    private String javaVersion;
    
    @JsonProperty("languageVersion")
    private String languageVersion;
    
    public BuildAnalysisResult() {
        super("Build");
    }
    
    public String getBuildTool() {
        return buildTool;
    }
    
    public void setBuildTool(String buildTool) {
        this.buildTool = buildTool;
    }
    
    public long getProjectSizeKB() {
        return projectSizeKB;
    }
    
    public void setProjectSizeKB(long projectSizeKB) {
        this.projectSizeKB = projectSizeKB;
    }
    
    public double getProjectSizeMB() {
        return projectSizeMB;
    }
    
    public void setProjectSizeMB(double projectSizeMB) {
        this.projectSizeMB = projectSizeMB;
    }
    
    public long getTotalFileCount() {
        return totalFileCount;
    }
    
    public void setTotalFileCount(long totalFileCount) {
        this.totalFileCount = totalFileCount;
    }
    
    public int getDependencyCount() {
        return dependencyCount;
    }
    
    public void setDependencyCount(int dependencyCount) {
        this.dependencyCount = dependencyCount;
    }
    
    public boolean isMultiModule() {
        return multiModule;
    }
    
    public void setMultiModule(boolean multiModule) {
        this.multiModule = multiModule;
    }
    
    public int getModuleCount() {
        return moduleCount;
    }
    
    public void setModuleCount(int moduleCount) {
        this.moduleCount = moduleCount;
    }
    
    public String getJavaVersion() {
        return javaVersion;
    }
    
    public void setJavaVersion(String javaVersion) {
        this.javaVersion = javaVersion;
    }
    
    public String getLanguageVersion() {
        return languageVersion;
    }
    
    public void setLanguageVersion(String languageVersion) {
        this.languageVersion = languageVersion;
    }
}
