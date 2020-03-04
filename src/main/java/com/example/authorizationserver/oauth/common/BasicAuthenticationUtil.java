package com.example.authorizationserver.oauth.common;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public final class BasicAuthenticationUtil {

  private static final String AUTHENTICATION_SCHEME_BASIC = "Basic";

  private BasicAuthenticationUtil() {}

  public static ClientCredentials fromBasicAuthHeader(String header) {
    if (header == null) {
      return null;
    }

    header = header.trim();
    if (!StringUtils.startsWithIgnoreCase(header, AUTHENTICATION_SCHEME_BASIC)) {
      return null;
    }

    byte[] base64Token = header.substring(6).getBytes(StandardCharsets.UTF_8);
    byte[] decoded;
    try {
      decoded = Base64.getDecoder().decode(base64Token);
    }
    catch (IllegalArgumentException e) {
      throw new BadCredentialsException(
              "Failed to decode basic authentication token");
    }

    String token = new String(decoded, StandardCharsets.UTF_8);

    int delim = token.indexOf(":");

    if (delim == -1) {
      throw new BadCredentialsException("Invalid basic authentication token");
    }
    return new ClientCredentials(token.substring(0, delim), token.substring(delim + 1));
  }
}
