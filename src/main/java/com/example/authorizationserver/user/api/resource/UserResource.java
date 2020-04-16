package com.example.authorizationserver.user.api.resource;

import com.example.authorizationserver.user.model.Gender;
import com.example.authorizationserver.user.model.User;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class UserResource {

  private UUID identifier;

  @NotNull private Gender gender;

  @NotBlank
  @Size(max = 100)
  private String firstName;

  @NotBlank
  @Size(max = 100)
  private String lastName;

  @NotNull @Email private String email;

  @NotBlank
  @Size(max = 50)
  private String username;

  @NotNull
  @Size(max = 100)
  private String phone;

  @NotNull @NotEmpty private Set<String> groups = new HashSet<>();

  @NotNull private AddressResource address;

  private LocalDateTime updatedAt;

  public UserResource() {}

  public UserResource(User user) {
    this.identifier = user.getIdentifier();
    this.address = new AddressResource(user.getAddress());
    this.email = user.getEmail();
    this.firstName = user.getFirstName();
    this.lastName = user.getLastName();
    this.username = user.getUsername();
    this.gender = user.getGender();
    this.groups = user.getGroups();
    this.phone = user.getPhone();
    this.updatedAt = user.getUpdatedAt();
  }

  public UUID getIdentifier() {
    return identifier;
  }

  public void setIdentifier(UUID identifier) {
    this.identifier = identifier;
  }

  public Gender getGender() {
    return gender;
  }

  public void setGender(Gender gender) {
    this.gender = gender;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getUsername() {
    return this.username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public Set<String> getGroups() {
    return groups;
  }

  public void setGroups(Set<String> groups) {
    this.groups = groups;
  }

  public AddressResource getAddress() {
    return address;
  }

  public void setAddress(AddressResource address) {
    this.address = address;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

  @Override
  public String toString() {
    return "UserResource{"
        + "identifier="
        + identifier
        + ", gender="
        + gender
        + ", firstName='"
        + firstName
        + '\''
        + ", lastName='"
        + lastName
        + '\''
        + ", email='"
        + email
        + '\''
        + ", username='"
        + username
        + '\''
        + ", phone='"
        + phone
        + '\''
        + ", groups="
        + groups
        + ", address="
        + address
        + ", updatedAt="
        + updatedAt
        + '}';
  }
}
