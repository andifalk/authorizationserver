package com.example.authorizationserver.oauth.endpoint.resource;

import javax.validation.constraints.NotBlank;

public class IntrospectionRequest {

  /**
   * REQUIRED.  The string value of the token.  For access tokens, this
   *   is the "access_token" value returned from the token endpoint
   *   defined in OAuth 2.0 [RFC6749], Section 5.1.  For refresh tokens,
   *           this is the "refresh_token" value returned from the token endpoint
   *   as defined in OAuth 2.0 [RFC6749], Section 5.1.  Other token types
   *   are outside the scope of this specification.
   */
  @NotBlank
  private String token;

  /**
   * OPTIONAL. A hint about the type of the token submitted for introspection. The protected
   * resource MAY pass this parameter to help the authorization server optimize the token lookup. If
   * the server is unable to locate the token using the given hint, it MUST extend its search across
   * all of its supported token types. An authorization server MAY ignore this parameter,
   * particularly if it is able to detect the token type automatically. Values for this field are
   * defined in the "OAuth Token Type Hints" registry defined in OAuth Token Revocation [RFC7009].
   */
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
    return "IntrospectionRequest{" +
            "token='" + token + '\'' +
            ", token_type_hint='" + token_type_hint + '\'' +
            '}';
  }
}
