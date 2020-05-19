package com.example.authorizationserver.oauth.store;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class AuthorizationCodeService {

  private final Map<String, AuthorizationCode> codeMap = new HashMap<>();

  public AuthorizationCode getCode(String code) {
    return codeMap.get(code);
  }

  public AuthorizationCode createAndStoreAuthorizationState(
          String clientId,
          URI redirectUri,
          Set<String> scopes,
          String subject,
          String nonce,
          String code_challenge,
          String code_challenge_method) {
    String code = RandomStringUtils.random(32, true, true);
    AuthorizationCode authorizationCode =
            new AuthorizationCode(
                    clientId,
                    redirectUri,
                    scopes,
                    code,
                    subject,
                    nonce,
                    code_challenge,
                    code_challenge_method);
    codeMap.put(code, authorizationCode);
    return authorizationCode;
  }

  public void removeCode(String code) {
    codeMap.remove(code);
  }
}
