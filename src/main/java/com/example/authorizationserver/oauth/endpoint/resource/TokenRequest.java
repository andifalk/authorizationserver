package com.example.authorizationserver.oauth.endpoint.resource;

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
  @NotBlank private String grant_type;

  /**
   * Authorization code. REQUIRED if grant type is {@link
   * com.example.authorizationserver.oauth.common.GrantType#AUTHORIZATION_CODE}
   */
  private String code;

  /**
   * Redirect URI. REQUIRED if grant type is {@link
   * com.example.authorizationserver.oauth.common.GrantType#AUTHORIZATION_CODE}
   */
  private URI redirect_uri;

  /** Client Id. REQUIRED if not given by authorization header */
  private String client_id;

  /**
   * Client Secret. REQUIRED for confidential client if not given by authorization header Applicable
   * for grant type is {@link
   * com.example.authorizationserver.oauth.common.GrantType#AUTHORIZATION_CODE} or {@link
   * com.example.authorizationserver.oauth.common.GrantType#CLIENT_CREDENTIALS}
   */
  private String client_secret;

  /** Unhashed Code Verifier. REQUIRED for PKCE. */
  private String code_verifier;

  /**
   * The resource owner username REQUIRED if grant type is {@link
   * com.example.authorizationserver.oauth.common.GrantType#PASSWORD}
   */
  private String username;

  /**
   * The resource owner password. REQUIRED if grant type is {@link
   * com.example.authorizationserver.oauth.common.GrantType#PASSWORD}
   */
  private String password;

  public TokenRequest(
      @NotBlank String grant_type,
      @NotBlank String code,
      @NotNull URI redirect_uri,
      String client_id,
      String client_secret,
      String code_verifier) {
    this.grant_type = grant_type;
    this.code = code;
    this.redirect_uri = redirect_uri;
    this.client_id = client_id;
    this.client_secret = client_secret;
    this.code_verifier = code_verifier;
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
        + '}';
  }
}
