package com.example.authorizationserver.oauth.common;

public enum GrantType {
  AUTHORIZATION_CODE("authorization_code"),
  PASSWORD("password"),
  CLIENT_CREDENTIALS("client_credentials"),
  TOKEN_EXCHANGE("urn:ietf:params:oauth:grant-type:token-exchange");

  private String grant;

  GrantType(String grant) {
    this.grant = grant;
  }

  public String getGrant() {
    return grant;
  }
}
