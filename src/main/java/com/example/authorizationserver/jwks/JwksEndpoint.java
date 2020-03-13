package com.example.authorizationserver.jwks;

import net.minidev.json.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(JwksEndpoint.ENDPOINT)
public class JwksEndpoint {

  public static final String ENDPOINT = "/jwks";

  private final JwtPki jwtPki;

  public JwksEndpoint(JwtPki jwtPki) {
    this.jwtPki = jwtPki;
  }

  @GetMapping
  public JSONObject jwksEndpoint() {
    return jwtPki.getJwkSet().toJSONObject();
  }
}
