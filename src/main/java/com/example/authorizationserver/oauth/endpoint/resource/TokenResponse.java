package com.example.authorizationserver.oauth.endpoint.resource;

public class TokenResponse {

  private static final String BEARER_TOKEN_TYPE = "Bearer";

  private String access_token;
  private String token_type = BEARER_TOKEN_TYPE;
  private String refresh_token;
  private long expires_in;
  private String id_token;

  public String getAccess_token() {
    return access_token;
  }

  public void setAccess_token(String access_token) {
    this.access_token = access_token;
  }

  public String getToken_type() {
    return token_type;
  }

  public void setToken_type(String token_type) {
    this.token_type = token_type;
  }

  public String getRefresh_token() {
    return refresh_token;
  }

  public void setRefresh_token(String refresh_token) {
    this.refresh_token = refresh_token;
  }

  public long getExpires_in() {
    return expires_in;
  }

  public void setExpires_in(long expires_in) {
    this.expires_in = expires_in;
  }

  public String getId_token() {
    return id_token;
  }

  public void setId_token(String id_token) {
    this.id_token = id_token;
  }

  public TokenResponse(String access_token, String refresh_token, long expires_in, String id_token) {
    this.access_token = access_token;
    this.refresh_token = refresh_token;
    this.expires_in = expires_in;
    this.id_token = id_token;
  }

  @Override
  public String toString() {
    return "TokenResponse{" +
            "access_token='" + access_token + '\'' +
            ", token_type='" + token_type + '\'' +
            ", refresh_token='" + refresh_token + '\'' +
            ", expires_in=" + expires_in +
            ", id_token='" + id_token + '\'' +
            '}';
  }
}
