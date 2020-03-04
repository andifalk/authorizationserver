package com.example.authorizationserver.oauth.store;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class AuthorizationState {

  private String clientId;
  private URI redirectUri;
  private List<String> scopes;
  private String code;
  private LocalDateTime expiry;
  private String subject;
  private String nonce;

  public AuthorizationState(String clientId, URI redirectUri, List<String> scopes, String code, String subject, String nonce) {
    this.clientId = clientId;
    this.redirectUri = redirectUri;
    this.scopes = scopes;
    this.code = code;
    this.subject = subject;
    this.nonce = nonce;
    this.expiry = LocalDateTime.now().plusMinutes(2);
  }

  public String getClientId() {
    return clientId;
  }

  public String getSubject() {
    return subject;
  }

  public String getNonce() {
    return nonce;
  }

  public URI getRedirectUri() {
    return redirectUri;
  }

  public String getCode() {
    return code;
  }

  public LocalDateTime getExpiry() {
    return expiry;
  }

  public List<String> getScopes() {
    return scopes;
  }

  @Override
  public String toString() {
    return "AuthorizationState{" +
            "clientId='" + clientId + '\'' +
            ", redirectUri=" + redirectUri +
            ", scopes=" + scopes +
            ", code='" + code + '\'' +
            ", expiry=" + expiry +
            ", subject=" + subject +
            ", nonce=" + nonce +
            '}';
  }
}
