package com.example.authorizationserver.oauth.endpoint.token.resource;

import javax.validation.constraints.NotBlank;
import java.net.URI;

/**
 * Token Request as specified by:
 *
 * <p>OAuth 2.0 (https://www.rfc-editor.org/rfc/rfc6749.html#section-4.1.3).
 * OpenID Connect 1.0 (https://openid.net/specs/openid-connect-core-1_0.html#TokenRequest)
 * and OAuth 2.0 Token Exchange (https://www.rfc-editor.org/rfc/rfc8693.html#section-2.1).
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
   * com.example.authorizationserver.oauth.common.GrantType#PASSWORD}.
   */
  private final String username;

  /**
   * The resource owner password. REQUIRED if grant type is {@link
   * com.example.authorizationserver.oauth.common.GrantType#PASSWORD}.
   */
  private final String password;

  /**
   * The refresh token issued to the client. REQUIRED if grant type is {@link
   * com.example.authorizationserver.oauth.common.GrantType#REFRESH_TOKEN}.
   */
  private final String refresh_token;

  /**
   *  A security token that represents the identity of the party on behalf of
   *  whom the request is being made. REQUIRED if grant type is
   *  {@link com.example.authorizationserver.oauth.common.GrantType#TOKEN_EXCHANGE}.
   */
  private final String subject_token;

  /**
   *  An identifier, as described in Section 3, that indicates the type of the security
   *  token in the subject_token parameter. REQUIRED if grant type is
   *  {@link com.example.authorizationserver.oauth.common.GrantType#TOKEN_EXCHANGE}.
   */
  private final String subject_token_type;

  /**
   * The scope of the access request. OPTIONAL if grant type is {@link
   * com.example.authorizationserver.oauth.common.GrantType#CLIENT_CREDENTIALS}.
   */
  private final String scope;

  /**
   *  A URI that indicates the target service or resource where the client
   *  intends to use the requested security token. OPTIONAL if grant type is
   *  {@link com.example.authorizationserver.oauth.common.GrantType#TOKEN_EXCHANGE}.
   */
  private final String resource;

  /**
   *  The logical name of the target service where the client intends to
   *  use the requested security token. OPTIONAL if grant type is
   *  {@link com.example.authorizationserver.oauth.common.GrantType#TOKEN_EXCHANGE}.
   */
  private final String audience;

  /**
   *  An identifier, as described in Section 3, for the type of the requested
   *  security token. OPTIONAL if grant type is
   *  {@link com.example.authorizationserver.oauth.common.GrantType#TOKEN_EXCHANGE}.
   */
  private final String requested_token_type;

  /**
   *  A security token that represents the identity of the acting party. OPTIONAL if grant type is
   *  {@link com.example.authorizationserver.oauth.common.GrantType#TOKEN_EXCHANGE}.
   */
  private final String actor_token;

  /**
   *  An identifier, as described in Section 3, that indicates the type of the security
   *  token in the actor_token parameter. REQUIRED when actor_token parameter is present and grant type is
   *  {@link com.example.authorizationserver.oauth.common.GrantType#TOKEN_EXCHANGE}.
   */
  private final String actor_token_type;


  public TokenRequest(
          String grant_type,
          String code,
          URI redirect_uri,
          String client_id,
          String client_secret,
          String code_verifier,
          String username,
          String password,
          String refresh_token,
          String subject_token,
          String subject_token_type,
          String scope,
          String resource,
          String audience,
          String requested_token_type,
          String actor_token,
          String actor_token_type) {
    this.grant_type = grant_type;
    this.code = code;
    this.redirect_uri = redirect_uri;
    this.client_id = client_id;
    this.client_secret = client_secret;
    this.code_verifier = code_verifier;
    this.username = username;
    this.password = password;
    this.refresh_token = refresh_token;
    this.subject_token = subject_token;
    this.subject_token_type = subject_token_type;
    this.scope = scope;
    this.resource = resource;
    this.audience = audience;
    this.requested_token_type = requested_token_type;
    this.actor_token = actor_token;
    this.actor_token_type = actor_token_type;
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

  public String getScope() {
    return scope;
  }

  public String getSubject_token() {
    return subject_token;
  }

  public String getSubject_token_type() {
    return subject_token_type;
  }

  public String getResource() {
    return resource;
  }

  public String getAudience() {
    return audience;
  }

  public String getRequested_token_type() {
    return requested_token_type;
  }

  public String getActor_token() {
    return actor_token;
  }

  public String getActor_token_type() {
    return actor_token_type;
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
            + ", scope='"
            + scope
            + '\''
            + ", resource='"
            + resource
            + '\''
            + ", audience='"
            + audience
            + '\''
            + ", requested_token_type='"
            + requested_token_type
            + '\''
            + ", subject_token='"
            + subject_token
            + '\''
            + ", subject_token_type='"
            + subject_token_type
            + '\''
            + ", actor_token='"
            + actor_token
            + '\''
            + ", actor_token_type='"
            + actor_token_type
            + '\''
            + ", username='"
            + username
            + '\''
            + ", password='*****'"
            + '}';
  }
}
