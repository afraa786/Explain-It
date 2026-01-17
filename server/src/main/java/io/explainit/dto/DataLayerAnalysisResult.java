package io.explainit.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Result from DataLayerAnalyzer with structured database and ORM information.
 */
public class DataLayerAnalysisResult extends AnalysisResult {
    @JsonProperty("database")
    private String database;
    
    @JsonProperty("orm")
    private String orm;
    
    @JsonProperty("entityCount")
    private int entityCount;
    
    @JsonProperty("repositoryCount")
    private int repositoryCount;
    
    @JsonProperty("migrationToolsDetected")
    private List<String> migrationToolsDetected;
    
    @JsonProperty("connectionPooling")
    private String connectionPooling;
    
    @JsonProperty("detections")
    private List<DetectionResult> detections;
    
    public DataLayerAnalysisResult() {
        super("DataLayer");
    }
    
    public String getDatabase() {
        return database;
    }
    
    public void setDatabase(String database) {
        this.database = database;
    }
    
    public String getOrm() {
        return orm;
    }
    
    public void setOrm(String orm) {
        this.orm = orm;
    }
    
    public int getEntityCount() {
        return entityCount;
    }
    
    public void setEntityCount(int entityCount) {
        this.entityCount = entityCount;
    }
    
    public int getRepositoryCount() {
        return repositoryCount;
    }
    
    public void setRepositoryCount(int repositoryCount) {
        this.repositoryCount = repositoryCount;
    }
    
    public List<String> getMigrationToolsDetected() {
        return migrationToolsDetected;
    }
    
    public void setMigrationToolsDetected(List<String> migrationToolsDetected) {
        this.migrationToolsDetected = migrationToolsDetected;
    }
    
    public String getConnectionPooling() {
        return connectionPooling;
    }
    
    public void setConnectionPooling(String connectionPooling) {
        this.connectionPooling = connectionPooling;
    }
    
    public List<DetectionResult> getDetections() {
        return detections;
    }
    
    public void setDetections(List<DetectionResult> detections) {
        this.detections = detections;
    }
}
