package com.example.backend;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;


@Entity
public class User {
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
}
