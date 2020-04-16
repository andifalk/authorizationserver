package com.example.authorizationserver.user.api.resource;

import com.example.authorizationserver.user.model.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class CreateUserResource extends UserResource {

  @NotBlank
  @Size(max = 200)
  private String password;

  public CreateUserResource() {}

  public CreateUserResource(User user) {
    setAddress(new AddressResource(user.getAddress()));
    setEmail(user.getEmail());
    setFirstName(user.getFirstName());
    setLastName(user.getLastName());
    setUsername(user.getUsername());
    setGender(user.getGender());
    setGroups(user.getGroups());
    setPhone(user.getPhone());
    setPassword(user.getPassword());
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
