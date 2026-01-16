package io.explainit.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

public class BuildInfo {
    @JsonProperty("buildTool")
    private String buildTool;
    
    @JsonProperty("javaVersion")
    private String javaVersion;
    
    @JsonProperty("springBootVersion")
    private String springBootVersion;
    
    @JsonProperty("dependencies")
    private List<String> dependencies = new ArrayList<>();

    public BuildInfo() {
    }

    public BuildInfo(String buildTool, String javaVersion, String springBootVersion) {
        this.buildTool = buildTool;
        this.javaVersion = javaVersion;
        this.springBootVersion = springBootVersion;
    }

    public String getBuildTool() {
        return buildTool;
    }

    public void setBuildTool(String buildTool) {
        this.buildTool = buildTool;
    }

    public String getJavaVersion() {
        return javaVersion;
    }

    public void setJavaVersion(String javaVersion) {
        this.javaVersion = javaVersion;
    }

    public String getSpringBootVersion() {
        return springBootVersion;
    }

    public void setSpringBootVersion(String springBootVersion) {
        this.springBootVersion = springBootVersion;
    }
    
    public List<String> getDependencies() {
        return dependencies;
    }
    
    public void setDependencies(List<String> dependencies) {
        this.dependencies = dependencies;
    }
}
