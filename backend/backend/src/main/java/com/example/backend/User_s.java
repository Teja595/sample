package com.example.backend;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.util.Objects;


@Entity
public class User_s {
  @Id
  private Long id;
  private String name;
  private String username;
  private String email;
  private String street;
  private String suite;
  private String city;
  private String zipcode;
  @Column(name = "geo_lat")
  private Double geoLat;
  @Column(name = "geo_lng")
  private Double geoLng;
  private String phone;
  private String website;
  @Column(name = "company_name")
  private String companyName;
  @Column(name = "company_catchPhrase")
  private String companyCatchPhrase;
  @Column(name = "company_bs")
  private String companyBs;

  // Getters and setters


  public Long getId() {
    return this.id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getUsername() {
    return this.username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getEmail() {
    return this.email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getStreet() {
    return this.street;
  }

  public void setStreet(String street) {
    this.street = street;
  }

  public String getSuite() {
    return this.suite;
  }

  public void setSuite(String suite) {
    this.suite = suite;
  }

  public String getCity() {
    return this.city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getZipcode() {
    return this.zipcode;
  }

  public void setZipcode(String zipcode) {
    this.zipcode = zipcode;
  }

  public Double getGeoLat() {
    return this.geoLat;
  }

  public void setGeoLat(Double geoLat) {
    this.geoLat = geoLat;
  }

  public Double getGeoLng() {
    return this.geoLng;
  }

  public void setGeoLng(Double geoLng) {
    this.geoLng = geoLng;
  }

  public String getPhone() {
    return this.phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getWebsite() {
    return this.website;
  }

  public void setWebsite(String website) {
    this.website = website;
  }

  public String getCompanyName() {
    return this.companyName;
  }

  public void setCompanyName(String companyName) {
    this.companyName = companyName;
  }

  public String getCompanyCatchPhrase() {
    return this.companyCatchPhrase;
  }

  public void setCompanyCatchPhrase(String companyCatchPhrase) {
    this.companyCatchPhrase = companyCatchPhrase;
  }

  public String getCompanyBs() {
    return this.companyBs;
  }

  public void setCompanyBs(String companyBs) {
    this.companyBs = companyBs;
  }
}