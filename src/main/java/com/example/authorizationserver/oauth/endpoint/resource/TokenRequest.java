package com.example.authorizationserver.oauth.endpoint.resource;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.net.URI;

public class TokenRequest {

  @NotBlank
  private String grant_type;

  @NotBlank
  private String code;

  @NotNull
  private URI redirect_uri;

  private String client_id;

  private String client_secret;

  public TokenRequest(@NotBlank String grant_type, @NotBlank String code, @NotNull URI redirect_uri, String client_id, String client_secret) {
    this.grant_type = grant_type;
    this.code = code;
    this.redirect_uri = redirect_uri;
    this.client_id = client_id;
    this.client_secret = client_secret;
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

  @Override
  public String toString() {
    return "TokenRequest{" +
            "grant_type='" + grant_type + '\'' +
            ", code='" + code + '\'' +
            ", redirect_uri=" + redirect_uri +
            ", client_id='" + client_id + '\'' +
            ", client_secret='*****'" +
            '}';
  }
}
