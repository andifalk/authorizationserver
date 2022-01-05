package com.example.authorizationserver.oauth.common;

public enum TokenType {

  // Indicates that the token is an OAuth 2.0 access token
  ACCESS_TOKEN("urn:ietf:params:oauth:token-type:access_token"),

  // Indicates that the token is an OAuth 2.0 refresh token.
  REFRESH_TOKEN("urn:ietf:params:oauth:token-type:refresh_token"),

  // Indicates that the token is an ID Token as defined in OpenID.Core.
  ID_TOKEN("urn:ietf:params:oauth:token-type:id_token"),

  // Indicates that the token is a base64url-encoded SAML 1.1 assertion.
  SAML11_TOKEN("urn:ietf:params:oauth:token-type:saml1"),

  // Indicates that the token is a base64url-encoded SAML 2.0 assertion.
  SAML2_TOKEN("urn:ietf:params:oauth:token-type:saml2"),

  // Indicated that the token is a JSON web token.
  JWT_TOKEN("urn:ietf:params:oauth:token-type:jwt");

  private final String identifier;

  TokenType(String identifier) {
    this.identifier = identifier;
  }

  public String getIdentifier() {
    return identifier;
  }

  public static TokenType getTokenTypeForIdentifier(String identifier) {
    if (ACCESS_TOKEN.getIdentifier().equals(identifier)) {
      return ACCESS_TOKEN;
    } else if (REFRESH_TOKEN.getIdentifier().equals(identifier)) {
      return REFRESH_TOKEN;
    } else if (ID_TOKEN.getIdentifier().equals(identifier)) {
      return ID_TOKEN;
    } else if (SAML11_TOKEN.getIdentifier().equals(identifier)) {
      return SAML11_TOKEN;
    } else if (SAML2_TOKEN.getIdentifier().equals(identifier)) {
      return SAML2_TOKEN;
    } else if (JWT_TOKEN.getIdentifier().equals(identifier)) {
      return JWT_TOKEN;
    } else {
      throw new IllegalArgumentException("Invalid token type " + identifier);
    }
  }
}
