package com.example.authorizationserver.oauth.endpoint;

import com.example.authorizationserver.authentication.AuthenticationService;
import com.example.authorizationserver.config.AuthorizationServerConfigurationProperties;
import com.example.authorizationserver.oauth.client.RegisteredClientService;
import com.example.authorizationserver.oauth.client.model.AccessTokenFormat;
import com.example.authorizationserver.oauth.client.model.RegisteredClient;
import com.example.authorizationserver.oauth.common.AuthenticationUtil;
import com.example.authorizationserver.oauth.common.ClientCredentials;
import com.example.authorizationserver.oauth.common.GrantType;
import com.example.authorizationserver.oauth.endpoint.resource.TokenRequest;
import com.example.authorizationserver.oauth.endpoint.resource.TokenResponse;
import com.example.authorizationserver.oauth.store.AuthorizationCode;
import com.example.authorizationserver.oauth.store.AuthorizationCodeService;
import com.example.authorizationserver.token.store.TokenService;
import com.example.authorizationserver.token.store.model.OpaqueToken;
import com.example.authorizationserver.user.model.User;
import com.example.authorizationserver.user.service.UserService;
import com.nimbusds.jose.JOSEException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

import static java.nio.charset.StandardCharsets.UTF_8;

@RequestMapping(TokenEndpoint.ENDPOINT)
@RestController
public class TokenEndpoint {
  public static final String ENDPOINT = "/token";
  private static final Logger LOG = LoggerFactory.getLogger(TokenEndpoint.class);

  private final AuthenticationService authenticationService;
  private final AuthorizationCodeService authorizationCodeService;
  private final RegisteredClientService registeredClientService;
  private final UserService userService;
  private final TokenService tokenService;
  private final PasswordEncoder passwordEncoder;
  private final AuthorizationServerConfigurationProperties authorizationServerProperties;

  public TokenEndpoint(
          AuthenticationService authenticationService,
          AuthorizationCodeService authorizationCodeService,
          UserService userService,
          TokenService tokenService,
          RegisteredClientService registeredClientService, PasswordEncoder passwordEncoder, AuthorizationServerConfigurationProperties authorizationServerProperties) {
    this.authenticationService = authenticationService;
    this.authorizationCodeService = authorizationCodeService;
    this.userService = userService;
    this.tokenService = tokenService;
    this.registeredClientService = registeredClientService;
    this.passwordEncoder = passwordEncoder;
    this.authorizationServerProperties = authorizationServerProperties;
  }

  @PostMapping
  public ResponseEntity<TokenResponse> getToken(
      @RequestHeader(name = "Authorization", required = false) String authorizationHeader,
      @ModelAttribute("token_request") TokenRequest tokenRequest)
      throws JOSEException {

    if (tokenRequest.getGrant_type().equalsIgnoreCase(GrantType.CLIENT_CREDENTIALS.getGrant())) {
      return getTokenResponseForClientCredentials(authorizationHeader, tokenRequest);
    } else if (tokenRequest.getGrant_type().equalsIgnoreCase(GrantType.PASSWORD.getGrant())) {
      return getTokenResponseForPassword(authorizationHeader, tokenRequest);
    } else if (tokenRequest.getGrant_type().equalsIgnoreCase(GrantType.TOKEN_EXCHANGE.getGrant())) {
      return ResponseEntity.badRequest().body(new TokenResponse("unsupported_grant_type"));
    } else if (tokenRequest
        .getGrant_type()
        .equalsIgnoreCase(GrantType.AUTHORIZATION_CODE.getGrant())) {
      return getTokenResponseForAuthorizationCode(authorizationHeader, tokenRequest);
    } else if (tokenRequest.getGrant_type().equalsIgnoreCase(GrantType.REFRESH_TOKEN.getGrant())) {
      return getTokenResponseForRefreshToken(authorizationHeader, tokenRequest);
    } else {
      return ResponseEntity.badRequest().body(new TokenResponse("unsupported_grant_type"));
    }
  }

  /* ---------------------
  Access Token Request

  The client makes a request to the token endpoint by sending the
  following parameters using the "application/x-www-form-urlencoded"
  format per Appendix B with a character encoding of UTF-8 in the HTTP
  request entity-body:

  grant_type
        REQUIRED.  Value MUST be set to "authorization_code".

  code
        REQUIRED.  The authorization code received from the
        authorization server.

  redirect_uri
        REQUIRED, if the "redirect_uri" parameter was included in the
        authorization request as described in Section 4.1.1, and their
        values MUST be identical.

  client_id
        REQUIRED, if the client is not authenticating with the
        authorization server as described in Section 3.2.1.

  If the client type is confidential or the client was issued client
  credentials (or assigned other authentication requirements), the
  client MUST authenticate with the authorization server.
  */
  private ResponseEntity<TokenResponse> getTokenResponseForAuthorizationCode(
      String authorizationHeader, TokenRequest tokenRequest) throws JOSEException {

    ClientCredentials clientCredentials =
        retrieveClientCredentials(authorizationHeader, tokenRequest);

    if (clientCredentials == null) {
      return reportInvalidClientError();
    }

    AuthorizationCode authorizationCode = authorizationCodeService.getCode(tokenRequest.getCode());
    if (authorizationCode == null
        || !clientCredentials.getClientId().equals(authorizationCode.getClientId())) {
      return reportInvalidClientError();
    }

    RegisteredClient registeredClient =
        registeredClientService.findOneByClientId(clientCredentials.getClientId());

    if (!registeredClient.getGrantTypes().contains(GrantType.AUTHORIZATION_CODE)) {
      return reportInvalidGrantError();
    }

    if (registeredClient.isConfidential()) {
      if (StringUtils.isBlank(clientCredentials.getClientSecret())
          || !passwordEncoder.matches(clientCredentials.getClientSecret(), registeredClient.getClientSecret())) {
        return reportInvalidClientError();
      }
    } else {
      if (StringUtils.isNotBlank(tokenRequest.getCode_verifier())) {
        if (StringUtils.isBlank(authorizationCode.getCode_challenge_method())
            || "S256".equalsIgnoreCase(authorizationCode.getCode_challenge_method())) {
          // Rehash the code verifier
          try {
            String rehashedChallenge = rehashCodeVerifier(tokenRequest.getCode_verifier());
            if (!MessageDigest.isEqual(
                authorizationCode.getCode_challenge().getBytes(UTF_8), rehashedChallenge.getBytes(UTF_8))) {
              return reportInvalidGrantError();
            }
          } catch (NoSuchAlgorithmException e) {
            return ResponseEntity.badRequest().body(new TokenResponse("server_error"));
          }
        } else if ("plain".equalsIgnoreCase(authorizationCode.getCode_challenge_method())) {
          if (!authorizationCode.getCode_challenge().equals(tokenRequest.getCode_verifier())) {
            return reportInvalidGrantError();
          }
        }
      } else {
        return reportInvalidGrantError();
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

      Duration accessTokenLifetime = authorizationServerProperties.getAccessToken().getLifetime();
      Duration refreshTokenLifetime = authorizationServerProperties.getRefreshToken().getLifetime();
      Duration idTokenLifetime = authorizationServerProperties.getIdToken().getLifetime();

      return ResponseEntity.ok(
          new TokenResponse(
              AccessTokenFormat.JWT.equals(registeredClient.getAccessTokenFormat())
                  ? tokenService
                      .createPersonalizedJwtAccessToken(
                          user.get(),
                          authorizationCode.getClientId(),
                          authorizationCode.getNonce(),
                          accessTokenLifetime)
                      .getValue()
                  : tokenService
                      .createPersonalizedOpaqueAccessToken(
                          user.get(), authorizationCode.getClientId(), accessTokenLifetime)
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
      return reportInvalidGrantError();
    }
  }

  /* -------------------
  Access Token Request

  The client makes a request to the token endpoint by adding the
  following parameters using the "application/x-www-form-urlencoded"
  format per Appendix B with a character encoding of UTF-8 in the HTTP
  request entity-body:

  grant_type
        REQUIRED.  Value MUST be set to "client_credentials".

  scope
        OPTIONAL.  The scope of the access request as described by
        Section 3.3.

  The client MUST authenticate with the authorization server
  */
  private ResponseEntity<TokenResponse> getTokenResponseForClientCredentials(
      String authorizationHeader, TokenRequest tokenRequest) throws JOSEException {

    ClientCredentials clientCredentials =
        retrieveClientCredentials(authorizationHeader, tokenRequest);

    if (clientCredentials == null) {
      return reportInvalidClientError();
    }

    Duration accessTokenLifetime = authorizationServerProperties.getAccessToken().getLifetime();
    Duration refreshTokenLifetime = authorizationServerProperties.getRefreshToken().getLifetime();

    RegisteredClient registeredClient =
        registeredClientService.findOneByClientId(clientCredentials.getClientId());
    if (registeredClient != null
        && passwordEncoder.matches(clientCredentials.getClientSecret(), registeredClient.getClientSecret())) {
      if (registeredClient.getGrantTypes().contains(GrantType.CLIENT_CREDENTIALS)) {
        return ResponseEntity.ok(
            new TokenResponse(
                AccessTokenFormat.JWT.equals(registeredClient.getAccessTokenFormat())
                    ? tokenService
                        .createAnonymousJwtAccessToken(
                            clientCredentials.getClientId(), accessTokenLifetime)
                        .getValue()
                    : tokenService
                        .createAnonymousOpaqueAccessToken(
                            clientCredentials.getClientId(), accessTokenLifetime)
                        .getValue(),
                tokenService
                    .createRefreshToken(clientCredentials.getClientId(), refreshTokenLifetime)
                    .getValue(),
                accessTokenLifetime.toSeconds(),
                null));
      } else {
        return reportUnauthorizedClientError();
      }
    } else {
      return reportInvalidClientError();
    }
  }

  /* ------------------
  Access Token Request

  The client makes a request to the token endpoint by adding the
  following parameters using the "application/x-www-form-urlencoded"
  format per Appendix B with a character encoding of UTF-8 in the HTTP
  request entity-body:

  grant_type
        REQUIRED.  Value MUST be set to "password".

  username
        REQUIRED.  The resource owner username.

  password
        REQUIRED.  The resource owner password.

  scope
        OPTIONAL.  The scope of the access request as described by
        Section 3.3.

  If the client type is confidential or the client was issued client
  credentials (or assigned other authentication requirements), the
  client MUST authenticate with the authorization server
  */
  private ResponseEntity<TokenResponse> getTokenResponseForPassword(
      String authorizationHeader, TokenRequest tokenRequest) throws JOSEException {

    ClientCredentials clientCredentials =
        retrieveClientCredentials(authorizationHeader, tokenRequest);

    if (clientCredentials == null) {
      return reportInvalidClientError();
    }

    RegisteredClient registeredClient =
        registeredClientService.findOneByClientId(clientCredentials.getClientId());
    if (registeredClient != null
        && registeredClient.getClientSecret().equals(clientCredentials.getClientSecret())) {
      if (registeredClient.getGrantTypes().contains(GrantType.PASSWORD)) {

        User authenticatedUser;
        try {
          authenticatedUser =
              authenticationService.authenticate(
                  tokenRequest.getUsername(), tokenRequest.getPassword());
        } catch (BadCredentialsException ex) {
          return reportUnauthorizedClientError();
        }

        Duration accessTokenLifetime = authorizationServerProperties.getAccessToken().getLifetime();
        Duration refreshTokenLifetime = authorizationServerProperties.getRefreshToken().getLifetime();

        return ResponseEntity.ok(
            new TokenResponse(
                AccessTokenFormat.JWT.equals(registeredClient.getAccessTokenFormat())
                    ? tokenService
                        .createPersonalizedJwtAccessToken(
                            authenticatedUser,
                            clientCredentials.getClientId(),
                            null,
                            accessTokenLifetime)
                        .getValue()
                    : tokenService
                        .createPersonalizedOpaqueAccessToken(
                            authenticatedUser, clientCredentials.getClientId(), accessTokenLifetime)
                        .getValue(),
                tokenService
                    .createRefreshToken(clientCredentials.getClientId(), refreshTokenLifetime)
                    .getValue(),
                accessTokenLifetime.toSeconds(),
                null));
      } else {
        return reportUnauthorizedClientError();
      }
    } else {
      return reportInvalidClientError();
    }
  }

  /* -------------------------
  Refreshing an Access Token

  If the authorization server issued a refresh token to the client, the
  client makes a refresh request to the token endpoint by adding the
  following parameters using the "application/x-www-form-urlencoded"
  format per Appendix B with a character encoding of UTF-8 in the HTTP
  request entity-body:

  grant_type
        REQUIRED.  Value MUST be set to "refresh_token".
  refresh_token
        REQUIRED.  The refresh token issued to the client.
  scope
        OPTIONAL.  The scope of the access request as described by
        Section 3.3.  The requested scope MUST NOT include any scope
        not originally granted by the resource owner, and if omitted is
        treated as equal to the scope originally granted by the
        resource owner.
  */
  private ResponseEntity<TokenResponse> getTokenResponseForRefreshToken(
      String authorizationHeader, TokenRequest tokenRequest) throws JOSEException {

    ClientCredentials clientCredentials =
        retrieveClientCredentials(authorizationHeader, tokenRequest);

    if (clientCredentials == null) {
      return reportInvalidClientError();
    }

    Duration accessTokenLifetime = authorizationServerProperties.getAccessToken().getLifetime();
    Duration refreshTokenLifetime = authorizationServerProperties.getRefreshToken().getLifetime();

    RegisteredClient registeredClient =
        registeredClientService.findOneByClientId(clientCredentials.getClientId());
    if (registeredClient != null
        && registeredClient.getClientSecret().equals(clientCredentials.getClientSecret())) {
      if (registeredClient.getGrantTypes().contains(GrantType.REFRESH_TOKEN)) {
        OpaqueToken opaqueWebToken =
            tokenService.findOpaqueWebToken(tokenRequest.getRefresh_token());
        if (opaqueWebToken != null && opaqueWebToken.isRefreshToken()) {
          Optional<User> authenticatedUser =
              userService.findOneByIdentifier(UUID.fromString(opaqueWebToken.getSubject()));
          if (authenticatedUser.isPresent()) {
            // TODO: Remove refresh token
            return ResponseEntity.ok(
                new TokenResponse(
                    AccessTokenFormat.JWT.equals(registeredClient.getAccessTokenFormat())
                        ? tokenService
                            .createPersonalizedJwtAccessToken(
                                authenticatedUser.get(),
                                clientCredentials.getClientId(),
                                null,
                                accessTokenLifetime)
                            .getValue()
                        : tokenService
                            .createAnonymousOpaqueAccessToken(
                                clientCredentials.getClientId(), accessTokenLifetime)
                            .getValue(),
                    tokenService
                        .createRefreshToken(clientCredentials.getClientId(), refreshTokenLifetime)
                        .getValue(),
                    accessTokenLifetime.toSeconds(),
                    null));
          }
        }
        return reportInvalidClientError();
      } else {
        return reportUnauthorizedClientError();
      }
    } else {
      return reportInvalidClientError();
    }
  }

  private ClientCredentials retrieveClientCredentials(
      String authorizationHeader, TokenRequest tokenRequest) {
    ClientCredentials clientCredentials = null;
    if (authorizationHeader != null) {
      clientCredentials = AuthenticationUtil.fromBasicAuthHeader(authorizationHeader);
    } else if (StringUtils.isNotBlank(tokenRequest.getClient_id())) {
      clientCredentials =
          new ClientCredentials(tokenRequest.getClient_id(), tokenRequest.getClient_secret());
    }
    return clientCredentials;
  }

  private ResponseEntity<TokenResponse> reportUnauthorizedClientError() {
    return ResponseEntity.badRequest().body(new TokenResponse("unauthorized_client"));
  }

  private String rehashCodeVerifier(String codeVerifier) throws NoSuchAlgorithmException {
    final MessageDigest digest = MessageDigest.getInstance("SHA-256");
    final byte[] hashedBytes = digest.digest(codeVerifier.getBytes(UTF_8));
    return Base64.getUrlEncoder().withoutPadding().encodeToString(hashedBytes);
  }

  private ResponseEntity<TokenResponse> reportInvalidClientError() {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .header("WWW-Authenticate", "Basic")
        .body(new TokenResponse("invalid_client"));
  }

  private ResponseEntity<TokenResponse> reportInvalidGrantError() {
    return ResponseEntity.badRequest().body(new TokenResponse("invalid_grant"));
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
