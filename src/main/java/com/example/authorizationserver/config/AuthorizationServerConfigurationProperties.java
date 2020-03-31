package com.example.authorizationserver.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.URI;
import java.time.Duration;

@ConfigurationProperties(prefix = "auth-server")
public class AuthorizationServerConfigurationProperties {

  private URI issuer;
  private AccessToken accessToken;
  private IdToken idToken;
  private RefreshToken refreshToken;

  public URI getIssuer() {
    return issuer;
  }

  public void setIssuer(URI issuer) {
    this.issuer = issuer;
  }

  public AccessToken getAccessToken() {
    return accessToken;
  }

  public void setAccessToken(AccessToken accessToken) {
    this.accessToken = accessToken;
  }

  public IdToken getIdToken() {
    return idToken;
  }

  public void setIdToken(IdToken idToken) {
    this.idToken = idToken;
  }

  public RefreshToken getRefreshToken() {
    return refreshToken;
  }

  public void setRefreshToken(RefreshToken refreshToken) {
    this.refreshToken = refreshToken;
  }

  public static abstract class Token {
    private Duration lifetime;

    public Duration getLifetime() {
      return lifetime;
    }

    public void setLifetime(Duration lifetime) {
      this.lifetime = lifetime;
    }
  }

  public enum TokenType {
    JWT,
    OPAQUE
  }

  public static class AccessToken extends Token {
    private TokenType defaultFormat;

    public TokenType getDefaultFormat() {
      return defaultFormat;
    }

    public void setDefaultFormat(TokenType defaultFormat) {
      this.defaultFormat = defaultFormat;
    }
  }

  public static class IdToken extends Token {}

  public static class RefreshToken extends Token {
    private Duration maxLifetime;

    public Duration getMaxLifetime() {
      return maxLifetime;
    }

    public void setMaxLifetime(Duration maxLifetime) {
      this.maxLifetime = maxLifetime;
    }
  }
}
