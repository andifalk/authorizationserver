package com.example.authorizationserver.token.store.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

@Entity
@DiscriminatorValue("jwt")
public class JsonWebToken extends Token {

  @NotNull
  private boolean accessToken;

  public boolean isAccessToken() {
    return accessToken;
  }

  public void setAccessToken(boolean idToken) {
    this.accessToken = idToken;
  }

  @Override
  public boolean isReferenceToken() {
    return false;
  }

  @Override
  public String toString() {
    return "JsonWebToken{" +
            "idToken=" + accessToken +
            "} " + super.toString();
  }
}
