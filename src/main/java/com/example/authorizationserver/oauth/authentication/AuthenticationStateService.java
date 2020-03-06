package com.example.authorizationserver.oauth.authentication;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthenticationStateService {

  private Map<String, AuthenticationState> authenticationStateMap = new HashMap<>();

  public AuthenticationState getState(String key) {
    return authenticationStateMap.get(key);
  }

  public String createState(
      String responseType,
      String scope,
      String clientId,
      URI redirectUri,
      String state,
      String responseMode,
      String nonce,
      String prompt,
      String display,
      Long max_age,
      String ui_locales,
      String id_token_hint,
      String login_hint,
      String acr_values,
      String code_challenge,
      String code_challenge_method,
      URI resource) {
    String key = RandomStringUtils.random(32, true, true);
    AuthenticationState authenticationState =
        new AuthenticationState(
            responseType,
            scope,
            clientId,
            redirectUri,
            state,
            responseMode,
            nonce,
            prompt,
            display,
            max_age,
            ui_locales,
            id_token_hint,
            login_hint,
            acr_values,
            code_challenge,
            code_challenge_method,
            resource);
    authenticationStateMap.put(key, authenticationState);
    return key;
  }

  public void removeAuthenticationState(String key) {
    authenticationStateMap.remove(key);
  }
}
