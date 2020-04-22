package com.example.authorizationserver.token.store.model;

import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@MappedSuperclass
public abstract class Token extends AbstractPersistable<Long> {

  @NotBlank
  @Size(max = 2000)
  private String value;

  @NotNull private LocalDateTime expiry;

  private boolean revoked;

  public Token() {}

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public LocalDateTime getExpiry() {
    return expiry;
  }

  public void setExpiry(LocalDateTime expiry) {
    this.expiry = expiry;
  }

  public boolean isRevoked() {
    return revoked;
  }

  public void setRevoked(boolean revoked) {
    this.revoked = revoked;
  }

  public abstract boolean isReferenceToken();

  @Override
  public String toString() {
    return "Token{"
        + "value='"
        + value
        + '\''
        + ", expiry="
        + expiry
        + ", revoked="
        + revoked
        + ", referenceToken="
        + isReferenceToken()
        + '}';
  }
}
