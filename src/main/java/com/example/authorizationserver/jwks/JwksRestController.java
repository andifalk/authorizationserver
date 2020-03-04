package com.example.authorizationserver.jwks;

import net.minidev.json.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/jwks")
public class JwksRestController {

  private final JwtPki jwtPki;

  public JwksRestController(JwtPki jwtPki) {
    this.jwtPki = jwtPki;
  }

  @GetMapping
  public JSONObject jwksEndpoint() {
    return jwtPki.getJwkSet().toJSONObject();
  }

}
