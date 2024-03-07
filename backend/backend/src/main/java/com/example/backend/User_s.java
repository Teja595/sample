package com.example.backend;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.Objects;
import jakarta.persistence.Table;


@Entity
@Table(name = "Geo")
public class User_s {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

private String deviceId;
private Long epochData;
private Long epochStored;
private Double latitude;
private Double longitude;
public User_s(){

}

  public Long getId() {
    return this.id;
  }

  @Override
  public String toString() {
    return "{" +
      " id='" + getId() + "'" +
      ", deviceId='" + getDeviceId() + "'" +
      ", epochData='" + getEpochData() + "'" +
      ", epochStored='" + getEpochStored() + "'" +
      ", latitude='" + getLatitude() + "'" +
      ", longitude='" + getLongitude() + "'" +
      "}";
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getDeviceId() {
    return this.deviceId;
  }

  public void setDeviceId(String deviceId) {
    this.deviceId = deviceId;
  }

  public Long getEpochData() {
    return this.epochData;
  }

  public void setEpochData(Long epochData) {
    this.epochData = epochData;
  }

  public Long getEpochStored() {
    return this.epochStored;
  }

  public void setEpochStored(Long epochStored) {
    this.epochStored = epochStored;
  }
  public User_s(Long id, String deviceId, Long epochData, Long epochStored, Double latitude, Double longitude) {
    this.id = id;
    this.deviceId = deviceId;
    this.epochData = epochData;
    this.epochStored = epochStored;
    this.latitude = latitude;
    this.longitude = longitude;
  
  }

  public Double getLatitude() {
    return this.latitude;
  }

  public void setLatitude(Double latitude) {
    this.latitude = latitude;
  }

  public Double getLongitude() {
    return this.longitude;
  }

  public void setLongitude(Double longitude) {
    this.longitude = longitude;
  }

}