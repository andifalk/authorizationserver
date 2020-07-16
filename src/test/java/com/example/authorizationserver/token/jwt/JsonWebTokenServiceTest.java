package com.example.authorizationserver.token.jwt;

import com.example.authorizationserver.jwks.JwtPki;
import com.example.authorizationserver.user.model.Address;
import com.example.authorizationserver.user.model.Gender;
import com.example.authorizationserver.user.model.User;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.IdGenerator;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class JsonWebTokenServiceTest {

  private final JsonWebTokenService cut;

  JsonWebTokenServiceTest(@Autowired JwtPki jwtPki, @Autowired IdGenerator idGenerator) {
    this.cut = new JsonWebTokenService(jwtPki, idGenerator);
  }

  @Test
  void createPersonalizedToken() throws JOSEException, ParseException {
    String personalizedToken = cut.createPersonalizedToken(true, "myclient", List.of("myaudience"),
            Collections.singleton("openid"), new User(UUID.randomUUID(), Gender.MALE, "First", "Name",
                    "secret", "first.name@example.com", "fname", "123456", Collections.singleton("user"),
                    new Address("street", "12222", "city", "state", "country"),
                    LocalDateTime.now()), "nonce", LocalDateTime.now().plusMinutes(5));
    JWTClaimsSet parsedToken = cut.parseAndValidateToken(personalizedToken);
    assertThat(parsedToken).isNotNull();
  }

  @Test
  void createPersonalizedTokenWithAllScopes() throws JOSEException, ParseException {
    String personalizedToken = cut.createPersonalizedToken(true, "myclient", List.of("myaudience"),
            Set.of("openid", "profile", "email", "phone", "address"), new User(UUID.randomUUID(), Gender.MALE, "First", "Name",
                    "secret", "first.name@example.com", "fname", "123456", Collections.singleton("user"),
                    new Address("street", "12222", "city", "state", "country"),
                    LocalDateTime.now()), "nonce", LocalDateTime.now().plusMinutes(5));
    JWTClaimsSet parsedToken = cut.parseAndValidateToken(personalizedToken);
    assertThat(parsedToken).isNotNull();
  }

  @Test
  void createAnonymousToken() throws JOSEException, ParseException {
    String anonymousToken = cut.createAnonymousToken(true, "myclient", List.of("myaudience"),
            Collections.singleton("openid"), LocalDateTime.now().plusMinutes(5));
    JWTClaimsSet parsedToken = cut.parseAndValidateToken(anonymousToken);
    assertThat(parsedToken).isNotNull();
  }
}