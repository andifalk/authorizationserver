package com.example.authorizationserver.oauth.endpoint.token;

import com.example.authorizationserver.config.AuthorizationServerConfigurationProperties;
import com.example.authorizationserver.oauth.client.RegisteredClientService;
import com.example.authorizationserver.oauth.client.model.AccessTokenFormat;
import com.example.authorizationserver.oauth.common.ClientCredentials;
import com.example.authorizationserver.oauth.common.GrantType;
import com.example.authorizationserver.oauth.endpoint.resource.TokenRequest;
import com.example.authorizationserver.oauth.endpoint.resource.TokenResponse;
import com.example.authorizationserver.token.store.TokenService;
import com.example.authorizationserver.token.store.model.OpaqueToken;
import com.example.authorizationserver.user.model.User;
import com.example.authorizationserver.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

import static com.example.authorizationserver.oauth.endpoint.resource.TokenResponse.BEARER_TOKEN_TYPE;

@Service
public class RefreshTokenEndpointService {
  private static final Logger LOG = LoggerFactory.getLogger(RefreshTokenEndpointService.class);

  private final RegisteredClientService registeredClientService;
  private final TokenService tokenService;
  private final PasswordEncoder passwordEncoder;
  private final UserService userService;
  private final AuthorizationServerConfigurationProperties authorizationServerProperties;

  public RefreshTokenEndpointService(
          TokenService tokenService,
          RegisteredClientService registeredClientService, PasswordEncoder passwordEncoder,
          UserService userService, AuthorizationServerConfigurationProperties authorizationServerProperties) {
    this.tokenService = tokenService;
    this.registeredClientService = registeredClientService;
    this.passwordEncoder = passwordEncoder;
    this.userService = userService;
    this.authorizationServerProperties = authorizationServerProperties;
  }


  /** -------------------------
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
  public ResponseEntity<TokenResponse> getTokenResponseForRefreshToken(
          String authorizationHeader, TokenRequest tokenRequest) {

    LOG.debug("Exchange token for refresh token with [{}]", tokenRequest);

    ClientCredentials clientCredentials =
            TokenEndpointHelper.retrieveClientCredentials(authorizationHeader, tokenRequest);

    if (clientCredentials == null) {
      return TokenEndpointHelper.reportInvalidClientError();
    }

    Duration accessTokenLifetime = authorizationServerProperties.getAccessToken().getLifetime();
    Duration refreshTokenLifetime = authorizationServerProperties.getRefreshToken().getLifetime();

    return registeredClientService.findOneByClientId(clientCredentials.getClientId()).map(
            registeredClient -> {
              if (passwordEncoder.matches(clientCredentials.getClientSecret(), registeredClient.getClientSecret())) {
                if (registeredClient.getGrantTypes().contains(GrantType.REFRESH_TOKEN)) {
                  OpaqueToken opaqueWebToken =
                          tokenService.findOpaqueWebToken(tokenRequest.getRefresh_token());
                  if (opaqueWebToken != null && opaqueWebToken.isRefreshToken()) {
                    opaqueWebToken.validate();
                    String subject = opaqueWebToken.getSubject();
                    if (TokenService.ANONYMOUS_TOKEN.equals(subject)) {

                      LOG.info("Creating anonymous token response for refresh token with client [{}]", tokenRequest.getClient_id());

                      return ResponseEntity.ok(
                              new TokenResponse(
                                      AccessTokenFormat.JWT.equals(registeredClient.getAccessTokenFormat())
                                              ? tokenService
                                              .createAnonymousJwtAccessToken(
                                                      clientCredentials.getClientId(),
                                                      accessTokenLifetime)
                                              .getValue()
                                              : tokenService
                                              .createAnonymousOpaqueAccessToken(
                                                      clientCredentials.getClientId(),
                                                      accessTokenLifetime)
                                              .getValue(),
                                      tokenService
                                              .createAnonymousRefreshToken(
                                                      clientCredentials.getClientId(),
                                                      refreshTokenLifetime)
                                              .getValue(),
                                      accessTokenLifetime.toSeconds(),
                                      null, BEARER_TOKEN_TYPE));
                    } else {
                      Optional<User> authenticatedUser =
                              userService.findOneByIdentifier(UUID.fromString(opaqueWebToken.getSubject()));
                      if (authenticatedUser.isPresent()) {

                        LOG.info("Creating personalized token response for refresh token with client [{}]", tokenRequest.getClient_id());

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
                                                .createPersonalizedOpaqueAccessToken(
                                                        authenticatedUser.get(),
                                                        clientCredentials.getClientId(),
                                                        accessTokenLifetime)
                                                .getValue(),
                                        tokenService
                                                .createPersonalizedRefreshToken(
                                                        clientCredentials.getClientId(),
                                                        authenticatedUser.get(),
                                                        refreshTokenLifetime)
                                                .getValue(),
                                        accessTokenLifetime.toSeconds(),
                                        null, BEARER_TOKEN_TYPE));
                      }
                    }
                    tokenService.remove(opaqueWebToken);
                  }
                  return TokenEndpointHelper.reportInvalidClientError();
                } else {
                  return TokenEndpointHelper.reportUnauthorizedClientError();
                }
              } else {
                return TokenEndpointHelper.reportInvalidClientError();
              }
            }
    ).orElse(TokenEndpointHelper.reportInvalidClientError());
  }
}
