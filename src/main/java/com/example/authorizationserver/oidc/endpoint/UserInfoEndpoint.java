package com.example.authorizationserver.oidc.endpoint;

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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/userinfo")
public class UserInfoEndpoint {

  private final TokenService tokenService;
  private final UserService userService;
  private final JsonWebTokenService jsonWebTokenService;

  public UserInfoEndpoint(TokenService tokenService, UserService userService, JsonWebTokenService jsonWebTokenService) {
    this.tokenService = tokenService;
    this.userService = userService;
    this.jsonWebTokenService = jsonWebTokenService;
  }

  @GetMapping
  public UserInfo userInfo(@RequestHeader("Authorization") String authorizationHeader) {
    String tokenValue = AuthenticationUtil.fromBearerAuthHeader(authorizationHeader);
    JsonWebToken jsonWebToken = tokenService.findJsonWebToken(tokenValue);
    Optional<User> user = Optional.empty();
    if (jsonWebToken != null) {
      try {
        JWTClaimsSet jwtClaimsSet = jsonWebTokenService.parseAndValidateToken(jsonWebToken.getValue());
        user = userService.findOneByIdentifier(UUID.fromString(jwtClaimsSet.getSubject()));
      } catch (ParseException | JOSEException e) {
        e.printStackTrace();
      }
    } else {
      OpaqueToken opaqueWebToken = tokenService.findOpaqueWebToken(tokenValue);
      if (opaqueWebToken != null) {
        opaqueWebToken.validate();
        user = userService.findOneByIdentifier(UUID.fromString(opaqueWebToken.getSubject()));
      } else {
        throw new BadCredentialsException("No valid bearer token");
      }
    }
    return user.map(UserInfo::new).orElseThrow(() -> new BadCredentialsException("no user"));
  }

  @ExceptionHandler(MissingRequestHeaderException.class)
  public ResponseEntity<String> handle(MissingRequestHeaderException ex) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(
            "WWW-Authenticate: error=\"invalid_token\",\n"
                + "    error_description=\"Access Token is required\"\n");
  }

  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<String> handle(BadCredentialsException ex) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
  }
}
