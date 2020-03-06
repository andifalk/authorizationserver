package com.example.authorizationserver.token.store.model;

import org.springframework.security.authentication.BadCredentialsException;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("opaque")
public class OpaqueToken extends Token {

  @NotBlank
  @Size(max = 200)
  private String subject;

  @NotBlank
  @Size(max = 200)
  private String clientId;

  public String getSubject() {
    return subject;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public String getClientId() {
    return clientId;
  }

  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

  @Override
  public boolean isReferenceToken() {
    return true;
  }

  public void validate() {
    if (LocalDateTime.now().isAfter(this.getExpiry())) {
      throw new BadCredentialsException("Expired");
    }
  }
}
