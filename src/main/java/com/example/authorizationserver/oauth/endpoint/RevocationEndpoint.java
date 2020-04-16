package com.example.authorizationserver.oauth.endpoint;

import com.example.authorizationserver.oauth.common.AuthenticationUtil;
import com.example.authorizationserver.oauth.common.ClientCredentials;
import com.example.authorizationserver.oauth.endpoint.resource.RevocationRequest;
import com.example.authorizationserver.oauth.endpoint.resource.RevocationResponse;
import com.example.authorizationserver.token.store.TokenService;
import com.example.authorizationserver.token.store.model.JsonWebToken;
import com.example.authorizationserver.token.store.model.OpaqueToken;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** OAuth 2.0 Token Revocation as specified in https://tools.ietf.org/html/rfc7009 */
@RestController
@RequestMapping(RevocationEndpoint.ENDPOINT)
public class RevocationEndpoint {

  public static final String ENDPOINT = "/revoke";

  private final TokenService tokenService;

  public RevocationEndpoint(TokenService tokenService) {
    this.tokenService = tokenService;
  }

  @PostMapping
  public ResponseEntity<RevocationResponse> revoke(
      @RequestHeader("Authorization") String authorizationHeader,
      @ModelAttribute("introspection_request") RevocationRequest revocationRequest,
      BindingResult result) {

    ClientCredentials clientCredentials;

    try {

      clientCredentials = AuthenticationUtil.fromBasicAuthHeader(authorizationHeader);
      if (clientCredentials == null) {
        return reportInvalidClientError();
      }

      OpaqueToken opaqueWebToken = tokenService.findOpaqueWebToken(revocationRequest.getToken());
      if (opaqueWebToken != null) {
        tokenService.remove(opaqueWebToken);
      } else {
        JsonWebToken jsonWebToken = tokenService.findJsonWebToken(revocationRequest.getToken());
        if (jsonWebToken != null) {
          tokenService.remove(jsonWebToken);
        } else {
          return ResponseEntity.badRequest().body(new RevocationResponse(null, "invalid_request"));
        }
      }
      return ResponseEntity.ok(new RevocationResponse("ok", null));
    } catch (BadCredentialsException ex) {
      return reportInvalidClientError();
    }
  }

  private ResponseEntity<RevocationResponse> reportInvalidClientError() {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .header("WWW-Authenticate", "Basic")
        .body(new RevocationResponse(null, "invalid_client"));
  }
}
