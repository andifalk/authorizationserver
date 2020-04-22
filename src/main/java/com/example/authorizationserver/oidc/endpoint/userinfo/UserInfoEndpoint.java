package com.example.authorizationserver.oidc.endpoint.userinfo;

import com.example.authorizationserver.oauth.common.AuthenticationUtil;
import com.example.authorizationserver.token.jwt.JsonWebTokenService;
import com.example.authorizationserver.token.store.TokenService;
import com.example.authorizationserver.token.store.model.JsonWebToken;
import com.example.authorizationserver.token.store.model.OpaqueToken;
import com.example.authorizationserver.user.model.User;
import com.example.authorizationserver.user.service.UserService;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping(UserInfoEndpoint.ENDPOINT)
public class UserInfoEndpoint {

  public static final String ENDPOINT = "/userinfo";

  private final TokenService tokenService;
  private final UserService userService;
  private final JsonWebTokenService jsonWebTokenService;

  public UserInfoEndpoint(
      TokenService tokenService, UserService userService, JsonWebTokenService jsonWebTokenService) {
    this.tokenService = tokenService;
    this.userService = userService;
    this.jsonWebTokenService = jsonWebTokenService;
  }

  @GetMapping
  public ResponseEntity<UserInfo> userInfo(
      @RequestHeader("Authorization") String authorizationHeader) {
    String tokenValue = AuthenticationUtil.fromBearerAuthHeader(authorizationHeader);
    JsonWebToken jsonWebToken = tokenService.findJsonWebToken(tokenValue);
    Optional<User> user;
    if (jsonWebToken != null) {
      try {
        JWTClaimsSet jwtClaimsSet =
            jsonWebTokenService.parseAndValidateToken(jsonWebToken.getValue());
        if (TokenService.ANONYMOUS_TOKEN.equals(jwtClaimsSet.getSubject())) {
          return ResponseEntity.ok(new UserInfo(jwtClaimsSet.getSubject()));
        } else {
          user = userService.findOneByIdentifier(UUID.fromString(jwtClaimsSet.getSubject()));
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
          user = userService.findOneByIdentifier(UUID.fromString(opaqueWebToken.getSubject()));
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
