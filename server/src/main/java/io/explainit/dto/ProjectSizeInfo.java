package io.explainit.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProjectSizeInfo {
    
    @JsonProperty("totalSizeBytes")
    private long totalSizeBytes;
    
    @JsonProperty("totalSizeMB")
    private double totalSizeMB;
    
    @JsonProperty("totalFileCount")
    private long totalFileCount;
    
    @JsonProperty("excludedDirs")
    private String[] excludedDirs;
    
    public ProjectSizeInfo() {
    }
    
    public ProjectSizeInfo(long totalSizeBytes, long totalFileCount) {
        this.totalSizeBytes = totalSizeBytes;
        this.totalSizeMB = Math.round((totalSizeBytes / (1024.0 * 1024.0)) * 100.0) / 100.0; // 2 decimal places
        this.totalFileCount = totalFileCount;
        this.excludedDirs = new String[]{".git", "node_modules", "target", "build", ".venv", "dist", ".next", "__pycache__"};
    }
    
    // Getters and Setters
    public long getTotalSizeBytes() {
        return totalSizeBytes;
    }

    public void setTotalSizeBytes(long totalSizeBytes) {
        this.totalSizeBytes = totalSizeBytes;
        this.totalSizeMB = Math.round((totalSizeBytes / (1024.0 * 1024.0)) * 100.0) / 100.0;
    }

    public double getTotalSizeMB() {
        return totalSizeMB;
    }

    public void setTotalSizeMB(double totalSizeMB) {
        this.totalSizeMB = Math.round(totalSizeMB * 100.0) / 100.0;
    }

    public long getTotalFileCount() {
        return totalFileCount;
    }

    public void setTotalFileCount(long totalFileCount) {
        this.totalFileCount = totalFileCount;
    }

    public String[] getExcludedDirs() {
        return excludedDirs;
    }

    public void setExcludedDirs(String[] excludedDirs) {
        this.excludedDirs = excludedDirs;
    }
}
