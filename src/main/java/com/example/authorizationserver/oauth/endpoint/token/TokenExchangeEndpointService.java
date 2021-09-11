package com.example.authorizationserver.oauth.endpoint.token;

import com.example.authorizationserver.config.AuthorizationServerConfigurationProperties;
import com.example.authorizationserver.oauth.client.model.AccessTokenFormat;
import com.example.authorizationserver.oauth.client.model.RegisteredClient;
import com.example.authorizationserver.oauth.common.ClientCredentials;
import com.example.authorizationserver.oauth.common.GrantType;
import com.example.authorizationserver.oauth.common.TokenType;
import com.example.authorizationserver.oauth.endpoint.token.resource.TokenRequest;
import com.example.authorizationserver.oauth.endpoint.token.resource.TokenResponse;
import com.example.authorizationserver.oidc.common.Scope;
import com.example.authorizationserver.scim.model.ScimUserEntity;
import com.example.authorizationserver.scim.service.ScimService;
import com.example.authorizationserver.security.client.RegisteredClientAuthenticationService;
import com.example.authorizationserver.token.jwt.JsonWebTokenService;
import com.example.authorizationserver.token.store.TokenService;
import com.example.authorizationserver.token.store.model.JsonWebToken;
import com.example.authorizationserver.token.store.model.OpaqueToken;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.authorizationserver.oauth.endpoint.token.resource.TokenResponse.BEARER_TOKEN_TYPE;

@Service
public class TokenExchangeEndpointService {
    private static final Logger LOG = LoggerFactory.getLogger(TokenExchangeEndpointService.class);

    private final TokenService tokenService;
    private final ScimService scimService;
    private final AuthorizationServerConfigurationProperties authorizationServerProperties;
    private final RegisteredClientAuthenticationService registeredClientAuthenticationService;
    private final JsonWebTokenService jsonWebTokenService;

    public TokenExchangeEndpointService(
            TokenService tokenService,
            ScimService scimService, AuthorizationServerConfigurationProperties authorizationServerProperties,
            RegisteredClientAuthenticationService registeredClientAuthenticationService, JsonWebTokenService jsonWebTokenService) {
        this.tokenService = tokenService;
        this.scimService = scimService;
        this.authorizationServerProperties = authorizationServerProperties;
        this.registeredClientAuthenticationService = registeredClientAuthenticationService;
        this.jsonWebTokenService = jsonWebTokenService;
    }

    /**
     * ------------------------- Exchanging a Token
     *
     * <p>The client makes a token exchange request to the token endpoint with an extension grant type using the HTTP POST method.
     * The following parameters are included in the HTTP request entity-body using the application/x-www-form-urlencoded format per
     * Appendix B with a character encoding of UTF-8 in the HTTP request entity-body:
     *
     * <p>grant_type REQUIRED. Value MUST be set to "urn:ietf:params:oauth:grant-type:token-exchange".
     * refresh_token REQUIRED. The refresh token issued to the client. scope OPTIONAL. The scope of the access request as
     * described by Section 3.3. The requested scope MUST NOT include any scope not originally granted
     * by the resource owner, and if omitted is treated as equal to the scope originally granted by
     * the resource owner.
     */
    public ResponseEntity<TokenResponse> getTokenResponseForTokenExchange(
            String authorizationHeader, TokenRequest tokenRequest) {

        LOG.debug("Exchange token for given token with [{}]", tokenRequest);

        ClientCredentials clientCredentials =
                TokenEndpointHelper.retrieveClientCredentials(authorizationHeader, tokenRequest);

        if (clientCredentials == null) {
            LOG.debug("No client credentials provided");
            return TokenEndpointHelper.reportInvalidClientError();
        }

        Duration accessTokenLifetime = authorizationServerProperties.getAccessToken().getLifetime();
        Duration idTokenLifetime = authorizationServerProperties.getIdToken().getLifetime();
        Duration refreshTokenLifetime = authorizationServerProperties.getRefreshToken().getLifetime();

        RegisteredClient registeredClient;

        try {
            registeredClient =
                    registeredClientAuthenticationService.authenticate(
                            clientCredentials.getClientId(), clientCredentials.getClientSecret());

        } catch (AuthenticationException ex) {
            return TokenEndpointHelper.reportInvalidClientError();
        }

        if (registeredClient.getGrantTypes().contains(GrantType.TOKEN_EXCHANGE)) {
            TokenType requestedTokenType = TokenType.ACCESS_TOKEN;
            TokenType subjectTokenType;
            if (tokenRequest.getSubject_token_type() != null) {
                try {
                    subjectTokenType = TokenType.getTokenTypeForIdentifier(tokenRequest.getSubject_token_type());
                } catch (IllegalArgumentException ex) {
                    LOG.warn("Token exchange is not valid for subject token type [{}]", tokenRequest.getSubject_token_type());
                    return TokenEndpointHelper.reportInvalidRequestError();
                }
            } else {
                LOG.warn("Required parameter [subject_token_type] is missing in request");
                return TokenEndpointHelper.reportInvalidRequestError();
            }
            if (tokenRequest.getRequested_token_type() != null) {
                try {
                    requestedTokenType = TokenType.getTokenTypeForIdentifier(tokenRequest.getRequested_token_type());
                } catch (IllegalArgumentException ex) {
                    LOG.warn("Token exchange is not valid for requested token type [{}]", tokenRequest.getRequested_token_type());
                    return TokenEndpointHelper.reportInvalidRequestError();
                }
            }
            if (!(TokenType.ACCESS_TOKEN.equals(requestedTokenType) || TokenType.ID_TOKEN.equals(requestedTokenType))) {
                LOG.warn("Token exchange is not supported for requested token type [{}]", tokenRequest.getRequested_token_type());
                return TokenEndpointHelper.reportInvalidRequestError();
            }

            if (TokenType.REFRESH_TOKEN.equals(subjectTokenType)) {
                return exchangeRefreshToken(tokenRequest, accessTokenLifetime, idTokenLifetime, refreshTokenLifetime, registeredClient, requestedTokenType);
            } else if (TokenType.JWT_TOKEN.equals(subjectTokenType)) {
                return exchangeJWT(tokenRequest, accessTokenLifetime, idTokenLifetime, refreshTokenLifetime, registeredClient, subjectTokenType, requestedTokenType);
            } else if (TokenType.ACCESS_TOKEN.equals(subjectTokenType)) {
                return exchangeAccessToken(tokenRequest, accessTokenLifetime, idTokenLifetime, refreshTokenLifetime, registeredClient, subjectTokenType, requestedTokenType);
            } else if (TokenType.ID_TOKEN.equals(subjectTokenType)) {
                return exchangeIDToken(tokenRequest, accessTokenLifetime, idTokenLifetime, refreshTokenLifetime, registeredClient, subjectTokenType);
            } else {
                LOG.warn("Unsupported subject token type {}", subjectTokenType);
                return TokenEndpointHelper.reportInvalidRequestError();
            }
        } else {
            return TokenEndpointHelper.reportUnauthorizedClientError();
        }
    }

    private ResponseEntity<TokenResponse> exchangeJWT(
            TokenRequest tokenRequest,
            Duration accessTokenLifetime,
            Duration idTokenLifetime,
            Duration refreshTokenLifetime,
            RegisteredClient registeredClient,
            TokenType subjectTokenType,
            TokenType requestedTokenType) {
        JsonWebToken jsonWebToken = tokenService.findJsonWebToken(tokenRequest.getSubject_token());
        if (jsonWebToken != null) {
            if (isNotValidForSubjectType(jsonWebToken, subjectTokenType)) {
                LOG.warn("Type of given token is not valid for given subject token type {}", tokenRequest.getSubject_token_type());
                return TokenEndpointHelper.reportInvalidRequestError();
            }
            Set<String> scopes = new HashSet<>();
            if (StringUtils.isNotBlank(tokenRequest.getScope())) {
                scopes = new HashSet<>(Arrays.asList(tokenRequest.getScope().split(" ")));
                scopes = scopes.stream().map(String::toUpperCase).collect(Collectors.toUnmodifiableSet());
            }

            try {
                JWTClaimsSet jwtClaimsSet =
                        jsonWebTokenService.parseAndValidateToken(jsonWebToken.getValue());
                String subject = jwtClaimsSet.getSubject();
                String ctx = jwtClaimsSet.getStringClaim("ctx");
                if (TokenService.ANONYMOUS_TOKEN.equals(ctx)) {

                    LOG.info(
                            "Creating anonymous token response for token exchange with client [{}]",
                            registeredClient.getClientId());

                    TokenResponse tokenResponse = createAnonymousTokenResponse(tokenRequest, registeredClient, scopes, accessTokenLifetime, refreshTokenLifetime, requestedTokenType);
                    LOG.debug(
                            "Token response for token exchange [{}]", tokenResponse);
                    return ResponseEntity.ok(tokenResponse);
                } else {
                    Optional<ScimUserEntity> authenticatedUser =
                            scimService.findUserByIdentifier(UUID.fromString(subject));
                    if (authenticatedUser.isPresent()) {

                        LOG.info(
                                "Creating personalized token response for token exchange with client [{}]",
                                registeredClient.getClientId());

                        TokenResponse tokenResponse = createPersonalizedTokenResponse(tokenRequest, registeredClient, authenticatedUser.get(), scopes, accessTokenLifetime, idTokenLifetime, refreshTokenLifetime, requestedTokenType);
                        LOG.debug(
                                "Token response for token exchange [{}]", tokenResponse);

                        return ResponseEntity.ok(tokenResponse);
                    } else {
                        return TokenEndpointHelper.reportInvalidRequestError();
                    }
                }
            } catch (ParseException | JOSEException e) {
                LOG.warn("Subject token is an invalid JWT");
                return TokenEndpointHelper.reportInvalidRequestError();
            }
        } else {
            LOG.warn("Subject token must be a JWT");
            return TokenEndpointHelper.reportInvalidRequestError();
        }
    }

    private ResponseEntity<TokenResponse> exchangeAccessToken(
            TokenRequest tokenRequest,
            Duration accessTokenLifetime,
            Duration idTokenLifetime,
            Duration refreshTokenLifetime,
            RegisteredClient registeredClient,
            TokenType subjectTokenType,
            TokenType requestedTokenType) {
        Set<String> scopes = new HashSet<>();
        if (StringUtils.isNotBlank(tokenRequest.getScope())) {
            scopes = new HashSet<>(Arrays.asList(tokenRequest.getScope().split(" ")));
            scopes = scopes.stream().map(String::toUpperCase).collect(Collectors.toUnmodifiableSet());
        }

        JsonWebToken jsonWebToken = tokenService.findJsonWebToken(tokenRequest.getSubject_token());
        if (jsonWebToken != null) {
            if (isNotValidForSubjectType(jsonWebToken, subjectTokenType)) {
                LOG.warn("Type of given token is not valid for given subject token type {}", tokenRequest.getSubject_token_type());
                return TokenEndpointHelper.reportInvalidRequestError();
            }

            try {
                JWTClaimsSet jwtClaimsSet =
                        jsonWebTokenService.parseAndValidateToken(jsonWebToken.getValue());
                String subject = jwtClaimsSet.getSubject();
                String ctx = jwtClaimsSet.getStringClaim("ctx");
                if (TokenService.ANONYMOUS_TOKEN.equals(ctx)) {

                    LOG.info(
                            "Creating anonymous token response for token exchange with client [{}]",
                            registeredClient.getClientId());
                    TokenResponse tokenResponse = createAnonymousTokenResponse(tokenRequest, registeredClient, scopes, accessTokenLifetime, refreshTokenLifetime, requestedTokenType);
                    LOG.debug(
                            "Token response for token exchange [{}]", tokenResponse);
                    return ResponseEntity.ok(tokenResponse);
                } else {
                    Optional<ScimUserEntity> authenticatedUser =
                            scimService.findUserByIdentifier(UUID.fromString(subject));
                    if (authenticatedUser.isPresent()) {

                        LOG.info(
                                "Creating personalized token response for token exchange with client [{}]",
                                registeredClient.getClientId());

                        TokenResponse tokenResponse = createPersonalizedTokenResponse(tokenRequest, registeredClient, authenticatedUser.get(), scopes, accessTokenLifetime, idTokenLifetime, refreshTokenLifetime, requestedTokenType);
                        LOG.debug(
                                "Token response for token exchange [{}]", tokenResponse);
                        return ResponseEntity.ok(tokenResponse);
                    } else {
                        return TokenEndpointHelper.reportInvalidRequestError();
                    }
                }
            } catch (ParseException | JOSEException e) {
                LOG.warn("Subject token is an invalid JWT");
                return TokenEndpointHelper.reportInvalidRequestError();
            }
        } else {
            LOG.debug("Subject token is opaque");
            OpaqueToken opaqueAccessToken = tokenService.findOpaqueAccessToken(tokenRequest.getSubject_token());
            if (opaqueAccessToken != null) {
                opaqueAccessToken.validate();
                String subject = opaqueAccessToken.getSubject();
                if (TokenService.ANONYMOUS_TOKEN.equals(subject)) {

                    LOG.info(
                            "Creating anonymous token response for token exchange with client [{}]",
                            registeredClient.getClientId());

                    TokenResponse tokenResponse = createAnonymousTokenResponse(tokenRequest, registeredClient, scopes, accessTokenLifetime, refreshTokenLifetime, requestedTokenType);
                    LOG.debug(
                            "Token response for token exchange [{}]", tokenResponse);

                    return ResponseEntity.ok(tokenResponse);
                } else {
                    Optional<ScimUserEntity> authenticatedUser =
                            scimService.findUserByIdentifier(UUID.fromString(opaqueAccessToken.getSubject()));
                    if (authenticatedUser.isPresent()) {

                        LOG.info(
                                "Creating personalized token response for token exchange with client [{}]",
                                registeredClient.getClientId());

                        TokenResponse tokenResponse = createPersonalizedTokenResponse(tokenRequest, registeredClient, authenticatedUser.get(), scopes, accessTokenLifetime, idTokenLifetime, refreshTokenLifetime, requestedTokenType);
                        LOG.debug(
                                "Token response for token exchange [{}]", tokenResponse);

                        return ResponseEntity.ok(tokenResponse);
                    } else {
                        return TokenEndpointHelper.reportInvalidRequestError();
                    }
                }
            } else {
                LOG.warn("Token is neither a valid JWT nor of opaque type");
                return TokenEndpointHelper.reportInvalidRequestError();
            }
        }
    }

    private ResponseEntity<TokenResponse> exchangeIDToken(
            TokenRequest tokenRequest,
            Duration accessTokenLifetime,
            Duration idTokenLifetime,
            Duration refreshTokenLifetime,
            RegisteredClient registeredClient,
            TokenType requestedTokenType) {
        Set<String> scopes = new HashSet<>();
        if (StringUtils.isNotBlank(tokenRequest.getScope())) {
            scopes = new HashSet<>(Arrays.asList(tokenRequest.getScope().split(" ")));
            scopes = scopes.stream().map(String::toUpperCase).collect(Collectors.toUnmodifiableSet());
        }

        JsonWebToken jsonWebToken = tokenService.findJsonWebIdToken(tokenRequest.getSubject_token());
        if (jsonWebToken != null) {
            try {
                JWTClaimsSet jwtClaimsSet =
                        jsonWebTokenService.parseAndValidateToken(jsonWebToken.getValue());
                String subject = jwtClaimsSet.getSubject();
                String ctx = jwtClaimsSet.getStringClaim("ctx");
                if (TokenService.ANONYMOUS_TOKEN.equals(ctx)) {

                    LOG.info(
                            "Creating anonymous token response for token exchange with client [{}]",
                            registeredClient.getClientId());
                    TokenResponse tokenResponse = createAnonymousTokenResponse(tokenRequest, registeredClient, scopes, accessTokenLifetime, refreshTokenLifetime, requestedTokenType);
                    LOG.debug(
                            "Token response for token exchange [{}]", tokenResponse);
                    return ResponseEntity.ok(tokenResponse);
                } else {
                    Optional<ScimUserEntity> authenticatedUser =
                            scimService.findUserByIdentifier(UUID.fromString(subject));
                    if (authenticatedUser.isPresent()) {

                        LOG.info(
                                "Creating personalized token response for token exchange with client [{}]",
                                registeredClient.getClientId());

                        TokenResponse tokenResponse = createPersonalizedTokenResponse(tokenRequest, registeredClient, authenticatedUser.get(), scopes, accessTokenLifetime, idTokenLifetime, refreshTokenLifetime, requestedTokenType);
                        LOG.debug(
                                "Token response for token exchange [{}]", tokenResponse);
                        return ResponseEntity.ok(tokenResponse);
                    } else {
                        return TokenEndpointHelper.reportInvalidRequestError();
                    }
                }
            } catch (ParseException | JOSEException e) {
                LOG.warn("Subject token is an invalid JWT");
                return TokenEndpointHelper.reportInvalidRequestError();
            }
        } else {
            LOG.warn("Subject token is not a valid ID Token");
            return TokenEndpointHelper.reportInvalidRequestError();
        }
    }

    private ResponseEntity<TokenResponse> exchangeRefreshToken(
            TokenRequest tokenRequest,
            Duration accessTokenLifetime,
            Duration idTokenLifetime,
            Duration refreshTokenLifetime,
            RegisteredClient registeredClient,
            TokenType requestedTokenType) {
        OpaqueToken opaqueWebToken = tokenService.findOpaqueToken(tokenRequest.getSubject_token());
        if (opaqueWebToken != null && opaqueWebToken.isRefreshToken()) {
            opaqueWebToken.validate();

            Set<String> scopes = new HashSet<>();
            if (StringUtils.isNotBlank(tokenRequest.getScope())) {
                scopes = new HashSet<>(Arrays.asList(tokenRequest.getScope().split(" ")));
                scopes = scopes.stream().map(String::toUpperCase).collect(Collectors.toUnmodifiableSet());
            }

            String subject = opaqueWebToken.getSubject();
            if (TokenService.ANONYMOUS_TOKEN.equals(subject)) {

                LOG.info(
                        "Creating anonymous token response for refresh token with client [{}]",
                        tokenRequest.getClient_id());

                TokenResponse tokenResponse = createAnonymousTokenResponse(tokenRequest, registeredClient, scopes, accessTokenLifetime, refreshTokenLifetime, requestedTokenType);
                LOG.info(
                        "Token response for refresh token [{}]", tokenResponse);
                return ResponseEntity.ok(tokenResponse);
            } else {
                Optional<ScimUserEntity> authenticatedUser =
                        scimService.findUserByIdentifier(UUID.fromString(opaqueWebToken.getSubject()));
                if (authenticatedUser.isPresent()) {

                    LOG.info(
                            "Creating personalized token response for refresh token with client [{}]",
                            tokenRequest.getClient_id());

                    TokenResponse tokenResponse = createPersonalizedTokenResponse(tokenRequest, registeredClient, authenticatedUser.get(), scopes, accessTokenLifetime, idTokenLifetime, refreshTokenLifetime, requestedTokenType);
                    LOG.info(
                            "Token response for refresh token [{}]", tokenResponse);
                    return ResponseEntity.ok(tokenResponse);
                }
            }
            tokenService.remove(opaqueWebToken);
        }
        return TokenEndpointHelper.reportInvalidClientError();
    }

    private boolean isNotValidForSubjectType(JsonWebToken jsonWebToken, TokenType subjectTokenType) {
        boolean isValid = false;
        switch(subjectTokenType) {
            case ID_TOKEN:
                isValid = jsonWebToken.isIdToken();
                break;
            case ACCESS_TOKEN:
                isValid = jsonWebToken.isAccessToken();
                break;
            case JWT_TOKEN:
                isValid = true;
                break;
            case SAML11_TOKEN:
            case SAML2_TOKEN:
            case REFRESH_TOKEN:
            default:
                break;
        }
        return !isValid;
    }

    private TokenResponse createAnonymousTokenResponse(
            TokenRequest tokenRequest,
            RegisteredClient registeredClient,
            Set<String> scopes,
            Duration accessTokenLifetime,
            Duration refreshTokenLifetime,
            TokenType requestedTokenType) {
        return new TokenResponse(
                AccessTokenFormat.JWT.equals(registeredClient.getAccessTokenFormat())
                        ? tokenService
                        .createAnonymousJwtAccessToken(
                                registeredClient.getClientId(), scopes, accessTokenLifetime)
                        .getValue()
                        : tokenService
                        .createAnonymousOpaqueAccessToken(
                                registeredClient.getClientId(), scopes, accessTokenLifetime)
                        .getValue(),
                tokenService
                        .createAnonymousRefreshToken(
                                registeredClient.getClientId(), scopes, refreshTokenLifetime)
                        .getValue(),
                accessTokenLifetime.toSeconds(),
                null,
                BEARER_TOKEN_TYPE, requestedTokenType.getIdentifier(), tokenRequest.getScope());
    }

    private TokenResponse createPersonalizedTokenResponse(
            TokenRequest tokenRequest,
            RegisteredClient registeredClient,
            ScimUserEntity authenticatedUser,
            Set<String> scopes,
            Duration accessTokenLifetime,
            Duration idTokenLifetime,
            Duration refreshTokenLifetime,
            TokenType requestedTokenType) {
        return new TokenResponse(
                AccessTokenFormat.JWT.equals(registeredClient.getAccessTokenFormat())
                        ? tokenService
                        .createPersonalizedJwtAccessToken(
                                authenticatedUser,
                                registeredClient.getClientId(),
                                null,
                                scopes,
                                accessTokenLifetime)
                        .getValue()
                        : tokenService
                        .createPersonalizedOpaqueAccessToken(
                                authenticatedUser,
                                registeredClient.getClientId(),
                                scopes,
                                accessTokenLifetime)
                        .getValue(),
                tokenService
                        .createPersonalizedRefreshToken(
                                registeredClient.getClientId(),
                                authenticatedUser,
                                scopes,
                                refreshTokenLifetime)
                        .getValue(),
                accessTokenLifetime.toSeconds(),
                scopes.contains(Scope.OPENID.name()) ?
                        tokenService.createIdToken(authenticatedUser, registeredClient.getClientId(), null, scopes, idTokenLifetime).getValue() : null,
                BEARER_TOKEN_TYPE, requestedTokenType.getIdentifier(), tokenRequest.getScope());
    }
}
