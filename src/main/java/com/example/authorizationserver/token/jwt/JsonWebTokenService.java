package com.example.authorizationserver.token.jwt;

import com.example.authorizationserver.jwks.JwtPki;
import com.example.authorizationserver.oidc.common.Scope;
import com.example.authorizationserver.token.store.TokenService;
import com.example.authorizationserver.user.model.User;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.stereotype.Service;
import org.springframework.util.IdGenerator;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class JsonWebTokenService {

  private static final JOSEObjectType JWT_TYP_ACCESS_TOKEN = new JOSEObjectType("at+jwt");

  private final JwtPki jwtPki;
  private final IdGenerator idGenerator;

  public JsonWebTokenService(JwtPki jwtPki, IdGenerator idGenerator) {
    this.jwtPki = jwtPki;
    this.idGenerator = idGenerator;
  }

  public JWTClaimsSet parseAndValidateToken(String token) throws ParseException, JOSEException {
    SignedJWT signedJWT = SignedJWT.parse(token);
    signedJWT.verify(jwtPki.getVerifier());
    return signedJWT.getJWTClaimsSet();
  }

  public String createPersonalizedToken(
          boolean isAccessToken,
          String clientId,
          List<String> audiences,
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
            .jwtID(idGenerator.generateId().toString())
            .claim("nonce", nonce)
            .claim("groups", user.getGroups())
            .claim("name", user.getUsername())
            .claim("client_id", clientId)
            .claim("locale", "de")
            .claim("ctx", TokenService.PERSONAL_TOKEN);

    if (!scopes.isEmpty()) {
      Set<String> scopesToCompare = scopes.stream().map(String::toUpperCase).collect(Collectors.toSet());
      builder.claim("scope", String.join(" ", scopes));

      if (scopesToCompare.contains(Scope.PROFILE.name())) {
        builder
                .claim("nickname", user.getUsername())
                .claim("preferred_username", user.getUsername())
                .claim("family_name", user.getLastName())
                .claim("given_name", user.getFirstName())
                .claim("gender", user.getGender().name());
      }

      if (scopesToCompare.contains(Scope.EMAIL.name())) {
        builder
                .claim("email", user.getEmail())
                .claim("email_verified", Boolean.TRUE);
      }

      if (scopesToCompare.contains(Scope.PHONE.name())) {
        builder
                .claim("phone", user.getPhone())
                .claim("phone_verified", Boolean.TRUE)
                .claim("phone_number", user.getPhone())
                .claim("phone_number_verified", Boolean.TRUE);
      }

      if (scopesToCompare.contains(Scope.ADDRESS.name())) {
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
    }

    JWTClaimsSet claimsSet = builder.build();

    SignedJWT signedJWT = createSignedJWT(isAccessToken, claimsSet);
    signedJWT.sign(jwtPki.getSigner());

    return signedJWT.serialize();
  }

  public String createAnonymousToken(
          boolean isAccessToken,
          String clientId,
          List<String> audiences,
          Set<String> scopes,
          LocalDateTime expiryDateTime)
          throws JOSEException {
    JWTClaimsSet.Builder builder =
            new JWTClaimsSet.Builder()
                    .subject(clientId)
                    .issuer(jwtPki.getIssuer())
                    .expirationTime(Date.from(expiryDateTime.atZone(ZoneId.systemDefault()).toInstant()))
                    .audience(audiences)
                    .issueTime(new Date())
                    .notBeforeTime(new Date())
                    .jwtID(idGenerator.generateId().toString())
                    .claim("client_id", clientId)
                    .claim("ctx", TokenService.ANONYMOUS_TOKEN);

    if (!scopes.isEmpty()) {
      builder.claim("scope", String.join(" ", scopes));
    }

    JWTClaimsSet claimsSet = builder.build();

    SignedJWT signedJWT = createSignedJWT(isAccessToken, claimsSet);
    signedJWT.sign(jwtPki.getSigner());

    return signedJWT.serialize();
  }

  private SignedJWT createSignedJWT(boolean isAccessToken, JWTClaimsSet claimsSet) {
    JWSHeader.Builder jwsHeaderBuilder = new JWSHeader.Builder(JWSAlgorithm.RS256);
    jwsHeaderBuilder.keyID(jwtPki.getPublicKey().getKeyID());

    if (isAccessToken) {
      jwsHeaderBuilder.type(JWT_TYP_ACCESS_TOKEN);
    }

    return new SignedJWT(
            jwsHeaderBuilder.build(),
            claimsSet);
  }
}
