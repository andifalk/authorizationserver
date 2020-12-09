package com.example.authorizationserver.token.store;

import com.example.authorizationserver.config.AuthorizationServerConfigurationProperties;
import com.example.authorizationserver.scim.model.ScimUserEntity;
import com.example.authorizationserver.token.jwt.JsonWebTokenService;
import com.example.authorizationserver.token.opaque.OpaqueTokenService;
import com.example.authorizationserver.token.store.dao.JsonWebTokenRepository;
import com.example.authorizationserver.token.store.dao.OpaqueTokenRepository;
import com.example.authorizationserver.token.store.model.JsonWebToken;
import com.example.authorizationserver.token.store.model.OpaqueToken;
import com.nimbusds.jose.JOSEException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class TokenService {

    public static final String ANONYMOUS_TOKEN = "anonymous";
    public static final String PERSONAL_TOKEN = "personal";

    private static final Logger LOG = LoggerFactory.getLogger(TokenService.class);

    private final AuthorizationServerConfigurationProperties authorizationServerProperties;
    private final JsonWebTokenRepository jsonWebTokenRepository;
    private final OpaqueTokenRepository opaqueTokenRepository;
    private final JsonWebTokenService jsonWebTokenService;
    private final OpaqueTokenService opaqueTokenService;

    public TokenService(
            AuthorizationServerConfigurationProperties authorizationServerProperties,
            JsonWebTokenRepository jsonWebTokenRepository,
            OpaqueTokenRepository opaqueTokenRepository,
            JsonWebTokenService jsonWebTokenService,
            OpaqueTokenService opaqueTokenService) {
        this.authorizationServerProperties = authorizationServerProperties;
        this.jsonWebTokenRepository = jsonWebTokenRepository;
        this.opaqueTokenRepository = opaqueTokenRepository;
        this.jsonWebTokenService = jsonWebTokenService;
        this.opaqueTokenService = opaqueTokenService;
    }

    public JsonWebToken findJsonWebToken(String value) {
        return jsonWebTokenRepository.findOneByValue(value);
    }

    public JsonWebToken findJsonWebAccessToken(String value) {
        return jsonWebTokenRepository.findOneByValueAndAccessToken(value, true);
    }

    public JsonWebToken findJsonWebIdToken(String value) {
        return jsonWebTokenRepository.findOneByValueAndAccessToken(value, false);
    }

    public OpaqueToken findOpaqueToken(String value) {
        return opaqueTokenRepository.findOneByValue(value);
    }

    public OpaqueToken findOpaqueAccessToken(String value) {
        return opaqueTokenRepository.findOneByValueAndRefreshToken(value, false);
    }

    public OpaqueToken findOpaqueRefreshToken(String value) {
        return opaqueTokenRepository.findOneByValueAndRefreshToken(value, true);
    }

    @Transactional
    public JsonWebToken createIdToken(
            ScimUserEntity user, String clientId, String nonce, Set<String> scopes, Duration idTokenLifetime) {
        LocalDateTime expiryDateTime = LocalDateTime.now().plusMinutes(idTokenLifetime.toMinutes());
        String token;
        try {
            token =
                    jsonWebTokenService.createPersonalizedToken(
                            false,
                            clientId,
                            Collections.singletonList(clientId),
                            scopes,
                            user,
                            nonce,
                            expiryDateTime);
            JsonWebToken jsonWebToken = new JsonWebToken();
            jsonWebToken.setExpiry(expiryDateTime);
            jsonWebToken.setValue(token);
            jsonWebToken.setAccessToken(false);
            return jsonWebTokenRepository.save(jsonWebToken);
        } catch (JOSEException e) {
            LOG.error("Error creating Id token", e);
            throw new TokenServiceException("Error creating Id token", e);
        }
    }

    @Transactional
    public JsonWebToken createPersonalizedJwtAccessToken(
            ScimUserEntity user, String clientId, String nonce, Set<String> scopes, Duration accessTokenLifetime) {
        LocalDateTime expiryDateTime = LocalDateTime.now().plusMinutes(accessTokenLifetime.toMinutes());
        String token;
        try {
            token =
                    jsonWebTokenService.createPersonalizedToken(
                            true,
                            clientId,
                            Collections.singletonList(clientId),
                            scopes,
                            user,
                            nonce,
                            expiryDateTime);
            JsonWebToken jsonWebToken = new JsonWebToken();
            jsonWebToken.setExpiry(expiryDateTime);
            jsonWebToken.setValue(token);
            jsonWebToken.setAccessToken(true);
            return jsonWebTokenRepository.save(jsonWebToken);
        } catch (JOSEException e) {
            LOG.error("Error creating a personalized JWT", e);
            throw new TokenServiceException("Error creating a personalized JWT", e);
        }
    }

    @Transactional
    public JsonWebToken createAnonymousJwtAccessToken(String clientId, Set<String> scopes, Duration accessTokenLifetime) {
        LocalDateTime expiryDateTime = LocalDateTime.now().plusMinutes(accessTokenLifetime.toMinutes());
        String token;
        try {
            token =
                    jsonWebTokenService.createAnonymousToken(
                            true,
                            clientId,
                            Collections.singletonList(clientId),
                            scopes,
                            expiryDateTime);
            JsonWebToken jsonWebToken = new JsonWebToken();
            jsonWebToken.setExpiry(expiryDateTime);
            jsonWebToken.setValue(token);
            jsonWebToken.setAccessToken(true);
            return jsonWebTokenRepository.save(jsonWebToken);
        } catch (JOSEException e) {
            LOG.error("Error creating a anonymous JWT", e);
            throw new TokenServiceException("Error creating a anonymous JWT", e);
        }
    }

    @Transactional
    public OpaqueToken createPersonalizedOpaqueAccessToken(
            ScimUserEntity user, String clientId, Set<String> scopes, Duration accessTokenLifetime) {
        LocalDateTime issueTime = LocalDateTime.now();
        LocalDateTime expiryDateTime = issueTime.plusMinutes(accessTokenLifetime.toMinutes());
        String token = opaqueTokenService.createToken();
        OpaqueToken opaqueToken = new OpaqueToken();
        opaqueToken.setRefreshToken(false);
        opaqueToken.setExpiry(expiryDateTime);
        opaqueToken.setValue(token);
        opaqueToken.setClientId(clientId);
        opaqueToken.setIssuedAt(issueTime);
        opaqueToken.setNotBefore(issueTime);
        opaqueToken.setIssuer(authorizationServerProperties.getIssuer().toString());
        opaqueToken.setScope(scopes);
        opaqueToken.setSubject(user.getIdentifier().toString());
        return opaqueTokenRepository.save(opaqueToken);
    }

    @Transactional
    public OpaqueToken createAnonymousOpaqueAccessToken(
            String clientId, Set<String> scopes, Duration accessTokenLifetime) {
        LocalDateTime issueTime = LocalDateTime.now();
        LocalDateTime expiryDateTime = issueTime.plusMinutes(accessTokenLifetime.toMinutes());
        String token = opaqueTokenService.createToken();
        OpaqueToken opaqueToken = new OpaqueToken();
        opaqueToken.setRefreshToken(false);
        opaqueToken.setExpiry(expiryDateTime);
        opaqueToken.setIssuedAt(issueTime);
        opaqueToken.setNotBefore(issueTime);
        opaqueToken.setIssuer(authorizationServerProperties.getIssuer().toString());
        opaqueToken.setScope(scopes);
        opaqueToken.setValue(token);
        opaqueToken.setClientId(clientId);
        opaqueToken.setSubject(ANONYMOUS_TOKEN);
        return opaqueTokenRepository.save(opaqueToken);
    }

    @Transactional
    public OpaqueToken createPersonalizedRefreshToken(
            String clientId, ScimUserEntity user, Set<String> scopes, Duration refreshTokenLifetime) {
        LocalDateTime issueTime = LocalDateTime.now();
        LocalDateTime expiryDateTime = issueTime.plusMinutes(refreshTokenLifetime.toMinutes());
        String token = opaqueTokenService.createToken();
        OpaqueToken opaqueToken = new OpaqueToken();
        opaqueToken.setRefreshToken(true);
        opaqueToken.setExpiry(expiryDateTime);
        opaqueToken.setIssuedAt(issueTime);
        opaqueToken.setNotBefore(issueTime);
        opaqueToken.setIssuer(authorizationServerProperties.getIssuer().toString());
        opaqueToken.setScope(scopes);
        opaqueToken.setValue(token);
        opaqueToken.setRefreshToken(true);
        opaqueToken.setClientId(clientId);
        opaqueToken.setSubject(user.getIdentifier().toString());
        return opaqueTokenRepository.save(opaqueToken);
    }

    @Transactional
    public OpaqueToken createAnonymousRefreshToken(String clientId, Set<String> scopes, Duration refreshTokenLifetime) {
        LocalDateTime issueTime = LocalDateTime.now();
        LocalDateTime expiryDateTime = issueTime.plusMinutes(refreshTokenLifetime.toMinutes());
        String token = opaqueTokenService.createToken();
        OpaqueToken opaqueToken = new OpaqueToken();
        opaqueToken.setRefreshToken(true);
        opaqueToken.setExpiry(expiryDateTime);
        opaqueToken.setIssuedAt(issueTime);
        opaqueToken.setNotBefore(issueTime);
        opaqueToken.setIssuer(authorizationServerProperties.getIssuer().toString());
        opaqueToken.setScope(scopes);
        opaqueToken.setValue(token);
        opaqueToken.setRefreshToken(true);
        opaqueToken.setClientId(clientId);
        opaqueToken.setSubject(ANONYMOUS_TOKEN);
        return opaqueTokenRepository.save(opaqueToken);
    }

    @Transactional
    public void remove(OpaqueToken entity) {
        opaqueTokenRepository.delete(entity);
    }

    @Transactional
    public void remove(JsonWebToken entity) {
        jsonWebTokenRepository.delete(entity);
    }
}
