package com.example.authorizationserver.user.model;

import com.example.authorizationserver.user.api.resource.AddressResource;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Entity;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
public class Address extends AbstractPersistable<Long> {

  @NotBlank
  @Size(max = 100)
  private String street;

  @NotBlank
  @Size(max = 20)
  private String zip;

  @NotBlank
  @Size(max = 100)
  private String city;

  @Size(max = 100)
  private String state;

  @NotBlank
  @Size(max = 100)
  private String country;

  public Address() {
  }

  public Address(String street, String zip, String city, String state, String country) {
    this.street = street;
    this.zip = zip;
    this.city = city;
    this.state = state;
    this.country = country;
  }

  public Address(AddressResource addressResource) {
    this.city = addressResource.getCity();
    this.country = addressResource.getCountry();
    this.state = addressResource.getState();
    this.street = addressResource.getStreet();
    this.zip = addressResource.getZip();
  }

  public String getStreet() {
    return street;
  }

  public void setStreet(String street) {
    this.street = street;
  }

  public String getZip() {
    return zip;
  }

  public void setZip(String zip) {
    this.zip = zip;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }
}
