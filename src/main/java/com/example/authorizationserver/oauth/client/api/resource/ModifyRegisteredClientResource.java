package com.example.authorizationserver.oauth.client.api.resource;

import com.example.authorizationserver.oauth.client.model.RegisteredClient;

import javax.validation.constraints.Size;

public class ModifyRegisteredClientResource extends RegisteredClientResource {

  @Size(max = 100)
  private String clientSecret;

  public ModifyRegisteredClientResource() {}

  public ModifyRegisteredClientResource(RegisteredClient registeredClient) {
    super(registeredClient);
    this.clientSecret = registeredClient.getClientSecret();
  }

  public String getClientSecret() {
    return clientSecret;
  }

  public void setClientSecret(String clientSecret) {
    this.clientSecret = clientSecret;
  }

  @Override
  public String toString() {
    return "ModifyRegisteredClientResource{" +
            "clientSecret='" + clientSecret + '\'' +
            "} " + super.toString();
  }
}
