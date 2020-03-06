package com.example.authorizationserver.user.api.resource;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class CreateUserResource extends UserResource {

  @NotBlank
  @Size(max = 200)
  private String password;

  public CreateUserResource() {}

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
