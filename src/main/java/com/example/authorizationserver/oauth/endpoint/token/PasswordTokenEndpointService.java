package com.example.authorizationserver.oauth.endpoint.token;

import com.example.authorizationserver.config.AuthorizationServerConfigurationProperties;
import com.example.authorizationserver.oauth.client.model.AccessTokenFormat;
import com.example.authorizationserver.oauth.client.model.RegisteredClient;
import com.example.authorizationserver.oauth.common.ClientCredentials;
import com.example.authorizationserver.oauth.common.GrantType;
import com.example.authorizationserver.oauth.endpoint.token.resource.TokenRequest;
import com.example.authorizationserver.oauth.endpoint.token.resource.TokenResponse;
import com.example.authorizationserver.scim.model.ScimUserEntity;
import com.example.authorizationserver.security.client.RegisteredClientAuthenticationService;
import com.example.authorizationserver.security.user.UserAuthenticationService;
import com.example.authorizationserver.token.store.TokenService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.example.authorizationserver.oauth.endpoint.token.resource.TokenResponse.BEARER_TOKEN_TYPE;

@Service
public class PasswordTokenEndpointService {
    private static final Logger LOG = LoggerFactory.getLogger(PasswordTokenEndpointService.class);

    private final UserAuthenticationService userAuthenticationService;
    private final TokenService tokenService;
    private final AuthorizationServerConfigurationProperties authorizationServerProperties;
    private final RegisteredClientAuthenticationService registeredClientAuthenticationService;

    public PasswordTokenEndpointService(
            UserAuthenticationService userAuthenticationService,
            TokenService tokenService,
            AuthorizationServerConfigurationProperties authorizationServerProperties,
            RegisteredClientAuthenticationService registeredClientAuthenticationService) {
        this.userAuthenticationService = userAuthenticationService;
        this.tokenService = tokenService;
        this.authorizationServerProperties = authorizationServerProperties;
        this.registeredClientAuthenticationService = registeredClientAuthenticationService;
    }

    /**
     * ------------------ Access Token Request
     *
     * <p>The client makes a request to the token endpoint by adding the following parameters using
     * the "application/x-www-form-urlencoded" format per Appendix B with a character encoding of
     * UTF-8 in the HTTP request entity-body:
     *
     * <p>grant_type REQUIRED. Value MUST be set to "password".
     *
     * <p>username REQUIRED. The resource owner username.
     *
     * <p>password REQUIRED. The resource owner password.
     *
     * <p>scope OPTIONAL. The scope of the access request as described by Section 3.3.
     *
     * <p>If the client type is confidential or the client was issued client credentials (or assigned
     * other authentication requirements), the client MUST authenticate with the authorization server
     */
    public ResponseEntity<TokenResponse> getTokenResponseForPassword(
            String authorizationHeader, TokenRequest tokenRequest) {

        LOG.debug("Exchange token for resource owner password with [{}]", tokenRequest);

        ClientCredentials clientCredentials =
                TokenEndpointHelper.retrieveClientCredentials(authorizationHeader, tokenRequest);

        if (clientCredentials == null) {
            return TokenEndpointHelper.reportInvalidClientError();
        }

        RegisteredClient registeredClient;

        try {
            registeredClient =
                    registeredClientAuthenticationService.authenticate(
                            clientCredentials.getClientId(), clientCredentials.getClientSecret());

        } catch (AuthenticationException ex) {
            return TokenEndpointHelper.reportInvalidClientError();
        }

        if (registeredClient.getGrantTypes().contains(GrantType.PASSWORD)) {

            ScimUserEntity authenticatedUser;
            try {
                authenticatedUser =
                        userAuthenticationService.authenticate(
                                tokenRequest.getUsername(), tokenRequest.getPassword());
            } catch (AuthenticationException ex) {
                return TokenEndpointHelper.reportUnauthorizedClientError();
            }

            Duration accessTokenLifetime = authorizationServerProperties.getAccessToken().getLifetime();
            Duration refreshTokenLifetime = authorizationServerProperties.getRefreshToken().getLifetime();

            Set<String> scopes = new HashSet<>();
            if (StringUtils.isNotBlank(tokenRequest.getScope())) {
                scopes = new HashSet<>(Arrays.asList(tokenRequest.getScope().split(" ")));
            }

            LOG.info(
                    "Creating token response for client credentials for client [{}]",
                    clientCredentials.getClientId());

            return ResponseEntity.ok(
                    new TokenResponse(
                            AccessTokenFormat.JWT.equals(registeredClient.getAccessTokenFormat())
                                    ? tokenService
                                    .createPersonalizedJwtAccessToken(
                                            authenticatedUser,
                                            clientCredentials.getClientId(),
                                            null,
                                            scopes,
                                            accessTokenLifetime)
                                    .getValue()
                                    : tokenService
                                    .createPersonalizedOpaqueAccessToken(
                                            authenticatedUser, clientCredentials.getClientId(), scopes, accessTokenLifetime)
                                    .getValue(),
                            tokenService
                                    .createPersonalizedRefreshToken(
                                            clientCredentials.getClientId(), authenticatedUser, scopes, refreshTokenLifetime)
                                    .getValue(),
                            accessTokenLifetime.toSeconds(),
                            null,
                            BEARER_TOKEN_TYPE));
        } else {
            return TokenEndpointHelper.reportUnauthorizedClientError();
        }
    }
}
