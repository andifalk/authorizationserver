package com.example.authorizationserver.oauth.endpoint.resource;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

public class UserForm implements Serializable {

  @NotBlank
  @Size(max = 100)
  @Column(unique = true)
  private String username;

  @NotBlank
  @Size(max = 200)
  private String password;

  public UserForm() {}

  public UserForm(
      @NotBlank @Size(max = 100) String username, @NotBlank @Size(max = 200) String password) {
    this.username = username;
    this.password = password;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  @Override
  public String toString() {
    return "UserForm{" + "username='" + username + '\'' + ", password='*****'" + '}';
  }
}
