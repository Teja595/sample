package com.example.backend;
import java.util.*;
public class DeviceIdApiResponse {
    private ApiResponse apiresponse;
    private Metadata meta;
    private List<DeviceRecord> records;

    // Getters and setters

    public ApiResponse getApiresponse() {
        return apiresponse;
    }

    public void setApiresponse(ApiResponse apiresponse) {
        this.apiresponse = apiresponse;
    }

    public Metadata getMeta() {
        return meta;
    }

    public void setMeta(Metadata meta) {
        this.meta = meta;
    }

    public List<DeviceRecord> getRecords() {
        return records;
    }

    public void setRecords(List<DeviceRecord> records) {
        this.records = records;
    }
}
