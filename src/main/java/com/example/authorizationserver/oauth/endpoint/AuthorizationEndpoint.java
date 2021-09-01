package com.example.authorizationserver.oauth.endpoint;

import com.example.authorizationserver.oauth.client.RegisteredClientService;
import com.example.authorizationserver.oauth.client.model.RegisteredClient;
import com.example.authorizationserver.oauth.common.GrantType;
import com.example.authorizationserver.oauth.store.AuthorizationCode;
import com.example.authorizationserver.oauth.store.AuthorizationCodeService;
import com.example.authorizationserver.security.user.EndUserDetails;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.constraints.Pattern;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Authorization endpoint as specified in RFC 6749: The OAuth 2.0 Authorization Framework
 *
 * @link https://www.rfc-editor.org/rfc/rfc6749.html#section-3.1
 */
@Validated
@CrossOrigin(originPatterns = "*", allowCredentials = "true", allowedHeaders = "*")
@RequestMapping(AuthorizationEndpoint.ENDPOINT)
@Controller
public class AuthorizationEndpoint {

  public static final String ENDPOINT = "/authorize";

  private static final Logger LOG = LoggerFactory.getLogger(AuthorizationEndpoint.class);
  private final AuthorizationCodeService authorizationCodeService;
  private final RegisteredClientService registeredClientService;

  public AuthorizationEndpoint(
      AuthorizationCodeService authorizationCodeService,
      RegisteredClientService registeredClientService) {
    this.authorizationCodeService = authorizationCodeService;
    this.registeredClientService = registeredClientService;
  }

  /**
   * Authorization endpoint.
   *
   * <p>Implements:
   *
   * <ul>
   *   <li>RFC 6749: OAuth 2.0 Authorization Request, <a
   *       href="https://www.rfc-editor.org/rfc/rfc6749.html#section-4.1.1">RFC 6749</a>
   *   <li>OpenID Connect 1.0 Authentication Request, <a
   *       href="https://openid.net/specs/openid-connect-core-1_0.html#AuthRequest">OIDC 1.0</a>
   *   <li>RFC 7636: OAuth 2.0 Proof Key for Code Exchange (PKCE), <a
   *       href="https://tools.ietf.org/html/rfc7636#section-4">RFC 7636</a>
   *   <li>RFC 8707: Resource Indicators for OAuth 2.0, <a
   *       href="https://www.rfc-editor.org/rfc/rfc8707.html#name-authorization-request">RFC
   *       8707</a>
   * </ul>
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
   * @param display OPTIONAL. ASCII string value that specifies how the Authorization Server
   *     displays the authentication and consent user interface pages to the End-User. The defined
   *     values are: page The Authorization Server SHOULD display the authentication and consent UI
   *     consistent with a full User Agent page view. If the display parameter is not specified,
   *     this is the default display mode. popup The Authorization Server SHOULD display the
   *     authentication and consent UI consistent with a popup User Agent window. The popup User
   *     Agent window should be of an appropriate size for a login-focused dialog and should not
   *     obscure the entire window that it is popping up over. touch The Authorization Server SHOULD
   *     display the authentication and consent UI consistent with a device that leverages a touch
   *     interface. wap The Authorization Server SHOULD display the authentication and consent UI
   *     consistent with a "feature phone" type display. The Authorization Server MAY also attempt
   *     to detect the capabilities of the User Agent and present an appropriate display.
   * @param prompt OPTIONAL. Space delimited, case sensitive list of ASCII string values that
   *     specifies whether the Authorization Server prompts the End-User for re-authentication and
   *     consent.
   * @param max_age OPTIONAL. Maximum Authentication Age. Specifies the allowable elapsed time in
   *     seconds since the last time the End-User was actively authenticated by the OP. If the
   *     elapsed time is greater than this value, the OP MUST attempt to actively re-authenticate
   *     the End-User. (The max_age request parameter corresponds to the OpenID 2.0 PAPE
   *     [OpenID.PAPE] max_auth_age request parameter.) When max_age is used, the ID Token returned
   *     MUST include an auth_time Claim Value.
   * @param ui_locales OPTIONAL. End-User's preferred languages and scripts for the user interface,
   *     represented as a space-separated list of BCP47 [RFC5646] language tag values, ordered by
   *     preference. For instance, the value "fr-CA fr en" represents a preference for French as
   *     spoken in Canada, then French (without a region designation), followed by English (without
   *     a region designation). An error SHOULD NOT result if some or all of the requested locales
   *     are not supported by the OpenID Provider.
   * @param id_token_hint OPTIONAL. ID Token previously issued by the Authorization Server being
   *     passed as a hint about the End-User's current or past authenticated session with the
   *     Client. If the End-User identified by the ID Token is logged in or is logged in by the
   *     request, then the Authorization Server returns a positive response; otherwise, it SHOULD
   *     return an error, such as login_required. When possible, an id_token_hint SHOULD be present
   *     when prompt=none is used and an invalid_request error MAY be returned if it is not;
   *     however, the server SHOULD respond successfully when possible, even if it is not present.
   *     The Authorization Server need not be listed as an audience of the ID Token when it is used
   *     as an id_token_hint value. If the ID Token received by the RP from the OP is encrypted, to
   *     use it as an id_token_hint, the Client MUST decrypt the signed ID Token contained within
   *     the encrypted ID Token. The Client MAY re-encrypt the signed ID token to the Authentication
   *     Server using a key that enables the server to decrypt the ID Token, and use the
   *     re-encrypted ID token as the id_token_hint value.
   * @param login_hint OPTIONAL. Hint to the Authorization Server about the login identifier the
   *     End-User might use to log in (if necessary). This hint can be used by an RP if it first
   *     asks the End-User for their e-mail address (or other identifier) and then wants to pass
   *     that value as a hint to the discovered authorization service. It is RECOMMENDED that the
   *     hint value match the value used for discovery. This value MAY also be a phone number in the
   *     format specified for the phone_number Claim. The use of this parameter is left to the OP's
   *     discretion.
   * @param acr_values OPTIONAL. Requested Authentication Context Class Reference values.
   *     Space-separated string that specifies the acr values that the Authorization Server is being
   *     requested to use for processing this Authentication Request, with the values appearing in
   *     order of preference. The Authentication Context Class satisfied by the authentication
   *     performed is returned as the acr Claim Value, as specified in Section 2. The acr Claim is
   *     requested as a Voluntary Claim by this parameter.
   * @param code_challenge REQUIRED for public clients (PKCE). Code challenge. Specified as part of
   *     RFC 7636: Proof Key for Code Exchange by OAuth Public Clients
   *     (https://tools.ietf.org/html/rfc7636).
   * @param code_challenge_method OPTIONAL for public clients (PKCE), defaults to "plain" if not
   *     present in the request. Code verifier transformation method is "S256" or "plain". Specified
   *     as part of RFC 7636: Proof Key for Code Exchange by OAuth Public Clients
   *     (https://tools.ietf.org/html/rfc7636).
   * @param resource OPTIONAL Indicates the target service or resource to which access is being
   *     requested. RFC 8707: Resource Indicators for OAuth 2.0
   *     (https://www.rfc-editor.org/rfc/rfc8707.html) Its value MUST be an absolute URI, as
   *     specified by Section 4.3 of [RFC3986]. The URI MUST NOT include a fragment component. It
   *     SHOULD NOT include a query component, but it is recognized that there are cases that make a
   *     query component a useful and necessary part of the resource parameter, such as when one or
   *     more query parameters are used to scope requests to an application. The resource parameter
   *     URI value is an identifier representing the identity of the resource, which MAY be a
   *     locator that corresponds to a network-addressable location where the target resource is
   *     hosted. Multiple resource parameters MAY be used to indicate that the requested token is
   *     intended to be used at multiple resources.
   * @return redirection to client with authorization code
   */
  @SuppressWarnings({"unused", "SpringMVCViewInspection"})
  @PreAuthorize("isAuthenticated()")
  @GetMapping
  public String authorizationRequest(
      @RequestParam("response_type") @Pattern(regexp = "code|token") String responseType,
      @RequestParam("scope") String scope,
      @RequestParam("client_id") String clientId,
      @RequestParam("redirect_uri") URI redirectUri,
      @RequestParam(name = "state", required = false) String state,
      @RequestParam(name = "response_mode", required = false) @Pattern(regexp = "query|form_post")
          String responseMode,
      @RequestParam(name = "nonce", required = false) String nonce,
      @RequestParam(name = "prompt", required = false)
          @Pattern(regexp = "none|login|consent|select_account")
          String prompt,
      @RequestParam(name = "display", required = false) @Pattern(regexp = "page|popup|touch|wap")
          String display,
      @RequestParam(name = "max_age", required = false) Long max_age,
      @RequestParam(name = "ui_locales", required = false) String ui_locales,
      @RequestParam(name = "id_token_hint", required = false) String id_token_hint,
      @RequestParam(name = "login_hint", required = false) String login_hint,
      @RequestParam(name = "acr_values", required = false) String acr_values,
      @RequestParam(name = "code_challenge", required = false) String code_challenge,
      @RequestParam(name = "code_challenge_method", required = false)
          @Pattern(regexp = "plain|S256")
          String code_challenge_method,
      @RequestParam(name = "resource", required = false) URI resource,
      @AuthenticationPrincipal EndUserDetails endUserDetails) {

    LOG.debug(
        "Authorization Request: client_id={}, response_type = {}, scope={}, redirectUri={}, endUser={}",
        clientId,
        responseType,
        scope,
        redirectUri,
        endUserDetails != null ? endUserDetails.getUsername() : "n/a");

    if (endUserDetails == null || endUserDetails.getIdentifier() == null) {
      throw new BadCredentialsException("No user");
    }

    if (StringUtils.isBlank(clientId)) {
      throw new InvalidClientIdError("Invalid client");
    }

    Optional<RegisteredClient> registeredClient =
        registeredClientService.findOneByClientId(clientId);

    if (registeredClient.isEmpty()) {
      throw new InvalidClientIdError(clientId);
    } else {
      RegisteredClient client = registeredClient.get();
      if (!client.getRedirectUris().contains(redirectUri.toString())) {
        throw new InvalidRedirectUriError(redirectUri.toString());
      }
      if (!client.isConfidential() && StringUtils.isBlank(code_challenge)) {
        return redirectError(
            redirectUri, "invalid_request", "code_challenge is required for public client", state);
      }
      if (!client.getGrantTypes().contains(GrantType.AUTHORIZATION_CODE)) {
        return redirectError(
            redirectUri,
            "unauthorized_client",
            "The client is not authorized to request an authorization code using this method",
            state);
      }
    }

    if (StringUtils.isBlank(scope)) {
      return redirectError(redirectUri, "invalid_scope", "scope must not be empty", state);
    }

    if ("token".equals(responseType)) {
      return redirectError(
          redirectUri, "invalid_request", "implicit grant is not supported", state);
    }

    Set<String> scopes = new HashSet<>(Arrays.asList(scope.split(" ")));
    LOG.info(
        "Authenticated user {} for client id {} and scopes {}",
        endUserDetails.getIdentifier(),
        clientId,
        scopes);

    AuthorizationCode authorizationCode =
        authorizationCodeService.createAndStoreAuthorizationState(
            clientId,
            redirectUri,
            scopes,
            endUserDetails.getIdentifier() != null ? endUserDetails.getIdentifier().toString() : "",
            nonce,
            code_challenge,
            code_challenge_method);

    return "redirect:"
        + redirectUri
        + "?code="
        + authorizationCode.getCode()
        + "&state="
        + state;
  }

  private String redirectError(
      URI redirectUri, String errorCode, String errorDescription, String state) {
    return "redirect:"
        + redirectUri.toString()
        + "?error="
        + errorCode
        + "&error_description="
        + errorDescription
        + "&state="
        + state;
  }

  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<String> handle(
      MissingServletRequestParameterException ex, HttpServletResponse httpServletResponse)
      throws IOException {
    LOG.warn("Invalid_request {}", ex.getMessage());
    httpServletResponse.sendError(400, "invalid_request");
    return ResponseEntity.badRequest()
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .body("error=invalid_request");
  }

  @ExceptionHandler(InvalidClientIdError.class)
  public ResponseEntity<String> handle(
      InvalidClientIdError ex, HttpServletResponse httpServletResponse) throws IOException {
    LOG.warn("Invalid client {}", ex.getMessage());
    httpServletResponse.sendError(400, "invalid client");
    return ResponseEntity.badRequest()
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .body("error=invalid client");
  }

  @ExceptionHandler(InvalidRedirectUriError.class)
  public ResponseEntity<String> handle(
      InvalidRedirectUriError ex, HttpServletResponse httpServletResponse) throws IOException {
    LOG.warn("Redirect uri mismatch {}", ex.getMessage());
    httpServletResponse.sendError(400, "redirect uri mismatch");
    return ResponseEntity.badRequest()
        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        .body("error=redirect uri mismatch");
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<String> handle(
      ConstraintViolationException ex, HttpServletResponse httpServletResponse) throws IOException {
    if (!ex.getConstraintViolations().isEmpty()) {
      ConstraintViolation<?> constraintViolation = ex.getConstraintViolations().iterator().next();
      if (constraintViolation.getPropertyPath().toString().contains("responseType")) {
        httpServletResponse.sendError(400, "unsupported_response_type");
        return ResponseEntity.badRequest()
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body("error=unsupported_response_type");
      } else {
        httpServletResponse.sendError(400, "invalid_request");
        return ResponseEntity.badRequest()
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body("error=invalid_request");
      }
    } else {
      httpServletResponse.sendError(400, "server_error");
      return ResponseEntity.badRequest()
          .contentType(MediaType.APPLICATION_FORM_URLENCODED)
          .body("error=server_error");
    }
  }

  static class InvalidClientIdError extends RuntimeException {
    InvalidClientIdError(String clientId) {
      super("Invalid client id " + clientId);
    }
  }

  static class InvalidRedirectUriError extends RuntimeException {
    InvalidRedirectUriError(String redirectUri) {
      super("Invalid redirect URI " + redirectUri);
    }
  }
}
