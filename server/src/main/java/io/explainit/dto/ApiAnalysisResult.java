package io.explainit.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

/**
 * Result from ApiAnalyzer with grouped endpoints and maturity level.
 */
public class ApiAnalysisResult extends AnalysisResult {
    @JsonProperty("controllerCount")
    private int controllerCount;
    
    @JsonProperty("endpointCount")
    private int endpointCount;
    
    @JsonProperty("endpointsByMethod")
    private Map<String, Integer> endpointsByMethod;
    
    @JsonProperty("securedEndpoints")
    private int securedEndpoints;
    
    @JsonProperty("publicEndpoints")
    private int publicEndpoints;
    
    @JsonProperty("restMaturityLevel")
    private String restMaturityLevel; // BASIC_CRUD, LAYERED, HYPERMEDIA, HATEOAS
    
    @JsonProperty("routes")
    private List<ApiRoute> routes;
    
    public ApiAnalysisResult() {
        super("API");
    }
    
    public int getControllerCount() {
        return controllerCount;
    }
    
    public void setControllerCount(int controllerCount) {
        this.controllerCount = controllerCount;
    }
    
    public int getEndpointCount() {
        return endpointCount;
    }
    
    public void setEndpointCount(int endpointCount) {
        this.endpointCount = endpointCount;
    }
    
    public Map<String, Integer> getEndpointsByMethod() {
        return endpointsByMethod;
    }
    
    public void setEndpointsByMethod(Map<String, Integer> endpointsByMethod) {
        this.endpointsByMethod = endpointsByMethod;
    }
    
    public int getSecuredEndpoints() {
        return securedEndpoints;
    }
    
    public void setSecuredEndpoints(int securedEndpoints) {
        this.securedEndpoints = securedEndpoints;
    }
    
    public int getPublicEndpoints() {
        return publicEndpoints;
    }
    
    public void setPublicEndpoints(int publicEndpoints) {
        this.publicEndpoints = publicEndpoints;
    }
    
    public String getRestMaturityLevel() {
        return restMaturityLevel;
    }
    
    public void setRestMaturityLevel(String restMaturityLevel) {
        this.restMaturityLevel = restMaturityLevel;
    }
    
    public List<ApiRoute> getRoutes() {
        return routes;
    }
    
    public void setRoutes(List<ApiRoute> routes) {
        this.routes = routes;
    }
}
