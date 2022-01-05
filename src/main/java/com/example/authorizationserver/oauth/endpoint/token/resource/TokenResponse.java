package com.example.authorizationserver.oauth.endpoint.token.resource;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Token Response as specified by:
 *
 * <p>OAuth 2.0 (https://www.rfc-editor.org/rfc/rfc6749.html#section-4.1.3) OpenID Connect 1.0
 * (https://openid.net/specs/openid-connect-core-1_0.html#TokenRequest)
 */
public class TokenResponse {

  public static final String BEARER_TOKEN_TYPE = "Bearer";

  private String access_token;
  private String token_type;
  private String issued_token_type;
  private String refresh_token;
  private String scope;
  private long expires_in;
  private String id_token;
  private String error;

  public TokenResponse(
          String access_token,
          String refresh_token,
          long expires_in,
          String id_token,
          String token_type) {
    this(access_token, refresh_token, expires_in, id_token, token_type, null, null);
  }

  @JsonCreator
  public TokenResponse(
          String access_token,
          String refresh_token,
          long expires_in,
          String id_token,
          String token_type,
          String issued_token_type,
          String scope) {
    this.access_token = access_token;
    this.refresh_token = refresh_token;
    this.expires_in = expires_in;
    this.id_token = id_token;
    this.token_type = token_type;
    this.issued_token_type = issued_token_type;
    this.scope = scope;
  }

  public TokenResponse(String error) {
    this.error = error;
  }

  public String getAccess_token() {
    return access_token;
  }

  public void setAccess_token(String access_token) {
    this.access_token = access_token;
  }

  public String getToken_type() {
    return token_type;
  }

  public void setToken_type(String token_type) {
    this.token_type = token_type;
  }

  public String getRefresh_token() {
    return refresh_token;
  }

  public void setRefresh_token(String refresh_token) {
    this.refresh_token = refresh_token;
  }

  public long getExpires_in() {
    return expires_in;
  }

  public void setExpires_in(long expires_in) {
    this.expires_in = expires_in;
  }

  public String getId_token() {
    return id_token;
  }

  public void setId_token(String id_token) {
    this.id_token = id_token;
  }

  public String getIssued_token_type() {
    return issued_token_type;
  }

  public void setIssued_token_type(String issued_token_type) {
    this.issued_token_type = issued_token_type;
  }

  public String getScope() {
    return scope;
  }

  public void setScope(String scope) {
    this.scope = scope;
  }

  public String getError() {
    return error;
  }

  public void setError(String error) {
    this.error = error;
  }

  @Override
  public String toString() {
    return "TokenResponse{"
        + "access_token='"
        + access_token
        + '\''
        + ", token_type='"
        + token_type
        + '\''
        + ", refresh_token='"
        + refresh_token
        + '\''
        + ", expires_in="
        + expires_in
        + ", id_token='"
        + id_token
        + '\''
        + '}';
  }
}
