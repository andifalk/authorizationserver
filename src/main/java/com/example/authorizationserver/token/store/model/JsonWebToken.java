package com.example.authorizationserver.token.store.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("jwt")
public class JsonWebToken extends Token {

  @Override
  public boolean isReferenceToken() {
    return false;
  }
}
