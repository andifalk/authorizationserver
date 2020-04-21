package com.example.authorizationserver.oidc.endpoint.discovery;

import java.util.ArrayList;
import java.util.List;

public class Discovery {

  // URL using the https scheme with no query or fragment component that the OP asserts as its
  // Issuer Identifier
  private String issuer;

  // URL of the OP's OAuth 2.0 Authorization Endpoint
  private String authorization_endpoint;

  // URL of the OP's OAuth 2.0 Token Endpoint
  private String token_endpoint;

  // URL of the OP's UserInfo Endpoint
  private String userinfo_endpoint;

  // URL of the OP's JSON Web Key Set [JWK] document
  private String jwks_uri;

  // URL of the OP's Dynamic Client Registration Endpoint
  private String registration_endpoint;

  // URL of OAuth2 introspection endpoint
  private String introspection_endpoint;

  // URL of OAuth2 revocation endpoint
  private String revocation_endpoint;

  // URL of OAuth2 device authorization endpoint
  private String device_authorization_endpoint;

  // URL of OAuth2 request object endpoint
  private String request_object_endpoint;

  // URL of OAuth2 pushed authorization request endpoint
  private String pushed_authorization_request_endpoint;

  // JSON array containing a list of the OAuth 2.0 [RFC6749] scope values that this server supports
  private List<String> scopes_supported = new ArrayList<>();

  // JSON array containing a list of the OAuth 2.0 response_type values that this OP supports.
  // Dynamic OpenID Providers MUST support the code, id_token, and the token id_token Response Type
  // values.
  private List<String> response_types_supported = new ArrayList<>();

  // JSON array containing a list of the OAuth 2.0 response_mode values that this OP supports, as
  // specified in OAuth 2.0 Multiple Response Type Encoding Practices [OAuth.Responses]. If omitted,
  // the default for Dynamic OpenID Providers is ["query", "fragment"].
  private List<String> response_modes_supported = new ArrayList<>();

  // JSON array containing a list of the OAuth 2.0 Grant Type values that this OP supports. Dynamic
  // OpenID Providers MUST support the authorization_code and implicit Grant Type values and MAY
  // support other Grant Types. If omitted, the default value is ["authorization_code", "implicit"].
  private List<String> grant_types_supported = new ArrayList<>();

  // JSON array containing a list of the Authentication Context Class References that this OP
  // supports.
  private List<String> acr_values_supported = new ArrayList<>();

  // JSON array containing a list of the Subject Identifier types that this OP supports. Valid types
  // include pairwise and public.
  private List<String> subject_types_supported = new ArrayList<>();

  // JSON array containing a list of the JWS signing algorithms (alg values) supported by the OP for
  // the ID Token to encode the Claims in a JWT [JWT]. The algorithm RS256 MUST be included. The
  // value none MAY be supported, but MUST NOT be used unless the Response Type used returns no ID
  // Token from the Authorization Endpoint (such as when using the Authorization Code Flow).
  private List<String> id_token_signing_alg_values_supported = new ArrayList<>();

  // JSON array containing a list of Client Authentication methods supported by this Token Endpoint.
  // The options are client_secret_post, client_secret_basic, client_secret_jwt, and
  // private_key_jwt, as described in Section 9 of OpenID Connect Core 1.0 [OpenID.Core]. Other
  // authentication methods MAY be defined by extensions. If omitted, the default is
  // client_secret_basic -- the HTTP Basic Authentication Scheme specified in Section 2.3.1 of OAuth
  // 2.0 [RFC6749].
  private List<String> token_endpoint_auth_methods_supported = new ArrayList<>();

  // JSON array containing a list of the JWS signing algorithms (alg values) supported by the Token
  // Endpoint for the signature on the JWT [JWT] used to authenticate the Client at the Token
  // Endpoint for the private_key_jwt and client_secret_jwt authentication methods. Servers SHOULD
  // support RS256. The value none MUST NOT be used.
  private List<String> token_endpoint_auth_signing_alg_values_supported = new ArrayList<>();

  // JSON array containing a list of the Claim Names of the Claims that the OpenID Provider MAY be
  // able to supply values for. Note that for privacy or other reasons, this might not be an
  // exhaustive list.
  private List<String> claims_supported = new ArrayList<>();

  // Hashing algorithms supported for PKCE challenge
  private List<String> code_challenge_methods_supported = new ArrayList<>();

  public String getIssuer() {
    return issuer;
  }

  public void setIssuer(String issuer) {
    this.issuer = issuer;
  }

  public String getAuthorization_endpoint() {
    return authorization_endpoint;
  }

  public void setAuthorization_endpoint(String authorization_endpoint) {
    this.authorization_endpoint = authorization_endpoint;
  }

  public String getToken_endpoint() {
    return token_endpoint;
  }

  public void setToken_endpoint(String token_endpoint) {
    this.token_endpoint = token_endpoint;
  }

  public String getUserinfo_endpoint() {
    return userinfo_endpoint;
  }

  public void setUserinfo_endpoint(String userinfo_endpoint) {
    this.userinfo_endpoint = userinfo_endpoint;
  }

  public String getJwks_uri() {
    return jwks_uri;
  }

  public void setJwks_uri(String jwks_uri) {
    this.jwks_uri = jwks_uri;
  }

  public String getRegistration_endpoint() {
    return registration_endpoint;
  }

  public void setRegistration_endpoint(String registration_endpoint) {
    this.registration_endpoint = registration_endpoint;
  }

  public String getIntrospection_endpoint() {
    return introspection_endpoint;
  }

  public void setIntrospection_endpoint(String introspection_endpoint) {
    this.introspection_endpoint = introspection_endpoint;
  }

  public String getRevocation_endpoint() {
    return revocation_endpoint;
  }

  public void setRevocation_endpoint(String revocation_endpoint) {
    this.revocation_endpoint = revocation_endpoint;
  }

  public String getDevice_authorization_endpoint() {
    return device_authorization_endpoint;
  }

  public void setDevice_authorization_endpoint(String device_authorization_endpoint) {
    this.device_authorization_endpoint = device_authorization_endpoint;
  }

  public String getRequest_object_endpoint() {
    return request_object_endpoint;
  }

  public void setRequest_object_endpoint(String request_object_endpoint) {
    this.request_object_endpoint = request_object_endpoint;
  }

  public String getPushed_authorization_request_endpoint() {
    return pushed_authorization_request_endpoint;
  }

  public void setPushed_authorization_request_endpoint(
      String pushed_authorization_request_endpoint) {
    this.pushed_authorization_request_endpoint = pushed_authorization_request_endpoint;
  }

  public List<String> getScopes_supported() {
    return scopes_supported;
  }

  public void setScopes_supported(List<String> scopes_supported) {
    this.scopes_supported = scopes_supported;
  }

  public List<String> getResponse_types_supported() {
    return response_types_supported;
  }

  public void setResponse_types_supported(List<String> response_types_supported) {
    this.response_types_supported = response_types_supported;
  }

  public List<String> getResponse_modes_supported() {
    return response_modes_supported;
  }

  public void setResponse_modes_supported(List<String> response_modes_supported) {
    this.response_modes_supported = response_modes_supported;
  }

  public List<String> getGrant_types_supported() {
    return grant_types_supported;
  }

  public void setGrant_types_supported(List<String> grant_types_supported) {
    this.grant_types_supported = grant_types_supported;
  }

  public List<String> getAcr_values_supported() {
    return acr_values_supported;
  }

  public void setAcr_values_supported(List<String> acr_values_supported) {
    this.acr_values_supported = acr_values_supported;
  }

  public List<String> getSubject_types_supported() {
    return subject_types_supported;
  }

  public void setSubject_types_supported(List<String> subject_types_supported) {
    this.subject_types_supported = subject_types_supported;
  }

  public List<String> getId_token_signing_alg_values_supported() {
    return id_token_signing_alg_values_supported;
  }

  public void setId_token_signing_alg_values_supported(
      List<String> id_token_signing_alg_values_supported) {
    this.id_token_signing_alg_values_supported = id_token_signing_alg_values_supported;
  }

  public List<String> getToken_endpoint_auth_methods_supported() {
    return token_endpoint_auth_methods_supported;
  }

  public void setToken_endpoint_auth_methods_supported(
      List<String> token_endpoint_auth_methods_supported) {
    this.token_endpoint_auth_methods_supported = token_endpoint_auth_methods_supported;
  }

  public List<String> getToken_endpoint_auth_signing_alg_values_supported() {
    return token_endpoint_auth_signing_alg_values_supported;
  }

  public void setToken_endpoint_auth_signing_alg_values_supported(
      List<String> token_endpoint_auth_signing_alg_values_supported) {
    this.token_endpoint_auth_signing_alg_values_supported =
        token_endpoint_auth_signing_alg_values_supported;
  }

  public List<String> getClaims_supported() {
    return claims_supported;
  }

  public void setClaims_supported(List<String> claims_supported) {
    this.claims_supported = claims_supported;
  }

  public List<String> getCode_challenge_methods_supported() {
    return code_challenge_methods_supported;
  }

  public void setCode_challenge_methods_supported(List<String> code_challenge_methods_supported) {
    this.code_challenge_methods_supported = code_challenge_methods_supported;
  }
}
