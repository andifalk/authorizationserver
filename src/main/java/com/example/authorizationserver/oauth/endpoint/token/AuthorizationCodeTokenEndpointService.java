package com.example.authorizationserver.oauth.endpoint.token;

import com.example.authorizationserver.config.AuthorizationServerConfigurationProperties;
import com.example.authorizationserver.oauth.client.RegisteredClientService;
import com.example.authorizationserver.oauth.client.model.AccessTokenFormat;
import com.example.authorizationserver.oauth.client.model.RegisteredClient;
import com.example.authorizationserver.oauth.common.ClientCredentials;
import com.example.authorizationserver.oauth.common.GrantType;
import com.example.authorizationserver.oauth.endpoint.token.resource.TokenRequest;
import com.example.authorizationserver.oauth.endpoint.token.resource.TokenResponse;
import com.example.authorizationserver.oauth.pkce.CodeChallengeError;
import com.example.authorizationserver.oauth.pkce.ProofKeyForCodeExchangeVerifier;
import com.example.authorizationserver.oauth.store.AuthorizationCode;
import com.example.authorizationserver.oauth.store.AuthorizationCodeService;
import com.example.authorizationserver.oidc.common.Scope;
import com.example.authorizationserver.scim.model.ScimUserEntity;
import com.example.authorizationserver.scim.service.ScimService;
import com.example.authorizationserver.security.client.RegisteredClientAuthenticationService;
import com.example.authorizationserver.token.store.TokenService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.example.authorizationserver.oauth.endpoint.token.resource.TokenResponse.BEARER_TOKEN_TYPE;

@Service
public class AuthorizationCodeTokenEndpointService {
    private static final Logger LOG =
            LoggerFactory.getLogger(AuthorizationCodeTokenEndpointService.class);

    private final RegisteredClientService registeredClientService;
    private final AuthorizationCodeService authorizationCodeService;
    private final TokenService tokenService;
    private final ScimService scimService;
    private final ProofKeyForCodeExchangeVerifier proofKeyForCodeExchangeVerifier;
    private final AuthorizationServerConfigurationProperties authorizationServerProperties;
    private final RegisteredClientAuthenticationService registeredClientAuthenticationService;

    public AuthorizationCodeTokenEndpointService(
            TokenService tokenService,
            RegisteredClientService registeredClientService,
            AuthorizationCodeService authorizationCodeService,
            ScimService scimService, ProofKeyForCodeExchangeVerifier proofKeyForCodeExchangeVerifier,
            AuthorizationServerConfigurationProperties authorizationServerProperties,
            RegisteredClientAuthenticationService registeredClientAuthenticationService) {
        this.tokenService = tokenService;
        this.registeredClientService = registeredClientService;
        this.authorizationCodeService = authorizationCodeService;
        this.scimService = scimService;
        this.proofKeyForCodeExchangeVerifier = proofKeyForCodeExchangeVerifier;
        this.authorizationServerProperties = authorizationServerProperties;
        this.registeredClientAuthenticationService = registeredClientAuthenticationService;
    }

    /* ---------------------
      Access Token Request with Authorization Code.

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

      code_verifier
            REQUIRED. Code verifier as specified by PKCE

      If the client type is confidential or the client was issued client
      credentials (or assigned other authentication requirements), the
      client MUST authenticate with the authorization server.
    */
    public ResponseEntity<TokenResponse> getTokenResponseForAuthorizationCode(
            String authorizationHeader, TokenRequest tokenRequest) {

        LOG.debug("Exchange token for 'authorization code' with [{}]", tokenRequest);

        ClientCredentials clientCredentials =
                TokenEndpointHelper.retrieveClientCredentials(authorizationHeader, tokenRequest);

        if (clientCredentials == null) {
            return TokenEndpointHelper.reportInvalidClientError();
        }

        AuthorizationCode authorizationCode = authorizationCodeService.getCode(tokenRequest.getCode());

        if (authorizationCode == null) {
            LOG.warn("Invalid authorization code");
            return TokenEndpointHelper.reportInvalidClientError();
        }

        if (authorizationCode.isExpired()) {
            LOG.warn("Authorization code already expired");
            authorizationCodeService.removeCode(authorizationCode.getCode());
            return TokenEndpointHelper.reportInvalidClientError();
        }

        if (!clientCredentials.getClientId().equals(authorizationCode.getClientId())) {
            LOG.warn(
                    "Client id mismatch for authorization code, [{}] does not match [{}]",
                    authorizationCode.getClientId(),
                    clientCredentials.getClientId());
            return TokenEndpointHelper.reportInvalidClientError();
        }

        return registeredClientService
                .findOneByClientId(clientCredentials.getClientId())
                .map(
                        registeredClient -> {
                            if (!registeredClient.getGrantTypes().contains(GrantType.AUTHORIZATION_CODE)) {
                                return TokenEndpointHelper.reportInvalidGrantError();
                            }

                            if (StringUtils.isNotBlank(authorizationCode.getCode_challenge())) {
                                if (StringUtils.isBlank(tokenRequest.getCode_verifier())) {
                                    LOG.warn("Code verifier is required for code challenge");
                                    return TokenEndpointHelper.reportInvalidClientError();
                                }
                                try {
                                    proofKeyForCodeExchangeVerifier.verifyCodeChallenge(
                                            authorizationCode.getCode_challenge_method(),
                                            tokenRequest.getCode_verifier(),
                                            authorizationCode.getCode_challenge());
                                } catch (CodeChallengeError ex) {
                                    LOG.warn("PKCE challenge verification failed", ex);
                                    return TokenEndpointHelper.reportUnauthorizedClientError();
                                }
                            } else {
                                // Public clients require PKCE
                                if (!registeredClient.isConfidential()
                                        && StringUtils.isBlank(authorizationCode.getCode_challenge())) {
                                    LOG.warn(
                                            "PKCE with code challenge is required for public client [{}]",
                                            registeredClient.getClientId());
                                    return TokenEndpointHelper.reportInvalidClientError();
                                }

                                // Confidential clients must present the client secret if PKCE is not used
                                try {
                                    registeredClientAuthenticationService.authenticate(
                                            clientCredentials.getClientId(), clientCredentials.getClientSecret());
                                } catch (AuthenticationException ex) {
                                    return TokenEndpointHelper.reportInvalidClientError();
                                }
                            }

                            return scimService
                                    .findUserByIdentifier(UUID.fromString(authorizationCode.getSubject()))
                                    .map(
                                            user -> {
                                                LOG.info(
                                                        "Creating token response for user {}, client id {} and scopes {}",
                                                        user.getUserName(),
                                                        authorizationCode.getClientId(),
                                                        authorizationCode.getScopes());

                                                Duration accessTokenLifetime =
                                                        authorizationServerProperties.getAccessToken().getLifetime();
                                                Duration refreshTokenLifetime =
                                                        authorizationServerProperties.getRefreshToken().getLifetime();
                                                Duration idTokenLifetime =
                                                        authorizationServerProperties.getIdToken().getLifetime();

                                                authorizationCodeService.removeCode(authorizationCode.getCode());

                                                return ResponseEntity.ok(
                                                        createTokenResponse(
                                                                authorizationCode,
                                                                registeredClient,
                                                                user,
                                                                accessTokenLifetime,
                                                                refreshTokenLifetime,
                                                                idTokenLifetime));
                                            })
                                    .orElse(TokenEndpointHelper.reportInvalidGrantError());
                        })
                .orElse(TokenEndpointHelper.reportInvalidGrantError());
    }

    private TokenResponse createTokenResponse(
            AuthorizationCode authorizationCode,
            RegisteredClient registeredClient,
            ScimUserEntity user,
            Duration accessTokenLifetime,
            Duration refreshTokenLifetime,
            Duration idTokenLifetime) {
        return new TokenResponse(
                createAccessToken(authorizationCode, registeredClient, user, accessTokenLifetime),
                createRefreshToken(authorizationCode, user, refreshTokenLifetime),
                accessTokenLifetime.toSeconds(),
                createIdToken(authorizationCode, user, idTokenLifetime),
                BEARER_TOKEN_TYPE);
    }

    private String createIdToken(
            AuthorizationCode authorizationCode, ScimUserEntity user, Duration idTokenLifetime) {
        return authorizationCode.getScopes().stream()
                .map(String::toUpperCase)
                .collect(Collectors.toList())
                .contains(Scope.OPENID.name())
                ? tokenService
                .createIdToken(
                        user,
                        authorizationCode.getClientId(),
                        authorizationCode.getNonce(),
                        authorizationCode.getScopes(),
                        idTokenLifetime)
                .getValue()
                : null;
    }

    private String createRefreshToken(
            AuthorizationCode authorizationCode, ScimUserEntity user, Duration refreshTokenLifetime) {
        return tokenService
                .createPersonalizedRefreshToken(authorizationCode.getClientId(), user, authorizationCode.getScopes(), refreshTokenLifetime)
                .getValue();
    }

    private String createAccessToken(
            AuthorizationCode authorizationCode,
            RegisteredClient registeredClient,
            ScimUserEntity user,
            Duration accessTokenLifetime) {
        return AccessTokenFormat.JWT.equals(registeredClient.getAccessTokenFormat())
                ? tokenService
                .createPersonalizedJwtAccessToken(
                        user,
                        authorizationCode.getClientId(),
                        authorizationCode.getNonce(),
                        authorizationCode.getScopes(),
                        accessTokenLifetime)
                .getValue()
                : tokenService
                .createPersonalizedOpaqueAccessToken(
                        user, authorizationCode.getClientId(), authorizationCode.getScopes(), accessTokenLifetime)
                .getValue();
    }
}
