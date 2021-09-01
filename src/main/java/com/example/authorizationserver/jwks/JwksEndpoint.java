package com.example.authorizationserver.jwks;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@CrossOrigin(originPatterns = "*", allowCredentials = "true", allowedHeaders = "*")
@RestController
@RequestMapping(JwksEndpoint.ENDPOINT)
public class JwksEndpoint {

  public static final String ENDPOINT = "/jwks";

  private final JwtPki jwtPki;

  public JwksEndpoint(JwtPki jwtPki) {
    this.jwtPki = jwtPki;
  }

  @Operation(
          summary = "Retrieves the JSON web key set with public key(s) to validate tokens",
          tags = {"OpenID Connect Discovery"}
  )
  @GetMapping
  public Map<String, Object> jwksEndpoint() {
    return jwtPki.getJwkSet().toJSONObject();
  }
}
