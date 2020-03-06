package com.example.authorizationserver.oauth.common;

public class ClientCredentials {

  private final String clientId;
  private final String clientSecret;

  public ClientCredentials(String clientId, String clientSecret) {
    this.clientId = clientId;
    this.clientSecret = clientSecret;
  }

  public String getClientId() {
    return clientId;
  }

  public String getClientSecret() {
    return clientSecret;
  }

  @Override
  public String toString() {
    return "ClientCredentials{" + "clientId='" + clientId + '\'' + ", clientSecret='*****'" + '}';
  }
}
