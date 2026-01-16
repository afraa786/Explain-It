package io.explainit.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ConfigFile {
    @JsonProperty("file")
    private String file;
    
    @JsonProperty("type")
    private String type;
    
    @JsonProperty("purpose")
    private String purpose;

    public ConfigFile() {
    }

    public ConfigFile(String file, String type, String purpose) {
        this.file = file;
        this.type = type;
        this.purpose = purpose;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }
}
