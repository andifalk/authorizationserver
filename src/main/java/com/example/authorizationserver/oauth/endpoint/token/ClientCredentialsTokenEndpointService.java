package com.example.authorizationserver.oauth.endpoint.token;

import com.example.authorizationserver.config.AuthorizationServerConfigurationProperties;
import com.example.authorizationserver.oauth.client.RegisteredClientService;
import com.example.authorizationserver.oauth.client.model.AccessTokenFormat;
import com.example.authorizationserver.oauth.common.ClientCredentials;
import com.example.authorizationserver.oauth.common.GrantType;
import com.example.authorizationserver.oauth.endpoint.resource.TokenRequest;
import com.example.authorizationserver.oauth.endpoint.resource.TokenResponse;
import com.example.authorizationserver.token.store.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;

import static com.example.authorizationserver.oauth.endpoint.resource.TokenResponse.BEARER_TOKEN_TYPE;

@Service
public class ClientCredentialsTokenEndpointService {
  private static final Logger LOG = LoggerFactory.getLogger(ClientCredentialsTokenEndpointService.class);

  private final RegisteredClientService registeredClientService;
  private final TokenService tokenService;
  private final PasswordEncoder passwordEncoder;
  private final AuthorizationServerConfigurationProperties authorizationServerProperties;

  public ClientCredentialsTokenEndpointService(
          TokenService tokenService,
          RegisteredClientService registeredClientService, PasswordEncoder passwordEncoder,
          AuthorizationServerConfigurationProperties authorizationServerProperties) {
    this.tokenService = tokenService;
    this.registeredClientService = registeredClientService;
    this.passwordEncoder = passwordEncoder;
    this.authorizationServerProperties = authorizationServerProperties;
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
  public ResponseEntity<TokenResponse> getTokenResponseForClientCredentials(
      String authorizationHeader, TokenRequest tokenRequest) {

    LOG.debug("Exchange token for 'client credentials' with [{}]", tokenRequest);

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
          if (registeredClient.getGrantTypes().contains(GrantType.CLIENT_CREDENTIALS)) {
            LOG.info("Creating token response for client credentials for client [{}]", tokenRequest.getClient_id());
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
                                    .createAnonymousRefreshToken(clientCredentials.getClientId(), refreshTokenLifetime)
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
