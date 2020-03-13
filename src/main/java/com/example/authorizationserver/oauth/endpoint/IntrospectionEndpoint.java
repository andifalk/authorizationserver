package com.example.authorizationserver.oauth.endpoint;

import com.example.authorizationserver.oauth.common.AuthenticationUtil;
import com.example.authorizationserver.oauth.common.ClientCredentials;
import com.example.authorizationserver.oauth.endpoint.resource.IntrospectionRequest;
import com.example.authorizationserver.oauth.endpoint.resource.IntrospectionResponse;
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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.Optional;
import java.util.UUID;

@RestController
public class IntrospectionEndpoint {

  public static final String ENDPOINT = "/introspect";

  private final TokenService tokenService;
  private final UserService userService;
  private final JsonWebTokenService jsonWebTokenService;

  public IntrospectionEndpoint(
      TokenService tokenService, UserService userService, JsonWebTokenService jsonWebTokenService) {
    this.tokenService = tokenService;
    this.userService = userService;
    this.jsonWebTokenService = jsonWebTokenService;
  }

  @PostMapping(ENDPOINT)
  public ResponseEntity<IntrospectionResponse> introspect(
      @RequestHeader("Authorization") String authorizationHeader,
      @ModelAttribute("introspection_request") IntrospectionRequest introspectionRequest,
      BindingResult result) {

    ClientCredentials clientCredentials;

    try {

      clientCredentials = AuthenticationUtil.fromBasicAuthHeader(authorizationHeader);
      if (clientCredentials == null) {
        return reportInvalidClientError();
      }

    String tokenValue = introspectionRequest.getToken();

    JsonWebToken jsonWebToken = tokenService.findJsonWebToken(tokenValue);
    if (jsonWebToken != null) {
      return ResponseEntity.ok(getIntrospectionResponse(jsonWebToken));
    } else {
      OpaqueToken opaqueWebToken = tokenService.findOpaqueWebToken(tokenValue);
      if (opaqueWebToken != null) {
        return ResponseEntity.ok(getIntrospectionResponse(opaqueWebToken));
      } else {
        return ResponseEntity.ok(new IntrospectionResponse(false));
      }
    }
    } catch (BadCredentialsException ex) {
      return reportInvalidClientError();
    }
  }

  private IntrospectionResponse getIntrospectionResponse(OpaqueToken opaqueWebToken) {
    String clientId;
    Optional<User> user;
    try {
      opaqueWebToken.validate();
      clientId = opaqueWebToken.getClientId();
      user = userService.findOneByIdentifier(UUID.fromString(opaqueWebToken.getSubject()));
      return user.map(
              u -> {
                IntrospectionResponse introspectionResponse = new IntrospectionResponse();
                introspectionResponse.setActive(true);
                introspectionResponse.setClient_id(clientId);
                introspectionResponse.setSub(u.getIdentifier().toString());
                introspectionResponse.setUsername(u.getUsername());
                /*introspectionResponse.setIss(jwtClaimsSet.getIssuer());
                introspectionResponse.setNbf(jwtClaimsSet.getNotBeforeTime().getTime());
                introspectionResponse.setIat(jwtClaimsSet.getIssueTime().getTime());*/
                return introspectionResponse;
              })
          .orElse(new IntrospectionResponse(false));
    } catch (BadCredentialsException ex) {
      return new IntrospectionResponse(false);
    }
  }

  private IntrospectionResponse getIntrospectionResponse(JsonWebToken jsonWebToken) {
    String clientId;
    Optional<User> user;
    try {
      JWTClaimsSet jwtClaimsSet =
          jsonWebTokenService.parseAndValidateToken(jsonWebToken.getValue());
      clientId = jwtClaimsSet.getStringClaim("client_id");
      user = userService.findOneByIdentifier(UUID.fromString(jwtClaimsSet.getSubject()));
      return user.map(
              u -> {
                IntrospectionResponse introspectionResponse = new IntrospectionResponse();
                introspectionResponse.setActive(true);
                introspectionResponse.setClient_id(clientId);
                introspectionResponse.setSub(u.getIdentifier().toString());
                introspectionResponse.setUsername(u.getUsername());
                introspectionResponse.setIss(jwtClaimsSet.getIssuer());
                introspectionResponse.setNbf(jwtClaimsSet.getNotBeforeTime().getTime());
                introspectionResponse.setIat(jwtClaimsSet.getIssueTime().getTime());
                introspectionResponse.setExp(jwtClaimsSet.getExpirationTime().getTime());
                return introspectionResponse;
              })
          .orElse(new IntrospectionResponse(false));
    } catch (ParseException | JOSEException e) {
      return new IntrospectionResponse(false);
    }
  }

    private ResponseEntity<IntrospectionResponse> reportInvalidClientError() {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
              .header("WWW-Authenticate", "Basic")
              .body(new IntrospectionResponse("invalid_client"));
    }
}
