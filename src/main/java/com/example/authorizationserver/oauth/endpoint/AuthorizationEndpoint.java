package com.example.authorizationserver.oauth.endpoint;

import com.example.authorizationserver.authentication.AuthenticationService;
import com.example.authorizationserver.oauth.store.AuthorizationState;
import com.example.authorizationserver.oauth.store.AuthorizationStateStore;
import com.example.authorizationserver.user.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

@RequestMapping("/")
@Controller
public class AuthorizationEndpoint {

  private static final Logger LOG = LoggerFactory.getLogger(AuthorizationEndpoint.class);

  private final AuthorizationStateStore authorizationStateStore;
  private final AuthenticationService authenticationService;

  public AuthorizationEndpoint(AuthorizationStateStore authorizationStateStore, AuthenticationService authenticationService) {
    this.authorizationStateStore = authorizationStateStore;
    this.authenticationService = authenticationService;
  }

  /**
   * Authorization endpoint.
   *
   * @param responseType REQUIRED. OAuth 2.0 Response Type value that determines the authorization
   *     processing flow to be used, including what parameters are returned from the endpoints used.
   *     When using the Authorization Code Flow, this value is code.
   * @param scope REQUIRED. OpenID Connect requests MUST contain the openid scope value. If the
   *     openid scope value is not present, the behavior is entirely unspecified. Other scope values
   *     MAY be present. Scope values used that are not understood by an implementation SHOULD be
   *     ignored. See Sections 5.4 and 11 for additional scope values defined by this specification.
   * @param clientId REQUIRED. OAuth 2.0 Client Identifier valid at the Authorization Server.
   * @param redirectUri REQUIRED. Redirection URI to which the response will be sent. This URI MUST
   *     exactly match one of the Redirection URI values for the Client pre-registered at the OpenID
   *     Provider, with the matching performed as described in Section 6.2.1 of [RFC3986] (Simple
   *     String Comparison). When using this flow, the Redirection URI SHOULD use the https scheme;
   *     however, it MAY use the http scheme, provided that the Client Type is confidential, as
   *     defined in Section 2.1 of OAuth 2.0, and provided the OP allows the use of http Redirection
   *     URIs in this case. The Redirection URI MAY use an alternate scheme, such as one that is
   *     intended to identify a callback into a native application.
   * @param state RECOMMENDED. Opaque value used to maintain state between the request and the
   *     callback. Typically, Cross-Site Request Forgery (CSRF, XSRF) mitigation is done by
   *     cryptographically binding the value of this parameter with a browser cookie.
   * @param responseMode OPTIONAL. Informs the Authorization Server of the mechanism to be used for
   *     returning parameters from the Authorization Endpoint. This use of this parameter is NOT
   *     RECOMMENDED when the Response Mode that would be requested is the default mode specified
   *     for the Response Type.
   * @param nonce OPTIONAL. String value used to associate a Client session with an ID Token, and to
   *     mitigate replay attacks. The value is passed through unmodified from the Authentication
   *     Request to the ID Token.
   * @param prompt OPTIONAL. Space delimited, case sensitive list of ASCII string values that
   *     specifies whether the Authorization Server prompts the End-User for re-authentication and
   *     consent.
   * @return the login page
   */
  @GetMapping("/authorize")
  public String authorizationRequest(
      @RequestParam("response_type") String responseType,
      @RequestParam("scope") String scope,
      @RequestParam("client_id") String clientId,
      @RequestParam("redirect_uri") URI redirectUri,
      @RequestParam(name = "state", required = false) String state,
      @RequestParam(name = "response_mode", required = false) String responseMode,
      @RequestParam(name = "nonce", required = false) String nonce,
      @RequestParam(name = "prompt", required = false) String prompt,
      Model model) {

    model.addAttribute("user", new User());
    model.addAttribute("state", state);
    model.addAttribute("scope", scope);
    model.addAttribute("nonce", nonce);
    model.addAttribute("client_id", clientId);
    model.addAttribute("redirect_uri", redirectUri);
    model.addAttribute("response_mode", responseMode);

    return "loginform";
  }

  @PostMapping("/authenticate")
  public String authenticate(
      @RequestParam("scope") String scope,
      @RequestParam("client_id") String clientId,
      @RequestParam("redirect_uri") URI redirectUri,
      @RequestParam("state") String state,
      @RequestParam(name = "nonce", required = false) String nonce,
      User user,
      Model model) {

    LOG.info("Authenticating user {} for client id {} and scopes {}", user.getUsername(), clientId, scope);

    User authenticatedUser;
    try {
      authenticatedUser = authenticationService.authenticate(user.getUsername(), user.getPassword());
    } catch (BadCredentialsException ex) {
      model.addAttribute("error", ex.getMessage());
      return "loginform";
    }

    List<String> scopes = Arrays.asList(scope.split(" "));

    model.addAttribute("scopes", scopes);
    model.addAttribute("client_id", clientId);

    LOG.info("Authenticated user {} for client id {} and scopes {}", authenticatedUser.getIdentifier(), clientId, scopes);

    AuthorizationState authorizationState =
        authorizationStateStore.createAndStoreAuthorizationState(
            clientId, redirectUri, scopes, authenticatedUser.getIdentifier().toString(), nonce);
    return "redirect:" + redirectUri.toString() + "?code=" + authorizationState.getCode() + "&state=" + state;
/*    return new RedirectView(
        redirectUri.toString() + "?code=" + authorizationState.getCode() + "&state=" + state);*/
  }

  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<String> handle(MissingServletRequestParameterException ex) {
    return ResponseEntity.badRequest().body(ex.getMessage());
  }

  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<String> handle(BadCredentialsException ex) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
  }
}
