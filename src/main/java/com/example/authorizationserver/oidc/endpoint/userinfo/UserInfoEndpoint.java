package com.example.authorizationserver.oidc.endpoint.userinfo;

import com.example.authorizationserver.oauth.common.AuthenticationUtil;
import com.example.authorizationserver.scim.model.ScimUserEntity;
import com.example.authorizationserver.scim.service.ScimService;
import com.example.authorizationserver.token.jwt.JsonWebTokenService;
import com.example.authorizationserver.token.store.TokenService;
import com.example.authorizationserver.token.store.model.JsonWebToken;
import com.example.authorizationserver.token.store.model.OpaqueToken;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.Optional;
import java.util.UUID;

@CrossOrigin(originPatterns = "*", allowCredentials = "true", allowedHeaders = "*")
@RestController
@RequestMapping(UserInfoEndpoint.ENDPOINT)
public class UserInfoEndpoint {
  private static final Logger LOG =
          LoggerFactory.getLogger(UserInfoEndpoint.class);

  public static final String ENDPOINT = "/userinfo";

  private final TokenService tokenService;
  private final ScimService scimService;
  private final JsonWebTokenService jsonWebTokenService;

  public UserInfoEndpoint(
          TokenService tokenService, ScimService scimService, JsonWebTokenService jsonWebTokenService) {
    this.tokenService = tokenService;
    this.scimService = scimService;
    this.jsonWebTokenService = jsonWebTokenService;
  }

  @GetMapping
  public ResponseEntity<UserInfo> userInfo(
          @RequestHeader("Authorization") String authorizationHeader) {
    String tokenValue = AuthenticationUtil.fromBearerAuthHeader(authorizationHeader);

    LOG.debug("Calling userinfo with bearer token header {}", tokenValue);

    JsonWebToken jsonWebToken = tokenService.findJsonWebToken(tokenValue);
    Optional<ScimUserEntity> user;
    if (jsonWebToken != null) {
      try {
        JWTClaimsSet jwtClaimsSet =
                jsonWebTokenService.parseAndValidateToken(jsonWebToken.getValue());
        if (TokenService.ANONYMOUS_TOKEN.equals(jwtClaimsSet.getStringClaim("ctx"))) {
          return ResponseEntity.ok(new UserInfo(jwtClaimsSet.getSubject()));
        } else {
          user = scimService.findUserByIdentifier(UUID.fromString(jwtClaimsSet.getSubject()));
          return user.map(u -> ResponseEntity.ok(new UserInfo(u)))
                  .orElse(
                          ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                  .header("WWW-Authenticate", "Bearer")
                                  .build());
        }
      } catch (ParseException | JOSEException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .header("WWW-Authenticate", "Bearer")
                .body(new UserInfo("invalid_token", "Access Token is invalid"));
      }
    } else {
      OpaqueToken opaqueWebToken = tokenService.findOpaqueToken(tokenValue);
      if (opaqueWebToken != null) {
        opaqueWebToken.validate();
        if (TokenService.ANONYMOUS_TOKEN.equals(opaqueWebToken.getSubject())) {
          return ResponseEntity.ok(new UserInfo(opaqueWebToken.getSubject()));
        } else {
          user = scimService.findUserByIdentifier(UUID.fromString(opaqueWebToken.getSubject()));
          return user.map(u -> ResponseEntity.ok(new UserInfo(u)))
                  .orElse(
                          ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                  .header("WWW-Authenticate", "Bearer")
                                  .build());
        }
      } else {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .header("WWW-Authenticate", "Bearer")
                .body(new UserInfo("invalid_token", "Access Token is invalid"));
      }
    }
  }

  @ExceptionHandler(MissingRequestHeaderException.class)
  public ResponseEntity<UserInfo> handle(MissingRequestHeaderException ex) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .header("WWW-Authenticate", "Bearer")
            .body(new UserInfo("invalid_token", "Access Token is required"));
  }
}
