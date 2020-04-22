package com.example.authorizationserver.oauth.endpoint.revocation.resource;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * Revocation request as defined in https://www.rfc-editor.org/rfc/rfc7009.html.
 */
public class RevocationRequest {

  /**
   * REQUIRED. The token that the client wants to get revoked.
   */
  @NotBlank private String token;

  /**
   * OPTIONAL. A hint about the type of the token submitted for revocation. Clients MAY pass this
   * parameter in order to help the authorization server to optimize the token lookup. If the server
   * is unable to locate the token using the given hint, it MUST extend its search across all of its
   * supported token types. An authorization server MAY ignore this parameter, particularly if it is
   * able to detect the token type automatically. This specification defines two such values:
   *
   * <ul>
   *   <li>access_token: An access token as defined in RFC6749, Section 1.4</li>
   *   <li>refresh_token: A refresh token as defined in [RFC6749], Section 1.5</li>
   * </ul>
   */
  @Pattern(regexp = "access_token|refresh_token")
  private String token_type_hint;

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public String getToken_type_hint() {
    return token_type_hint;
  }

  public void setToken_type_hint(String token_type_hint) {
    this.token_type_hint = token_type_hint;
  }

  @Override
  public String toString() {
    return "IntrospectionRequest{"
        + "token='"
        + token
        + '\''
        + ", token_type_hint='"
        + token_type_hint
        + '\''
        + '}';
  }
}
