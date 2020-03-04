package com.example.authorizationserver.oauth.store;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class AuthorizationStateStore {

  private Map<String, AuthorizationState> codeMap = new HashMap<>();

  public AuthorizationState getState(String code) {
    return codeMap.get(code);
  }

  public AuthorizationState createAndStoreAuthorizationState(String clientId, URI redirectUri, List<String> scopes, String subject, String nonce) {
    String code = RandomStringUtils.random(32, true, true);
    AuthorizationState authorizationState = new AuthorizationState(clientId, redirectUri, scopes, code, subject, nonce);
    codeMap.put(code, authorizationState);
    return authorizationState;
  }

  public void removeState(String code) {
    codeMap.remove(code);
  }

}
