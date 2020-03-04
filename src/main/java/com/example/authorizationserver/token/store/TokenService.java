package com.example.authorizationserver.token.store;

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

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class TokenService {

  private final JsonWebTokenRepository jsonWebTokenRepository;
  private final OpaqueTokenRepository opaqueTokenRepository;
  private final JsonWebTokenService jsonWebTokenService;
  private final OpaqueTokenService opaqueTokenService;

  public TokenService(JsonWebTokenRepository jsonWebTokenRepository, OpaqueTokenRepository opaqueTokenRepository, JsonWebTokenService jsonWebTokenService, OpaqueTokenService opaqueTokenService) {
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
  public JsonWebToken createIdToken(User user, String clientId, String nonce, List<String> scopes) throws JOSEException {
    String token = jsonWebTokenService.createToken(Collections.singletonList(clientId), "test", scopes, user, nonce);
    JsonWebToken jsonWebToken = new JsonWebToken();
    jsonWebToken.setExpiry(LocalDateTime.now().plusMinutes(60));
    jsonWebToken.setValue(token);
    return jsonWebTokenRepository.save(jsonWebToken);
  }

  @Transactional
  public JsonWebToken createJwtAccessToken(User user, String clientId, String nonce) throws JOSEException {
    String token = jsonWebTokenService.createToken(Collections.singletonList(clientId), "test", Collections.emptyList(), user, nonce);
    JsonWebToken jsonWebToken = new JsonWebToken();
    jsonWebToken.setExpiry(LocalDateTime.now().plusMinutes(60));
    jsonWebToken.setValue(token);
    return jsonWebTokenRepository.save(jsonWebToken);
  }

  @Transactional
  public OpaqueToken createOpaqueAccessToken(User user, String clientId) throws JOSEException {
    String token = opaqueTokenService.createToken();
    OpaqueToken opaqueToken = new OpaqueToken();
    opaqueToken.setExpiry(LocalDateTime.now().plusMinutes(60));
    opaqueToken.setValue(token);
    opaqueToken.setClientId(clientId);
    opaqueToken.setSubject(user.getIdentifier().toString());
    return opaqueTokenRepository.save(opaqueToken);
  }

  @Transactional
  public OpaqueToken createRefreshToken(User user, String clientId) {
    String token = opaqueTokenService.createToken();
    OpaqueToken opaqueToken = new OpaqueToken();
    opaqueToken.setExpiry(LocalDateTime.now().plusMinutes(60));
    opaqueToken.setValue(token);
    opaqueToken.setRefreshToken(true);
    opaqueToken.setClientId(clientId);
    opaqueToken.setSubject(user.getIdentifier().toString());
    return opaqueTokenRepository.save(opaqueToken);
  }

}
