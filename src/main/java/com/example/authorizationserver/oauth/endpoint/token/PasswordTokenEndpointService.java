package com.example.authorizationserver.oauth.endpoint.token;

import com.example.authorizationserver.authentication.AuthenticationService;
import com.example.authorizationserver.config.AuthorizationServerConfigurationProperties;
import com.example.authorizationserver.oauth.client.RegisteredClientService;
import com.example.authorizationserver.oauth.client.model.AccessTokenFormat;
import com.example.authorizationserver.oauth.common.ClientCredentials;
import com.example.authorizationserver.oauth.common.GrantType;
import com.example.authorizationserver.oauth.endpoint.resource.TokenRequest;
import com.example.authorizationserver.oauth.endpoint.resource.TokenResponse;
import com.example.authorizationserver.token.store.TokenService;
import com.example.authorizationserver.user.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;

import static com.example.authorizationserver.oauth.endpoint.resource.TokenResponse.BEARER_TOKEN_TYPE;

@Service
public class PasswordTokenEndpointService {
  private static final Logger LOG = LoggerFactory.getLogger(PasswordTokenEndpointService.class);

  private final AuthenticationService authenticationService;
  private final RegisteredClientService registeredClientService;
  private final TokenService tokenService;
  private final PasswordEncoder passwordEncoder;
  private final AuthorizationServerConfigurationProperties authorizationServerProperties;

  public PasswordTokenEndpointService(
          AuthenticationService authenticationService, TokenService tokenService,
          RegisteredClientService registeredClientService, PasswordEncoder passwordEncoder,
          AuthorizationServerConfigurationProperties authorizationServerProperties) {
    this.authenticationService = authenticationService;
    this.tokenService = tokenService;
    this.registeredClientService = registeredClientService;
    this.passwordEncoder = passwordEncoder;
    this.authorizationServerProperties = authorizationServerProperties;
  }

  /** ------------------
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
  public ResponseEntity<TokenResponse> getTokenResponseForPassword(
          String authorizationHeader, TokenRequest tokenRequest) {

    LOG.debug("Exchange token for resource owner password with [{}]", tokenRequest);

    ClientCredentials clientCredentials =
            TokenEndpointHelper.retrieveClientCredentials(authorizationHeader, tokenRequest);

    if (clientCredentials == null) {
      return TokenEndpointHelper.reportInvalidClientError();
    }

    return registeredClientService.findOneByClientId(clientCredentials.getClientId()).map(
            registeredClient -> {
              if (passwordEncoder.matches(clientCredentials.getClientSecret(), registeredClient.getClientSecret())) {
                if (registeredClient.getGrantTypes().contains(GrantType.PASSWORD)) {

                  User authenticatedUser;
                  try {
                    authenticatedUser =
                            authenticationService.authenticate(
                                    tokenRequest.getUsername(), tokenRequest.getPassword());
                  } catch (BadCredentialsException ex) {
                    return TokenEndpointHelper.reportUnauthorizedClientError();
                  }

                  Duration accessTokenLifetime = authorizationServerProperties.getAccessToken().getLifetime();
                  Duration refreshTokenLifetime = authorizationServerProperties.getRefreshToken().getLifetime();

                  LOG.info("Creating token response for client credentials for client [{}]", clientCredentials.getClientId());

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
                                          .createPersonalizedRefreshToken(clientCredentials.getClientId(), authenticatedUser, refreshTokenLifetime)
                                          .getValue(),
                                  accessTokenLifetime.toSeconds(),
                                  null, BEARER_TOKEN_TYPE));
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
