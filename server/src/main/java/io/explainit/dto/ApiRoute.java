package io.explainit.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ApiRoute {
    @JsonProperty("method")
    private String method;
    
    @JsonProperty("path")
    private String path;
    
    @JsonProperty("handler")
    private String handler;

    public ApiRoute() {
    }

    public ApiRoute(String method, String path, String handler) {
        this.method = method;
        this.path = path;
        this.handler = handler;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getHandler() {
        return handler;
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }
}
