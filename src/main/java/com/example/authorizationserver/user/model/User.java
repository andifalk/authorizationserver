package com.example.authorizationserver.user.model;

import com.example.authorizationserver.user.api.resource.CreateUserResource;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToOne;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static javax.persistence.FetchType.EAGER;

@Entity
public class User extends AbstractPersistable<Long> {

  @NotNull private UUID identifier;

  @NotNull
  @Enumerated(EnumType.STRING)
  private Gender gender;

  @NotBlank
  @Size(max = 100)
  private String firstName;

  @NotBlank
  @Size(max = 100)
  private String lastName;

  @NotBlank
  @Size(max = 200)
  private String password;

  @NotNull @Email private String email;

  @NotBlank
  @Size(max = 100)
  @Column(unique = true)
  private String username;

  @NotNull
  @Size(max = 100)
  private String phone;

  @ElementCollection(fetch = EAGER)
  private Set<String> groups = new HashSet<>();

  @NotNull
  @OneToOne(optional = false, cascade = CascadeType.ALL, fetch = EAGER)
  private Address address;

  @NotNull
  private LocalDateTime updatedAt;

  public User() {}

  public User(
      UUID identifier,
      Gender gender,
      String firstName,
      String lastName,
      String password,
      String email,
      String username,
      String phone,
      Set<String> groups,
      Address address,
      LocalDateTime updatedAt) {
    this.identifier = identifier;
    this.gender = gender;
    this.firstName = firstName;
    this.lastName = lastName;
    this.password = password;
    this.email = email;
    this.username = username;
    this.phone = phone;
    this.groups = groups;
    this.address = address;
    this.updatedAt = updatedAt;
  }

  public User(CreateUserResource userResource) {
    this.identifier = userResource.getIdentifier();
    this.address = new Address(userResource.getAddress());
    this.email = userResource.getEmail();
    this.firstName = userResource.getFirstName();
    this.lastName = userResource.getLastName();
    this.username = userResource.getUsername();
    this.gender = userResource.getGender();
    this.groups = userResource.getGroups();
    this.phone = userResource.getPhone();
    this.password = userResource.getPassword();
    this.updatedAt = userResource.getUpdatedAt();
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

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
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

  public Address getAddress() {
    return address;
  }

  public void setAddress(Address address) {
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
    return "User{"
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
        + ", password='"
        + password
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
