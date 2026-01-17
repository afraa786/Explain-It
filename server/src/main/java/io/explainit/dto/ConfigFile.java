package io.explainit.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    // ===== EXISTING GETTERS =====

    public String getFile() {
        return file;
    }

    public String getType() {
        return type;
    }

    public String getPurpose() {
        return purpose;
    }


    @JsonIgnore
    public String getFilename() {
        return file;
    }
}
