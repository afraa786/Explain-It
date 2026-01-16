package io.explainit.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EntryPoint {
    @JsonProperty("file")
    private String file;
    
    @JsonProperty("class")
    private String className;
    
    @JsonProperty("method")
    private String method;
    
    @JsonProperty("type")
    private String type;

    public EntryPoint() {
    }

    public EntryPoint(String file, String className, String method, String type) {
        this.file = file;
        this.className = className;
        this.method = method;
        this.type = type;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
