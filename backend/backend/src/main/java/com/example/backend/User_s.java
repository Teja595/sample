package com.example.backend;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.Objects;
import jakarta.persistence.Table;


@Entity
@Table(name = "sample")
public class User_s {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

private String deviceId;
private Long epochData;
private Long epochStored;
private Double latitude;
private Double longitude;
// Example new fields
private Double delta_distance; 
private Double delta_t;
private Double speed;
private Double mov_avg_spd;
public User_s(){

}

  public Double getMov_avg_spd() {
    return this.mov_avg_spd;
  }

  public void setMov_avg_spd(Double mov_avg_spd) {
    this.mov_avg_spd = mov_avg_spd;
  }

  public Long getId() {
    return this.id;
  }

 

  public Double getDelta_distance() {
    return this.delta_distance;
  }

  public void setDelta_distance(Double delta_distance) {
    this.delta_distance = delta_distance;
  }

  public Double getDelta_t() {
    return this.delta_t;
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
      ", delta_distance='" + getDelta_distance() + "'" +
      ", delta_t='" + getDelta_t() + "'" +
      ", speed='" + getSpeed() + "'" +
      ", mov_avg_spd='" + getMov_avg_spd() + "'" +
      "}";
  }
 

  public void setDelta_t(Double delta_t) {
    this.delta_t = delta_t;
  }

  public Double getSpeed() {
    return this.speed;
  }

  public void setSpeed(Double speed) {
    this.speed = speed;
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