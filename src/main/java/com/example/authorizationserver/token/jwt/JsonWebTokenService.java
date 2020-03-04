package com.example.authorizationserver.token.jwt;

import com.example.authorizationserver.jwks.JwtPki;
import com.example.authorizationserver.oauth.common.Scope;
import com.example.authorizationserver.user.model.User;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class JsonWebTokenService {

  private final JwtPki jwtPki;

  public JsonWebTokenService(JwtPki jwtPki) {
    this.jwtPki = jwtPki;
  }

  public String createToken(List<String> audiences, String jti, List<String> scopes, User user, String nonce)
      throws JOSEException {
    JWTClaimsSet claimsSet =
        new JWTClaimsSet.Builder()
            .subject(user.getIdentifier().toString())
            .issuer(jwtPki.getIssuer())
            .expirationTime(new Date(new Date().getTime() + 60 * 1000))
            .audience(audiences)
            .issueTime(new Date())
            .notBeforeTime(new Date())
            .jwtID(jti)
            .claim("nonce", nonce)
            .claim("groups", user.getGroups())
            .claim("name", user.getUsername())
            .claim("email", user.getEmail())
            .claim("email_verified", "true")
            .claim("family_name", user.getLastName())
            .claim("given_name", user.getFirstName())
            .claim("gender", user.getGender().name())
            .claim("phone", user.getPhone())
            .claim("phone_verified", "true")
            .build();

    SignedJWT signedJWT =
        new SignedJWT(
            new JWSHeader.Builder(JWSAlgorithm.RS256)
                .keyID(jwtPki.getPublicKey().getKeyID())
                .build(),
            claimsSet);

    signedJWT.sign(jwtPki.getSigner());

    return signedJWT.serialize();
  }
}
