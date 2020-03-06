package com.example.authorizationserver.oauth.authentication;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class AuthenticationState {

  private final String responseType;
  private final String scope;
  private final String clientId;
  private final URI redirectUri;
  private final String state;
  private final String responseMode;
  private final String nonce;
  private final String prompt;
  private final String display;
  private final Long max_age;
  private final String ui_locales;
  private final String id_token_hint;
  private final String login_hint;
  private final String acr_values;
  private final String code_challenge;
  private final String code_challenge_method;
  private final URI resource;
  private UUID userIdentifier;
  private final LocalDateTime expiry;

  public AuthenticationState(
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
    this.responseType = responseType;
    this.scope = scope;
    this.clientId = clientId;
    this.redirectUri = redirectUri;
    this.state = state;
    this.responseMode = responseMode;
    this.nonce = nonce;
    this.prompt = prompt;
    this.display = display;
    this.max_age = max_age;
    this.ui_locales = ui_locales;
    this.id_token_hint = id_token_hint;
    this.login_hint = login_hint;
    this.acr_values = acr_values;
    this.code_challenge = code_challenge;
    this.code_challenge_method = code_challenge_method;
    this.resource = resource;
    this.expiry = LocalDateTime.now().plusHours(8);
  }

  public String getResponseType() {
    return responseType;
  }

  public String getScope() {
    return scope;
  }

  public String getClientId() {
    return clientId;
  }

  public URI getRedirectUri() {
    return redirectUri;
  }

  public String getState() {
    return state;
  }

  public String getResponseMode() {
    return responseMode;
  }

  public String getNonce() {
    return nonce;
  }

  public String getPrompt() {
    return prompt;
  }

  public String getDisplay() {
    return display;
  }

  public Long getMax_age() {
    return max_age;
  }

  public String getUi_locales() {
    return ui_locales;
  }

  public String getId_token_hint() {
    return id_token_hint;
  }

  public String getLogin_hint() {
    return login_hint;
  }

  public String getAcr_values() {
    return acr_values;
  }

  public String getCode_challenge() {
    return code_challenge;
  }

  public String getCode_challenge_method() {
    return code_challenge_method;
  }

  public URI getResource() {
    return resource;
  }

  public UUID getUserIdentifier() {
    return userIdentifier;
  }

  public void setUserIdentifier(UUID userIdentifier) {
    this.userIdentifier = userIdentifier;
  }

  public boolean isExpired() {
    return LocalDateTime.now().isAfter(expiry);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AuthenticationState that = (AuthenticationState) o;
    return Objects.equals(responseType, that.responseType)
        && Objects.equals(scope, that.scope)
        && Objects.equals(clientId, that.clientId)
        && Objects.equals(redirectUri, that.redirectUri)
        && Objects.equals(state, that.state)
        && Objects.equals(responseMode, that.responseMode)
        && Objects.equals(nonce, that.nonce)
        && Objects.equals(prompt, that.prompt)
        && Objects.equals(display, that.display)
        && Objects.equals(max_age, that.max_age)
        && Objects.equals(ui_locales, that.ui_locales)
        && Objects.equals(id_token_hint, that.id_token_hint)
        && Objects.equals(login_hint, that.login_hint)
        && Objects.equals(acr_values, that.acr_values)
        && Objects.equals(code_challenge, that.code_challenge)
        && Objects.equals(code_challenge_method, that.code_challenge_method)
        && Objects.equals(resource, that.resource);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
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
  }

  @Override
  public String toString() {
    return "AuthenticationState{"
        + "responseType='"
        + responseType
        + '\''
        + ", scope='"
        + scope
        + '\''
        + ", clientId='"
        + clientId
        + '\''
        + ", redirectUri="
        + redirectUri
        + ", state='"
        + state
        + '\''
        + ", responseMode='"
        + responseMode
        + '\''
        + ", nonce='"
        + nonce
        + '\''
        + ", prompt='"
        + prompt
        + '\''
        + ", display='"
        + display
        + '\''
        + ", max_age="
        + max_age
        + ", ui_locales='"
        + ui_locales
        + '\''
        + ", id_token_hint='"
        + id_token_hint
        + '\''
        + ", login_hint='"
        + login_hint
        + '\''
        + ", acr_values='"
        + acr_values
        + '\''
        + ", code_challenge='"
        + code_challenge
        + '\''
        + ", code_challenge_method='"
        + code_challenge_method
        + '\''
        + ", resource="
        + resource
        + '}';
  }
}
