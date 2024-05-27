package com.example.backend;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DeviceRecord {

    @JsonProperty("deviceid")
    private String deviceId;

    @JsonProperty("last_updated")
    private String lastUpdated;

    @JsonProperty("epoch_data")
    private long epochData;

    @JsonProperty("epoch_stored")
    private long epochStored;

    @JsonProperty("latitude")
    private double latitude;

    @JsonProperty("longitude")
    private double longitude;

    // Getters and setters for all fields

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public long getEpochData() {
        return epochData;
    }

    public void setEpochData(long epochData) {
        this.epochData = epochData;
    }

    public long getEpochStored() {
        return epochStored;
    }

    public void setEpochStored(long epochStored) {
        this.epochStored = epochStored;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
