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
import java.util.UUID;

@Entity
public class RegisteredClient extends AbstractPersistable<Long> {

  /** Technical Identifier. */
  @NotNull private UUID identifier;

  /** Unique identifier for client. */
  @NotBlank
  @Size(max = 100)
  @Column(unique = true)
  private String clientId;

  /** Client secret, only needed for confidential clients. */
  @Size(max = 100)
  private String clientSecret;

  /**
   * Confidential or Public client? Public Client: Requires PKCE but no clientSecret Confidential
   * Client: Requires clientSecret
   */
  @NotNull private boolean confidential;

  /** Refresh tokens supported? */
  @NotNull private boolean offline;

  /** Direct grants like 'client_credentials' or 'password' allowed? */
  @NotNull private boolean directGrant;

  /** Specifies format for access tokens: JWt or Opaque */
  @NotNull
  @Enumerated(EnumType.STRING)
  private AccessTokenFormat accessTokenFormat;

  /** List of valid redirect URIs. */
  @NotEmpty
  @ElementCollection(fetch = FetchType.EAGER)
  private Set<String> redirectUris = new HashSet<>();

  /** List of CORS origins allowed. */
  @NotEmpty
  @ElementCollection(fetch = FetchType.EAGER)
  private Set<String> corsUris = new HashSet<>();

  public RegisteredClient() {}

  public RegisteredClient(
      UUID identifier,
      String clientId,
      String clientSecret,
      boolean confidential,
      boolean offline,
      boolean directGrant,
      AccessTokenFormat accessTokenFormat,
      Set<String> redirectUris,
      Set<String> corsUris) {
    this.identifier = identifier;
    this.accessTokenFormat = accessTokenFormat;
    this.clientId = clientId;
    this.clientSecret = clientSecret;
    this.confidential = confidential;
    this.directGrant = directGrant;
    this.offline = offline;
    this.corsUris = corsUris;
    this.redirectUris = redirectUris;
  }

  public UUID getIdentifier() {
    return identifier;
  }

  public void setIdentifier(UUID identifier) {
    this.identifier = identifier;
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
    return "RegisteredClient{"
        + "identifier='"
        + identifier
        + '\''
        + ", clientId='"
        + clientId
        + ", clientSecret='*****'"
        + ", confidential="
        + confidential
        + ", accessTokenFormat="
        + accessTokenFormat
        + ", offline="
        + offline
        + ", directGrant="
        + directGrant
        + ", redirectUris="
        + redirectUris
        + ", corsUris="
        + corsUris
        + '}';
  }
}
