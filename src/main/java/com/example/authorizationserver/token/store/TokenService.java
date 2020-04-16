package com.example.authorizationserver.token.store;

import com.example.authorizationserver.config.AuthorizationServerConfigurationProperties;
import com.example.authorizationserver.token.jwt.JsonWebTokenService;
import com.example.authorizationserver.token.opaque.OpaqueTokenService;
import com.example.authorizationserver.token.store.dao.JsonWebTokenRepository;
import com.example.authorizationserver.token.store.dao.OpaqueTokenRepository;
import com.example.authorizationserver.token.store.model.JsonWebToken;
import com.example.authorizationserver.token.store.model.OpaqueToken;
import com.example.authorizationserver.user.model.User;
import com.nimbusds.jose.JOSEException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class TokenService {

  public static final String ANONYMOUS_TOKEN = "anonymous";

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

  public OpaqueToken findOpaqueWebToken(String value) {
    return opaqueTokenRepository.findOneByValue(value);
  }

  @Transactional
  public JsonWebToken createIdToken(
      User user, String clientId, String nonce, List<String> scopes, Duration idTokenLifetime)
      throws JOSEException {
    LocalDateTime expiryDateTime = LocalDateTime.now().plusMinutes(idTokenLifetime.toMinutes());
    String token =
        jsonWebTokenService.createPersonalizedToken(
            clientId,
            Collections.singletonList(clientId),
            "test",
            scopes,
            user,
            nonce,
            expiryDateTime);
    JsonWebToken jsonWebToken = new JsonWebToken();
    jsonWebToken.setExpiry(expiryDateTime);
    jsonWebToken.setValue(token);
    return jsonWebTokenRepository.save(jsonWebToken);
  }

  @Transactional
  public JsonWebToken createPersonalizedJwtAccessToken(
      User user, String clientId, String nonce, Duration accessTokenLifetime) throws JOSEException {
    LocalDateTime expiryDateTime = LocalDateTime.now().plusMinutes(accessTokenLifetime.toMinutes());
    String token =
        jsonWebTokenService.createPersonalizedToken(
            clientId,
            Collections.singletonList(clientId),
            "test",
            Collections.emptyList(),
            user,
            nonce,
            expiryDateTime);
    JsonWebToken jsonWebToken = new JsonWebToken();
    jsonWebToken.setExpiry(expiryDateTime);
    jsonWebToken.setValue(token);
    return jsonWebTokenRepository.save(jsonWebToken);
  }

  @Transactional
  public JsonWebToken createAnonymousJwtAccessToken(String clientId, Duration accessTokenLifetime)
      throws JOSEException {
    LocalDateTime expiryDateTime = LocalDateTime.now().plusMinutes(accessTokenLifetime.toMinutes());
    String token =
        jsonWebTokenService.createAnonymousToken(
            clientId,
            Collections.singletonList(clientId),
            "test",
            Collections.emptyList(),
            expiryDateTime);
    JsonWebToken jsonWebToken = new JsonWebToken();
    jsonWebToken.setExpiry(expiryDateTime);
    jsonWebToken.setValue(token);
    return jsonWebTokenRepository.save(jsonWebToken);
  }

  @Transactional
  public OpaqueToken createPersonalizedOpaqueAccessToken(
      User user, String clientId, Duration accessTokenLifetime) {
    LocalDateTime issueTime = LocalDateTime.now();
    LocalDateTime expiryDateTime = issueTime.plusMinutes(accessTokenLifetime.toMinutes());
    String token = opaqueTokenService.createToken();
    OpaqueToken opaqueToken = new OpaqueToken();
    opaqueToken.setExpiry(expiryDateTime);
    opaqueToken.setValue(token);
    opaqueToken.setClientId(clientId);
    opaqueToken.setIssuedAt(issueTime);
    opaqueToken.setNotBefore(issueTime);
    opaqueToken.setIssuer(authorizationServerProperties.getIssuer().toString());
    opaqueToken.setSubject(user.getIdentifier().toString());
    return opaqueTokenRepository.save(opaqueToken);
  }

  @Transactional
  public OpaqueToken createAnonymousOpaqueAccessToken(
      String clientId, Duration accessTokenLifetime) {
    LocalDateTime issueTime = LocalDateTime.now();
    LocalDateTime expiryDateTime = issueTime.plusMinutes(accessTokenLifetime.toMinutes());
    String token = opaqueTokenService.createToken();
    OpaqueToken opaqueToken = new OpaqueToken();
    opaqueToken.setExpiry(expiryDateTime);
    opaqueToken.setIssuedAt(issueTime);
    opaqueToken.setNotBefore(issueTime);
    opaqueToken.setIssuer(authorizationServerProperties.getIssuer().toString());
    opaqueToken.setValue(token);
    opaqueToken.setClientId(clientId);
    opaqueToken.setSubject(ANONYMOUS_TOKEN);
    return opaqueTokenRepository.save(opaqueToken);
  }

  @Transactional
  public OpaqueToken createPersonalizedRefreshToken(String clientId, User user, Duration refreshTokenLifetime) {
    LocalDateTime issueTime = LocalDateTime.now();
    LocalDateTime expiryDateTime = issueTime.plusMinutes(refreshTokenLifetime.toMinutes());
    String token = opaqueTokenService.createToken();
    OpaqueToken opaqueToken = new OpaqueToken();
    opaqueToken.setExpiry(expiryDateTime);
    opaqueToken.setIssuedAt(issueTime);
    opaqueToken.setNotBefore(issueTime);
    opaqueToken.setIssuer(authorizationServerProperties.getIssuer().toString());
    opaqueToken.setValue(token);
    opaqueToken.setRefreshToken(true);
    opaqueToken.setClientId(clientId);
    opaqueToken.setSubject(user.getIdentifier().toString());
    return opaqueTokenRepository.save(opaqueToken);
  }

  @Transactional
  public OpaqueToken createAnonymousRefreshToken(String clientId, Duration refreshTokenLifetime) {
    LocalDateTime issueTime = LocalDateTime.now();
    LocalDateTime expiryDateTime = issueTime.plusMinutes(refreshTokenLifetime.toMinutes());
    String token = opaqueTokenService.createToken();
    OpaqueToken opaqueToken = new OpaqueToken();
    opaqueToken.setExpiry(expiryDateTime);
    opaqueToken.setIssuedAt(issueTime);
    opaqueToken.setNotBefore(issueTime);
    opaqueToken.setIssuer(authorizationServerProperties.getIssuer().toString());
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
