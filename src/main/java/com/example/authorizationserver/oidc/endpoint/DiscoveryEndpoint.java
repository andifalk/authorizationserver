package com.example.authorizationserver.oidc.endpoint;

import com.example.authorizationserver.jwks.JwtPki;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.oauth2.sdk.GrantType;
import com.nimbusds.oauth2.sdk.ResponseType;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.openid.connect.sdk.SubjectType;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;

import static com.nimbusds.oauth2.sdk.GrantType.AUTHORIZATION_CODE;
import static com.nimbusds.oauth2.sdk.GrantType.CLIENT_CREDENTIALS;
import static com.nimbusds.oauth2.sdk.GrantType.JWT_BEARER;
import static com.nimbusds.oauth2.sdk.GrantType.REFRESH_TOKEN;
import static com.nimbusds.oauth2.sdk.ResponseMode.FORM_POST;
import static com.nimbusds.oauth2.sdk.ResponseMode.QUERY;
import static com.nimbusds.oauth2.sdk.auth.ClientAuthenticationMethod.CLIENT_SECRET_BASIC;
import static com.nimbusds.oauth2.sdk.auth.ClientAuthenticationMethod.CLIENT_SECRET_POST;
import static com.nimbusds.oauth2.sdk.pkce.CodeChallengeMethod.PLAIN;
import static com.nimbusds.oauth2.sdk.pkce.CodeChallengeMethod.S256;
import static com.nimbusds.openid.connect.sdk.claims.ACR.PHR;
import static com.nimbusds.openid.connect.sdk.claims.ACR.PHRH;

@Controller
public class DiscoveryEndpoint {

  private static final GrantType TOKEN_EXCHANGE = new GrantType("urn:ietf:params:oauth:grant-type:token-exchange");

  private final JwtPki jwtPki;

  public DiscoveryEndpoint(JwtPki jwtPki) {
    this.jwtPki = jwtPki;
  }

  @GetMapping("/.well-known/openid-configuration")
  public HTTPResponse discoveryEndpoint() {

    OIDCProviderMetadata metadata = new OIDCProviderMetadata(
            new Issuer(jwtPki.getIssuer()),
            Collections.singletonList(SubjectType.PUBLIC),
            URI.create(jwtPki.getIssuer() + "/jwks"));
    metadata.setAuthorizationEndpointURI(URI.create(jwtPki.getIssuer() + "/authorize"));
    metadata.setTokenEndpointURI(URI.create(jwtPki.getIssuer() + "/token"));
    metadata.setUserInfoEndpointURI(URI.create(jwtPki.getIssuer() + "/userinfo"));
    metadata.setIntrospectionEndpointURI(URI.create(jwtPki.getIssuer() + "/introspect"));
    metadata.setRevocationEndpointURI(URI.create(jwtPki.getIssuer() + "/revoke"));
    metadata.setResponseTypes(Collections.singletonList(ResponseType.getDefault()));
    metadata.setIDTokenJWSAlgs(Collections.singletonList(JWSAlgorithm.RS256));
    metadata.setTokenEndpointJWSAlgs(Collections.singletonList(JWSAlgorithm.RS256));
    metadata.setScopes(new Scope("openid", "profile", "offline_access", "email", "address"));
    metadata.setGrantTypes(Arrays.asList(AUTHORIZATION_CODE, CLIENT_CREDENTIALS, REFRESH_TOKEN, JWT_BEARER, TOKEN_EXCHANGE));
    metadata.setSupportsBackChannelLogout(false);
    metadata.setSupportsFrontChannelLogout(false);
    metadata.setACRs(Arrays.asList(PHR, PHRH));
    metadata.setResponseModes(Arrays.asList(QUERY, FORM_POST));
    metadata.setCodeChallengeMethods(Arrays.asList(PLAIN, S256));
    metadata.setTokenEndpointAuthMethods(Arrays.asList(CLIENT_SECRET_BASIC,CLIENT_SECRET_POST));
    HTTPResponse response = new HTTPResponse(200);
    response.setContent(metadata.toJSONObject().toString());
    return response;
  }
}
