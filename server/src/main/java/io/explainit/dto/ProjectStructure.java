package io.explainit.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProjectStructure {
    @JsonProperty("sourceDirectory")
    private String sourceDirectory;
    
    @JsonProperty("resourcesDirectory")
    private String resourcesDirectory;
    
    @JsonProperty("testDirectory")
    private String testDirectory;
    
    @JsonProperty("currentClasses")
    private int currentClasses;

    public ProjectStructure() {
    }

    public ProjectStructure(String sourceDirectory, String resourcesDirectory, String testDirectory, int currentClasses) {
        this.sourceDirectory = sourceDirectory;
        this.resourcesDirectory = resourcesDirectory;
        this.testDirectory = testDirectory;
        this.currentClasses = currentClasses;
    }

    public String getSourceDirectory() {
        return sourceDirectory;
    }

    public void setSourceDirectory(String sourceDirectory) {
        this.sourceDirectory = sourceDirectory;
    }

    public String getResourcesDirectory() {
        return resourcesDirectory;
    }

    public void setResourcesDirectory(String resourcesDirectory) {
        this.resourcesDirectory = resourcesDirectory;
    }

    public String getTestDirectory() {
        return testDirectory;
    }

    public void setTestDirectory(String testDirectory) {
        this.testDirectory = testDirectory;
    }

    public int getCurrentClasses() {
        return currentClasses;
    }

    public void setCurrentClasses(int currentClasses) {
        this.currentClasses = currentClasses;
    }
}
