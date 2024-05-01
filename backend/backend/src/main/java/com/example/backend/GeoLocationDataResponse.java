package com.example.backend;
import java.util.*;
public class GeoLocationDataResponse {
    private ApiResponse apiresponse;
    private Metadata meta;
    private List<DeviceRecord> records;

    public ApiResponse getApiresponse() {
        return this.apiresponse;
    }

    public void setApiresponse(ApiResponse apiresponse) {
        this.apiresponse = apiresponse;
    }

    public Metadata getMeta() {
        return this.meta;
    }

    public void setMeta(Metadata meta) {
        this.meta = meta;
    }

    public List<DeviceRecord> getRecords() {
        return this.records;
    }

    public void setRecords(List<DeviceRecord> records) {
        this.records = records;
    }

    // Getters and setters

    public boolean hasRecords() {
        return records != null && !records.isEmpty();
    }
}
