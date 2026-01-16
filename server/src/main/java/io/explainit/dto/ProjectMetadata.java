package io.explainit.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class ProjectMetadata {
    @JsonProperty("projectType")
    private String projectType;
    
    @JsonProperty("languages")
    private List<String> languages;
    
    @JsonProperty("frameworks")
    private List<String> frameworks;
    
    @JsonProperty("entryPoints")
    private List<EntryPoint> entryPoints;
    
    @JsonProperty("configFiles")
    private List<ConfigFile> configFiles;
    
    @JsonProperty("apiDetected")
    private boolean apiDetected;
    
    @JsonProperty("apiRoutes")
    private List<ApiRoute> apiRoutes;
    
    @JsonProperty("dataLayerHints")
    private List<String> dataLayerHints;
    
    @JsonProperty("securityHints")
    private List<String> securityHints;
    
    @JsonProperty("buildInfo")
    private BuildInfo buildInfo;
    
    @JsonProperty("projectStructure")
    private ProjectStructure projectStructure;
    
    @JsonProperty("summary")
    private String summary;

    public ProjectMetadata() {
    }

    public String getProjectType() {
        return projectType;
    }

    public void setProjectType(String projectType) {
        this.projectType = projectType;
    }

    public List<String> getLanguages() {
        return languages;
    }

    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }

    public List<String> getFrameworks() {
        return frameworks;
    }

    public void setFrameworks(List<String> frameworks) {
        this.frameworks = frameworks;
    }

    public List<EntryPoint> getEntryPoints() {
        return entryPoints;
    }

    public void setEntryPoints(List<EntryPoint> entryPoints) {
        this.entryPoints = entryPoints;
    }

    public List<ConfigFile> getConfigFiles() {
        return configFiles;
    }

    public void setConfigFiles(List<ConfigFile> configFiles) {
        this.configFiles = configFiles;
    }

    public boolean isApiDetected() {
        return apiDetected;
    }

    public void setApiDetected(boolean apiDetected) {
        this.apiDetected = apiDetected;
    }

    public List<ApiRoute> getApiRoutes() {
        return apiRoutes;
    }

    public void setApiRoutes(List<ApiRoute> apiRoutes) {
        this.apiRoutes = apiRoutes;
    }

    public List<String> getDataLayerHints() {
        return dataLayerHints;
    }

    public void setDataLayerHints(List<String> dataLayerHints) {
        this.dataLayerHints = dataLayerHints;
    }

    public List<String> getSecurityHints() {
        return securityHints;
    }

    public void setSecurityHints(List<String> securityHints) {
        this.securityHints = securityHints;
    }

    public BuildInfo getBuildInfo() {
        return buildInfo;
    }

    public void setBuildInfo(BuildInfo buildInfo) {
        this.buildInfo = buildInfo;
    }

    public ProjectStructure getProjectStructure() {
        return projectStructure;
    }

    public void setProjectStructure(ProjectStructure projectStructure) {
        this.projectStructure = projectStructure;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}
