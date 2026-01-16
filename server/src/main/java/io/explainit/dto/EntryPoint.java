package io.explainit.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EntryPoint {
    @JsonProperty("file")
    private String file;
    
    @JsonProperty("filePath")
    private String filePath;
    
    @JsonProperty("class")
    private String className;
    
    @JsonProperty("method")
    private String method;
    
    @JsonProperty("methodName")
    private String methodName;
    
    @JsonProperty("type")
    private String type;

    public EntryPoint() {
    }

    public EntryPoint(String file, String className, String method, String type) {
        this.file = file;
        this.filePath = file;
        this.className = className;
        this.method = method;
        this.methodName = method;
        this.type = type;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
        this.filePath = file;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
        this.file = filePath;
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
        this.methodName = method;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
        this.method = methodName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
