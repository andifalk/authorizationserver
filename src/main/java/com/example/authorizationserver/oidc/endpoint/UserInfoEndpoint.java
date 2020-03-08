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
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.id.Audience;
import com.nimbusds.oauth2.sdk.id.Subject;
import com.nimbusds.oauth2.sdk.token.BearerTokenError;
import com.nimbusds.openid.connect.sdk.UserInfoErrorResponse;
import com.nimbusds.openid.connect.sdk.UserInfoResponse;
import com.nimbusds.openid.connect.sdk.UserInfoSuccessResponse;
import com.nimbusds.openid.connect.sdk.claims.UserInfo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.text.ParseException;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Controller
public class UserInfoEndpoint {

  private final TokenService tokenService;
  private final UserService userService;
  private final JsonWebTokenService jsonWebTokenService;

  public UserInfoEndpoint(
      TokenService tokenService, UserService userService, JsonWebTokenService jsonWebTokenService) {
    this.tokenService = tokenService;
    this.userService = userService;
    this.jsonWebTokenService = jsonWebTokenService;
  }

  @GetMapping("/userinfo")
  public HTTPResponse userInfo(@RequestHeader("Authorization") String authorizationHeader) {
    String tokenValue = AuthenticationUtil.fromBearerAuthHeader(authorizationHeader);
    JsonWebToken jsonWebToken = tokenService.findJsonWebToken(tokenValue);
    UserInfoResponse userInfoResponse;
    if (jsonWebToken != null) {
      try {
        JWTClaimsSet jwtClaimsSet =
            jsonWebTokenService.parseAndValidateToken(jsonWebToken.getValue());
        Optional<User> user = userService.findOneByIdentifier(UUID.fromString(jwtClaimsSet.getSubject()));
        if (user.isPresent()) {
          UserInfo userInfo = new UserInfo(jwtClaimsSet);
          userInfo.setUpdatedTime(Date.from(user.get().getUpdatedAt().atZone( ZoneId.systemDefault()).toInstant()));
          userInfo.setClaim("address", user.get().getAddress());
          userInfoResponse =
              new UserInfoSuccessResponse(userInfo);
        } else {
          userInfoResponse = new UserInfoErrorResponse(BearerTokenError.INVALID_TOKEN);
        }
      } catch (ParseException | JOSEException e) {
        userInfoResponse = new UserInfoErrorResponse(BearerTokenError.INVALID_TOKEN);
      }
    } else {
      OpaqueToken opaqueWebToken = tokenService.findOpaqueWebToken(tokenValue);
      if (opaqueWebToken != null) {
        opaqueWebToken.validate();
        Optional<User> user = userService.findOneByIdentifier(UUID.fromString(opaqueWebToken.getSubject()));
        if (user.isPresent()) {
          User authenticatedUser = user.get();
          com.nimbusds.openid.connect.sdk.claims.UserInfo userInfo =
              new com.nimbusds.openid.connect.sdk.claims.UserInfo(
                  new Subject(opaqueWebToken.getSubject()));
          userInfo.setAudience(new Audience(opaqueWebToken.getClientId()));
          userInfo.setFamilyName(authenticatedUser.getLastName());
          userInfo.setGivenName(authenticatedUser.getFirstName());
          userInfo.setEmailAddress(authenticatedUser.getEmail());
          userInfo.setName(authenticatedUser.getUsername());
          userInfo.setPhoneNumber(authenticatedUser.getPhone());
          userInfo.setClaim("groups", authenticatedUser.getGroups());
          userInfo.setUpdatedTime(Date.from(user.get().getUpdatedAt().atZone( ZoneId.systemDefault()).toInstant()));
          userInfoResponse = new UserInfoSuccessResponse(userInfo);
        } else {
          userInfoResponse = new UserInfoErrorResponse(BearerTokenError.INVALID_TOKEN);
        }
      } else {
        userInfoResponse = new UserInfoErrorResponse(BearerTokenError.MISSING_TOKEN);
      }
    }
    return userInfoResponse.toHTTPResponse();
  }

  @ExceptionHandler(MissingRequestHeaderException.class)
  public ResponseEntity<String> handle(MissingRequestHeaderException ex) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).header("WWW-Authenticate", "Basic")
        .body(
            "WWW-Authenticate: error=\"invalid_token\",\n"
                + "    error_description=\"Access Token is required\"\n");
  }

  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<String> handle(BadCredentialsException ex) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
  }
}
