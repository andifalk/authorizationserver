package com.example.authorizationserver.oauth.endpoint.token.resource;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.net.URI;

/**
 * Token Request as specified by:
 *
 * <p>OAuth 2.0 (https://www.rfc-editor.org/rfc/rfc6749.html#section-4.1.3) OpenID Connect 1.0
 * (https://openid.net/specs/openid-connect-core-1_0.html#TokenRequest)
 */
public class TokenRequest {

  /**
   * Authorization Grant Type. REQUIRED One of {@link
   * com.example.authorizationserver.oauth.common.GrantType}
   */
  @NotBlank private final String grant_type;

  /**
   * Authorization code. REQUIRED if grant type is {@link
   * com.example.authorizationserver.oauth.common.GrantType#AUTHORIZATION_CODE}
   */
  private final String code;

  /**
   * Redirect URI. REQUIRED if grant type is {@link
   * com.example.authorizationserver.oauth.common.GrantType#AUTHORIZATION_CODE}
   */
  private final URI redirect_uri;

  /** Client Id. REQUIRED if not given by authorization header */
  private final String client_id;

  /**
   * Client Secret. REQUIRED for confidential client if not given by authorization header Applicable
   * for grant type is {@link
   * com.example.authorizationserver.oauth.common.GrantType#AUTHORIZATION_CODE} or {@link
   * com.example.authorizationserver.oauth.common.GrantType#CLIENT_CREDENTIALS}
   */
  private final String client_secret;

  /** Unhashed Code Verifier. REQUIRED for PKCE. */
  private final String code_verifier;

  /**
   * The resource owner username REQUIRED if grant type is {@link
   * com.example.authorizationserver.oauth.common.GrantType#PASSWORD}
   */
  private final String username;

  /**
   * The resource owner password. REQUIRED if grant type is {@link
   * com.example.authorizationserver.oauth.common.GrantType#PASSWORD}
   */
  private final String password;

  /**
   * The refresh token issued to the client. REQUIRED if grant type is {@link
   * com.example.authorizationserver.oauth.common.GrantType#REFRESH_TOKEN}
   */
  private final String refresh_token;

  public TokenRequest(
      @NotBlank String grant_type,
      @NotBlank String code,
      @NotNull URI redirect_uri,
      String client_id,
      String client_secret,
      String code_verifier,
      String username,
      String password,
      String refresh_token) {
    this.grant_type = grant_type;
    this.code = code;
    this.redirect_uri = redirect_uri;
    this.client_id = client_id;
    this.client_secret = client_secret;
    this.code_verifier = code_verifier;
    this.username = username;
    this.password = password;
    this.refresh_token = refresh_token;
  }

  public String getGrant_type() {
    return grant_type;
  }

  public String getCode() {
    return code;
  }

  public URI getRedirect_uri() {
    return redirect_uri;
  }

  public String getClient_id() {
    return client_id;
  }

  public String getClient_secret() {
    return client_secret;
  }

  public String getCode_verifier() {
    return code_verifier;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  public String getRefresh_token() {
    return refresh_token;
  }

  @Override
  public String toString() {
    return "TokenRequest{"
        + "grant_type='"
        + grant_type
        + '\''
        + ", code='"
        + code
        + '\''
        + ", redirect_uri="
        + redirect_uri
        + ", client_id='"
        + client_id
        + '\''
        + ", client_secret='*****'"
        + ", code_verifier='"
        + code_verifier
        + '\''
        + ", refresh_token='"
        + refresh_token
        + '\''
        + ", username='"
        + username
        + '\''
        + ", password='*****'"
        + '}';
  }
}
