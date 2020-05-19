package com.example.authorizationserver.token.jwt;

import com.example.authorizationserver.jwks.JwtPki;
import com.example.authorizationserver.oidc.common.Scope;
import com.example.authorizationserver.token.store.TokenService;
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
import java.util.Set;

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
          Set<String> scopes,
          User user,
          String nonce,
          LocalDateTime expiryDateTime)
          throws JOSEException {


    JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder()
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
            .claim("client_id", clientId)
            .claim("locale", "de");

    if (scopes.contains(Scope.PROFILE)) {
      builder
              .claim("nickname", user.getUsername())
              .claim("preferred_username", user.getUsername())
              .claim("family_name", user.getLastName())
              .claim("given_name", user.getFirstName())
              .claim("gender", user.getGender().name());
    }

    if (scopes.contains(Scope.EMAIL)) {
      builder
              .claim("email", user.getEmail())
              .claim("email_verified", Boolean.TRUE);
    }

    if (scopes.contains(Scope.PHONE)) {
      builder
              .claim("phone", user.getPhone())
              .claim("phone_verified", Boolean.TRUE)
              .claim("phone_number", user.getPhone())
              .claim("phone_number_verified", Boolean.TRUE);
    }

    if (scopes.contains(Scope.ADDRESS)) {
      builder
              .claim("formatted", user.getAddress().getStreet()
                      + "\n"
                      + user.getAddress().getZip()
                      + "\n"
                      + user.getAddress().getCity()
                      + "/n"
                      + user.getAddress().getCountry())
              .claim("street_address", user.getAddress().getStreet())
              .claim("locality", user.getAddress().getCity())
              .claim("region", user.getAddress().getState())
              .claim("postal_code", user.getAddress().getZip())
              .claim("country", user.getAddress().getCountry());
    }

    JWTClaimsSet claimsSet = builder.build();

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
          LocalDateTime expiryDateTime)
          throws JOSEException {
    JWTClaimsSet claimsSet =
            new JWTClaimsSet.Builder()
                    .subject(TokenService.ANONYMOUS_TOKEN)
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
