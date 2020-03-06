package com.example.authorizationserver.oauth.endpoint;

import com.example.authorizationserver.oauth.client.RegisteredClientService;
import com.example.authorizationserver.oauth.client.model.RegisteredClient;
import com.example.authorizationserver.oauth.common.AuthenticationUtil;
import com.example.authorizationserver.oauth.common.ClientCredentials;
import com.example.authorizationserver.oauth.common.GrantType;
import com.example.authorizationserver.oauth.endpoint.resource.TokenRequest;
import com.example.authorizationserver.oauth.endpoint.resource.TokenResponse;
import com.example.authorizationserver.oauth.store.AuthorizationCode;
import com.example.authorizationserver.oauth.store.AuthorizationCodeService;
import com.example.authorizationserver.token.store.TokenService;
import com.example.authorizationserver.user.model.User;
import com.example.authorizationserver.user.service.UserService;
import com.nimbusds.jose.JOSEException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

import static javax.xml.crypto.dsig.DigestMethod.SHA256;
import static javax.xml.crypto.dsig.DigestMethod.SHA3_256;

@RequestMapping("/token")
@RestController
public class TokenEndpoint {
  private static final Logger LOG = LoggerFactory.getLogger(TokenEndpoint.class);

  private final AuthorizationCodeService authorizationCodeService;
  private final RegisteredClientService registeredClientService;
  private final UserService userService;
  private final TokenService tokenService;

  private final Duration accessTokenLifetime;
  private final Duration idTokenLifetime;
  private final Duration refreshTokenLifetime;

  public TokenEndpoint(
      AuthorizationCodeService authorizationCodeService,
      UserService userService,
      TokenService tokenService,
      @Value("${auth-server.access-token.lifetime}") Duration accessTokenLifetime,
      @Value("${auth-server.id-token.lifetime}") Duration idTokenLifetime,
      @Value("${auth-server.refresh-token.lifetime}") Duration refreshTokenLifetime,
      @Value("${auth-server.access-token.default-format}") String accessTokenFormat,
      RegisteredClientService registeredClientService) {
    this.authorizationCodeService = authorizationCodeService;
    this.userService = userService;
    this.tokenService = tokenService;
    this.accessTokenLifetime = accessTokenLifetime;
    this.idTokenLifetime = idTokenLifetime;
    this.refreshTokenLifetime = refreshTokenLifetime;
    this.registeredClientService = registeredClientService;
  }

  @PostMapping
  public ResponseEntity<TokenResponse> getToken(
      @RequestHeader(name = "Authorization", required = false) String authorizationHeader,
      @ModelAttribute("token_request") TokenRequest tokenRequest,
      BindingResult result)
      throws JOSEException {


    if (tokenRequest.getGrant_type().equalsIgnoreCase(GrantType.CLIENT_CREDENTIALS.getGrant())) {
      return getTokenResponseForClientCredentials(authorizationHeader, tokenRequest);
    } else if (tokenRequest.getGrant_type().equalsIgnoreCase(GrantType.PASSWORD.getGrant())) {
      return ResponseEntity.badRequest().body(new TokenResponse("unsupported_grant_type"));
    } else if (tokenRequest.getGrant_type().equalsIgnoreCase(GrantType.TOKEN_EXCHANGE.getGrant())) {
      return ResponseEntity.badRequest().body(new TokenResponse("unsupported_grant_type"));
    } else if (tokenRequest
        .getGrant_type()
        .equalsIgnoreCase(GrantType.AUTHORIZATION_CODE.getGrant())) {
      return getTokenResponseForAuthorizationCode(authorizationHeader, tokenRequest);
    } else {
      return ResponseEntity.badRequest().body(new TokenResponse("unsupported_grant_type"));
    }
  }

  /*
   * Create token response for 'authorization code'.
   */
  private ResponseEntity<TokenResponse> getTokenResponseForAuthorizationCode(
      String authorizationHeader, TokenRequest tokenRequest) throws JOSEException {

    String clientId;
    String clientSecret;

    if (authorizationHeader != null) {
      ClientCredentials clientCredentials =
          AuthenticationUtil.fromBasicAuthHeader(authorizationHeader);
      clientId = clientCredentials.getClientId();
      clientSecret = clientCredentials.getClientSecret();
    } else if (StringUtils.isNotBlank(tokenRequest.getClient_id())) {
      clientId = tokenRequest.getClient_id();
      clientSecret = tokenRequest.getClient_secret();
    } else {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .header("WWW-Authenticate", "Basic")
          .body(new TokenResponse("invalid_client"));
    }

    AuthorizationCode authorizationCode = authorizationCodeService.getCode(tokenRequest.getCode());
    if (authorizationCode == null || !clientId.equals(authorizationCode.getClientId())) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .header("WWW-Authenticate", "Basic")
          .body(new TokenResponse("invalid_client"));
    }

    RegisteredClient registeredClient = registeredClientService.findOneByClientId(clientId);
    if (registeredClient.isConfidential()) {
      if (StringUtils.isBlank(clientSecret)
          || !registeredClient.getClientSecret().equals(clientSecret)) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .header("WWW-Authenticate", "Basic")
            .body(new TokenResponse("invalid_client"));
      }
    } else {
      if (StringUtils.isNotBlank(tokenRequest.getCode_verifier())) {
        if (StringUtils.isBlank(authorizationCode.getCode_challenge_method())
            || "S256".equalsIgnoreCase(authorizationCode.getCode_challenge_method())) {
          // Rehash the code verifier
          try {
            String rehashedChallenge = rehashCodeVerifier(tokenRequest.getCode_verifier());
            if (!authorizationCode.getCode_challenge().equals(rehashedChallenge)) {
              return ResponseEntity.badRequest().body(new TokenResponse("invalid_grant"));
            }
          } catch (NoSuchAlgorithmException e) {
            return ResponseEntity.badRequest().body(new TokenResponse("server_error"));
          }
        } else if ("plain".equalsIgnoreCase(authorizationCode.getCode_challenge_method())) {
          if (!authorizationCode.getCode_challenge().equals(tokenRequest.getCode_verifier())) {
            return ResponseEntity.badRequest().body(new TokenResponse("invalid_grant"));
          }
        }
      } else {
        return ResponseEntity.badRequest().body(new TokenResponse("invalid_grant"));
      }
    }

    Optional<User> user =
        userService.findOneByIdentifier(UUID.fromString(authorizationCode.getSubject()));
    if (user.isPresent()) {

      LOG.info(
          "Creating token response for user {}, client id {} and scopes {}",
          user.get().getUsername(),
          authorizationCode.getClientId(),
          authorizationCode.getScopes());

      return ResponseEntity.ok(
          new TokenResponse(
              tokenService
                  .createPersonalizedJwtAccessToken(
                      user.get(),
                      authorizationCode.getClientId(),
                      authorizationCode.getNonce(),
                      accessTokenLifetime)
                  .getValue(),
              tokenService
                  .createRefreshToken(authorizationCode.getClientId(), refreshTokenLifetime)
                  .getValue(),
              accessTokenLifetime.toSeconds(),
              tokenService
                  .createIdToken(
                      user.get(),
                      authorizationCode.getClientId(),
                      authorizationCode.getNonce(),
                      authorizationCode.getScopes(),
                      idTokenLifetime)
                  .getValue()));
    } else {
      return ResponseEntity.badRequest().body(new TokenResponse("invalid_grant"));
    }
  }

  /*
   * Create token response for 'client_credentials'.
   */
  private ResponseEntity<TokenResponse> getTokenResponseForClientCredentials(
      String authorizationHeader, TokenRequest tokenRequest) throws JOSEException {

    String clientId;
    String clientSecret;

    if (authorizationHeader != null) {
      ClientCredentials clientCredentials =
          AuthenticationUtil.fromBasicAuthHeader(authorizationHeader);
      clientId = clientCredentials.getClientId();
      clientSecret = clientCredentials.getClientSecret();
    } else if (StringUtils.isNotBlank(tokenRequest.getClient_id())
        && StringUtils.isNotBlank(tokenRequest.getClient_secret())) {
      clientId = tokenRequest.getClient_id();
      clientSecret = tokenRequest.getClient_secret();
    } else {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .header("WWW-Authenticate", "Basic")
          .body(new TokenResponse("invalid_client"));
    }

    RegisteredClient registeredClient = registeredClientService.findOneByClientId(clientId);
    if (registeredClient != null && registeredClient.getClientSecret().equals(clientSecret)) {
      if (registeredClient.isDirectGrant()) {
        return ResponseEntity.ok(
            new TokenResponse(
                tokenService
                    .createAnonymousJwtAccessToken(clientId, accessTokenLifetime)
                    .getValue(),
                tokenService.createRefreshToken(clientId, refreshTokenLifetime).getValue(),
                accessTokenLifetime.toSeconds(),
                null));
      } else {
        return ResponseEntity.badRequest().body(new TokenResponse("unauthorized_client"));
      }
    } else {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .header("WWW-Authenticate", "Basic")
          .body(new TokenResponse("invalid_client"));
    }
  }

  private String rehashCodeVerifier(String codeVerifier) throws NoSuchAlgorithmException {
    final MessageDigest digest = MessageDigest.getInstance("SHA-256");
    final byte[] hashbytes = digest.digest(codeVerifier.getBytes(StandardCharsets.UTF_8));
    return Base64.getUrlEncoder().withoutPadding().encodeToString(hashbytes);
  }

  private String bytesToHex(byte[] hash) {
    StringBuffer hexString = new StringBuffer();
    for (int i = 0; i < hash.length; i++) {
      String hex = Integer.toHexString(0xff & hash[i]);
      if (hex.length() == 1) hexString.append('0');
      hexString.append(hex);
    }
    return hexString.toString();
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
