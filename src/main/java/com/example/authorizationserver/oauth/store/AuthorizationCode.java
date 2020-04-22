package com.example.authorizationserver.oauth.store;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

public class AuthorizationCode {

  private final String clientId;
  private final URI redirectUri;
  private final List<String> scopes;
  private final String code;
  private final LocalDateTime expiry;
  private final String subject;
  private final String nonce;
  private final String code_challenge;
  private final String code_challenge_method;

  public AuthorizationCode(
      String clientId,
      URI redirectUri,
      List<String> scopes,
      String code,
      String subject,
      String nonce,
      String code_challenge,
      String code_challenge_method) {
    this.clientId = clientId;
    this.redirectUri = redirectUri;
    this.scopes = scopes;
    this.code = code;
    this.subject = subject;
    this.nonce = nonce;
    this.code_challenge = code_challenge;
    this.code_challenge_method = code_challenge_method;
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

  public String getCode_challenge() {
    return code_challenge;
  }

  public String getCode_challenge_method() {
    return code_challenge_method;
  }

  public boolean isExpired() {
    return LocalDateTime.now().isAfter(getExpiry());
  }

  @Override
  public String toString() {
    return "AuthorizationState{"
        + "clientId='"
        + clientId
        + '\''
        + ", redirectUri="
        + redirectUri
        + ", scopes="
        + scopes
        + ", code='"
        + code
        + '\''
        + ", expiry="
        + expiry
        + ", subject="
        + subject
        + ", nonce="
        + nonce
        + ", code_challenge="
        + code_challenge
        + ", code_challenge_method="
        + code_challenge_method
        + '}';
  }
}
