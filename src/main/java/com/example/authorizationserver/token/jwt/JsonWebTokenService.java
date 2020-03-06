package com.example.authorizationserver.token.jwt;

import com.example.authorizationserver.jwks.JwtPki;
import com.example.authorizationserver.user.model.User;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
public class JsonWebTokenService {

  private final JwtPki jwtPki;

  public JsonWebTokenService(JwtPki jwtPki) {
    this.jwtPki = jwtPki;
  }

  public JWTClaimsSet parseAndValidateToken(String token) throws ParseException, JOSEException {
    SignedJWT signedJWT = SignedJWT.parse(token);
    signedJWT.verify(jwtPki.getVerifier());
    return signedJWT.getJWTClaimsSet();
  }

  public String createPersonalizedToken(
      String clientId,
      List<String> audiences,
      String jti,
      List<String> scopes,
      User user,
      String nonce,
      LocalDateTime expiryDateTime)
      throws JOSEException {
    JWTClaimsSet claimsSet =
        new JWTClaimsSet.Builder()
            .subject(user.getIdentifier().toString())
            .issuer(jwtPki.getIssuer())
            .expirationTime(Date.from(expiryDateTime.atZone(ZoneId.systemDefault()).toInstant()))
            .audience(audiences)
            .issueTime(new Date())
            .notBeforeTime(new Date())
            .jwtID(jti)
            .claim("nonce", nonce)
            .claim("groups", user.getGroups())
            .claim("name", user.getUsername())
            .claim("email", user.getEmail())
            .claim("email_verified", Boolean.TRUE)
            .claim("family_name", user.getLastName())
            .claim("given_name", user.getFirstName())
            .claim("gender", user.getGender().name())
            .claim("phone", user.getPhone())
            .claim("phone_verified", Boolean.TRUE)
            .claim("client_id", clientId)
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

  public String createAnonymousToken(
      String clientId,
      List<String> audiences,
      String jti,
      List<String> scopes,
      LocalDateTime expiryDateTime)
      throws JOSEException {
    JWTClaimsSet claimsSet =
        new JWTClaimsSet.Builder()
            .subject("anonymous")
            .issuer(jwtPki.getIssuer())
            .expirationTime(Date.from(expiryDateTime.atZone(ZoneId.systemDefault()).toInstant()))
            .audience(audiences)
            .issueTime(new Date())
            .notBeforeTime(new Date())
            .jwtID(jti)
            .claim("client_id", clientId)
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
