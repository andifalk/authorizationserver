package com.example.authorizationserver.oauth.client.model;

import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Entity
public class RegisteredClient extends AbstractPersistable<Long> {

  @NotBlank
  @Size(max = 100)
  @Column(unique = true)
  private String clientId;

  @Size(max = 100)
  private String clientSecret;

  @NotNull
  private boolean confidential;

  @NotNull
  private boolean offline;

  @NotNull
  private boolean directGrant;

  @NotNull
  @Enumerated(EnumType.STRING)
  private AccessTokenFormat accessTokenFormat;

  @NotEmpty
  @ElementCollection(fetch = FetchType.EAGER)
  private Set<String> redirectUris = new HashSet<>();

  @NotEmpty
  @ElementCollection(fetch = FetchType.EAGER)
  private Set<String> corsUris = new HashSet<>();

  public RegisteredClient() {
  }

  public String getClientId() {
    return clientId;
  }

  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

  public String getClientSecret() {
    return clientSecret;
  }

  public void setClientSecret(String clientSecret) {
    this.clientSecret = clientSecret;
  }

  public boolean isConfidential() {
    return confidential;
  }

  public void setConfidential(boolean confidential) {
    this.confidential = confidential;
  }

  public boolean isOffline() {
    return offline;
  }

  public void setOffline(boolean offline) {
    this.offline = offline;
  }

  public boolean isDirectGrant() {
    return directGrant;
  }

  public void setDirectGrant(boolean directGrant) {
    this.directGrant = directGrant;
  }

  public Set<String> getRedirectUris() {
    return redirectUris;
  }

  public void setRedirectUris(Set<String> redirectUris) {
    this.redirectUris = redirectUris;
  }

  public Set<String> getCorsUris() {
    return corsUris;
  }

  public void setCorsUris(Set<String> corsUris) {
    this.corsUris = corsUris;
  }

  public AccessTokenFormat getAccessTokenFormat() {
    return accessTokenFormat;
  }

  public void setAccessTokenFormat(AccessTokenFormat accessTokenFormat) {
    this.accessTokenFormat = accessTokenFormat;
  }

  @Override
  public String toString() {
    return "RegisteredClient{" +
            "clientId='" + clientId + '\'' +
            ", clientSecret='*****'" +
            ", confidential=" + confidential +
            ", accessTokenFormat=" + accessTokenFormat +
            ", offline=" + offline +
            ", directGrant=" + directGrant +
            ", redirectUris=" + redirectUris +
            ", corsUris=" + corsUris +
            '}';
  }
}
