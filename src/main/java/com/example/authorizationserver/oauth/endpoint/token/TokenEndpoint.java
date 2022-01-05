package com.example.authorizationserver.oauth.endpoint.token;

import com.example.authorizationserver.oauth.common.GrantType;
import com.example.authorizationserver.oauth.endpoint.token.resource.TokenRequest;
import com.example.authorizationserver.oauth.endpoint.token.resource.TokenResponse;
import com.nimbusds.jose.JOSEException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(originPatterns = "*", allowCredentials = "true", allowedHeaders = "*")
@RequestMapping(TokenEndpoint.ENDPOINT)
@RestController
public class TokenEndpoint {
  public static final String ENDPOINT = "/token";
  private static final Logger LOG = LoggerFactory.getLogger(TokenEndpoint.class);

  private final ClientCredentialsTokenEndpointService clientCredentialsTokenEndpointService;
  private final PasswordTokenEndpointService passwordTokenEndpointService;
  private final RefreshTokenEndpointService refreshTokenEndpointService;
  private final AuthorizationCodeTokenEndpointService authorizationCodeTokenEndpointService;
  private final TokenExchangeEndpointService tokenExchangeEndpointService;

  public TokenEndpoint(
          ClientCredentialsTokenEndpointService clientCredentialsTokenEndpointService,
          PasswordTokenEndpointService passwordTokenEndpointService,
          RefreshTokenEndpointService refreshTokenEndpointService,
          AuthorizationCodeTokenEndpointService authorizationCodeTokenEndpointService, TokenExchangeEndpointService tokenExchangeEndpointService) {
    this.clientCredentialsTokenEndpointService = clientCredentialsTokenEndpointService;
    this.passwordTokenEndpointService = passwordTokenEndpointService;
    this.refreshTokenEndpointService = refreshTokenEndpointService;
    this.authorizationCodeTokenEndpointService = authorizationCodeTokenEndpointService;
    this.tokenExchangeEndpointService = tokenExchangeEndpointService;
  }

  @PostMapping
  public ResponseEntity<TokenResponse> getToken(
          @RequestHeader(name = "Authorization", required = false) String authorizationHeader,
          @ModelAttribute("token_request") TokenRequest tokenRequest) {

    LOG.debug("Exchange token with grant type [{}]", tokenRequest.getGrant_type());

    if (tokenRequest.getGrant_type().equalsIgnoreCase(GrantType.CLIENT_CREDENTIALS.getGrant())) {
      return clientCredentialsTokenEndpointService.getTokenResponseForClientCredentials(
              authorizationHeader, tokenRequest);
    } else if (tokenRequest.getGrant_type().equalsIgnoreCase(GrantType.PASSWORD.getGrant())) {
      return passwordTokenEndpointService.getTokenResponseForPassword(
              authorizationHeader, tokenRequest);
    } else if (tokenRequest
            .getGrant_type()
            .equalsIgnoreCase(GrantType.AUTHORIZATION_CODE.getGrant())) {
      return authorizationCodeTokenEndpointService.getTokenResponseForAuthorizationCode(
              authorizationHeader, tokenRequest);
    } else if (tokenRequest.getGrant_type().equalsIgnoreCase(GrantType.REFRESH_TOKEN.getGrant())) {
      return refreshTokenEndpointService.getTokenResponseForRefreshToken(
              authorizationHeader, tokenRequest);
    } else if (tokenRequest.getGrant_type().equalsIgnoreCase(GrantType.TOKEN_EXCHANGE.getGrant())) {
      return tokenExchangeEndpointService.getTokenResponseForTokenExchange(authorizationHeader, tokenRequest);
    } else {
      LOG.warn("Requested grant type [{}] is unsupported", tokenRequest.getGrant_type());
      return ResponseEntity.badRequest().body(new TokenResponse("unsupported_grant_type"));
    }
  }

  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<String> handle(MissingServletRequestParameterException ex) {
    return ResponseEntity.badRequest().body(ex.getMessage());
  }

  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<String> handle(BadCredentialsException ex) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
  }

  @ExceptionHandler(JOSEException.class)
  public ResponseEntity<String> handle(JOSEException ex) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
  }
}
