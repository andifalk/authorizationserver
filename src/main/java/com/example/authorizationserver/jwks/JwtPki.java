package com.example.authorizationserver.jwks;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class JwtPki {

  private RSAKey publicKey;

  private JWKSet jwkSet;

  private JWSSigner signer;

  private JWSVerifier verifier;

  private String issuer;

  public JwtPki(@Value("${auth-server.issuer}") String issuer) {
    this.issuer = issuer;
  }

  @PostConstruct
  public void initPki() throws JOSEException {
    RSAKey rsaJWK = new RSAKeyGenerator(2048).keyID("1").generate();
    this.publicKey = rsaJWK.toPublicJWK();
    this.signer = new RSASSASigner(rsaJWK);
    this.jwkSet = new JWKSet(this.publicKey);
    this.verifier = new RSASSAVerifier(this.publicKey);
  }

  public JWSSigner getSigner() {
    return signer;
  }

  public JWSVerifier getVerifier() {
    return verifier;
  }

  public RSAKey getPublicKey() {
    return publicKey;
  }

  public String getIssuer() {
    return issuer;
  }

  public JWKSet getJwkSet() {
    return jwkSet;
  }
}
