package com.example.authorizationserver.oauth.endpoint;

import com.example.authorizationserver.annotation.WebIntegrationTest;
import com.example.authorizationserver.oauth.client.model.RegisteredClient;
import com.example.authorizationserver.oauth.common.GrantType;
import com.example.authorizationserver.oauth.endpoint.resource.TokenResponse;
import com.example.authorizationserver.oauth.store.AuthorizationCode;
import com.example.authorizationserver.oauth.store.AuthorizationCodeService;
import com.example.authorizationserver.token.store.TokenService;
import com.example.authorizationserver.token.store.model.OpaqueToken;
import com.example.authorizationserver.user.model.User;
import com.example.authorizationserver.user.service.UserService;
import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import static com.example.authorizationserver.oauth.endpoint.TokenEndpoint.ENDPOINT;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;

@WebIntegrationTest
class TokenEndpointIntegrationTest {

  public static final int EXPIRY = 600;
  public static final String BEARER = "Bearer";
  @Autowired private WebApplicationContext webApplicationContext;

  @Autowired private UserService userService;

  @Autowired private AuthorizationCodeService authorizationCodeService;

  @Autowired private TokenService tokenService;

  private User bwayne_user;

  @BeforeEach
  void initMockMvc() {
    RestAssuredMockMvc.webAppContextSetup(webApplicationContext);
    Optional<User> bwayne = userService.findOneByUsername("bwayne");
    bwayne.ifPresent(user -> bwayne_user = user);
  }

  @Test
  void getTokenForAuthorizationCodeSuccess() {

    AuthorizationCode authorizationCode =
        authorizationCodeService.createAndStoreAuthorizationState(
            "confidential-jwt",
            RegisteredClient.DEFAULT_REDIRECT_URI,
            List.of("openid", "profile"),
            bwayne_user.getIdentifier().toString(),
            "1234",
            null,
            null);

    TokenResponse tokenResponse =
        given()
            .header(
                "Authorization",
                "Basic "
                    + Base64.getEncoder()
                        .encodeToString("confidential-jwt:demo".getBytes(StandardCharsets.UTF_8)))
            .contentType(ContentType.URLENC)
            .formParam("grant_type", GrantType.AUTHORIZATION_CODE.getGrant())
            .formParam("code", authorizationCode.getCode())
            .formParam("redirect_uri", authorizationCode.getRedirectUri().toString())
            .formParam("client_id", "confidential-jwt")
            .when()
            .post(ENDPOINT)
            .then()
            .log()
            .ifValidationFails()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body(not(empty()))
            .extract()
            .as(TokenResponse.class);
    assertThat(tokenResponse).describedAs("token response should be present").isNotNull();
    assertThat(tokenResponse.getAccess_token())
        .describedAs("access token should be present")
        .isNotBlank();
    assertThat(tokenResponse.getId_token()).describedAs("id token should be present").isNotBlank();
    assertThat(tokenResponse.getRefresh_token())
        .describedAs("refresh token should be present")
        .isNotBlank();
    assertThat(tokenResponse.getToken_type())
        .describedAs("token type must be %s", BEARER)
        .isEqualTo(BEARER);
    assertThat(tokenResponse.getExpires_in())
        .describedAs("expires in must be %s", EXPIRY)
        .isEqualTo(EXPIRY);
    assertThat(tokenResponse.getError()).describedAs("error must not be present").isNull();
  }

  @Test
  void getTokenForClientCredentialsSuccess() {

    TokenResponse tokenResponse =
        given()
            .header(
                "Authorization",
                "Basic "
                    + Base64.getEncoder()
                        .encodeToString("confidential-jwt:demo".getBytes(StandardCharsets.UTF_8)))
            .contentType(ContentType.URLENC)
            .formParam("grant_type", GrantType.CLIENT_CREDENTIALS.getGrant())
            .when()
            .post(ENDPOINT)
            .then()
            .log()
            .ifValidationFails()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body(not(empty()))
            .extract()
            .as(TokenResponse.class);
    assertThat(tokenResponse).describedAs("token response should be present").isNotNull();
    assertThat(tokenResponse.getAccess_token())
        .describedAs("access token should be present")
        .isNotBlank();
    assertThat(tokenResponse.getId_token()).describedAs("id token must not be present").isNull();
    assertThat(tokenResponse.getRefresh_token())
        .describedAs("refresh token should be present")
        .isNotBlank();
    assertThat(tokenResponse.getToken_type())
        .describedAs("token type must be %s", BEARER)
        .isEqualTo(BEARER);
    assertThat(tokenResponse.getExpires_in())
        .describedAs("expires in must be %s", EXPIRY)
        .isEqualTo(EXPIRY);
    assertThat(tokenResponse.getError()).describedAs("error must not be present").isNull();
  }

  @Test
  void getTokenForPasswordGrantSuccess() {

    TokenResponse tokenResponse =
        given()
            .header(
                "Authorization",
                "Basic "
                    + Base64.getEncoder()
                        .encodeToString("confidential-jwt:demo".getBytes(StandardCharsets.UTF_8)))
            .contentType(ContentType.URLENC)
            .formParam("grant_type", GrantType.PASSWORD.getGrant())
            .formParam("username", "bwayne")
            .formParam("password", "wayne")
            .when()
            .post(ENDPOINT)
            .then()
            .log()
            .ifValidationFails()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body(not(empty()))
            .extract()
            .as(TokenResponse.class);
    assertThat(tokenResponse).describedAs("token response should be present").isNotNull();
    assertThat(tokenResponse.getAccess_token())
        .describedAs("access token should be present")
        .isNotBlank();
    assertThat(tokenResponse.getId_token()).describedAs("id token must not be present").isNull();
    assertThat(tokenResponse.getRefresh_token())
        .describedAs("refresh token should be present")
        .isNotBlank();
    assertThat(tokenResponse.getToken_type())
        .describedAs("token type must be %s", BEARER)
        .isEqualTo(BEARER);
    assertThat(tokenResponse.getExpires_in())
        .describedAs("expires in must be %s", EXPIRY)
        .isEqualTo(EXPIRY);
    assertThat(tokenResponse.getError()).describedAs("error must not be present").isNull();
  }

  @Test
  void getTokenForAnonymousRefreshTokenGrantSuccess() {

    OpaqueToken refreshToken =
        tokenService.createAnonymousRefreshToken("confidential-jwt", Duration.ofMinutes(5));

    TokenResponse tokenResponse =
        given()
            .header(
                "Authorization",
                "Basic "
                    + Base64.getEncoder()
                        .encodeToString("confidential-jwt:demo".getBytes(StandardCharsets.UTF_8)))
            .contentType(ContentType.URLENC)
            .formParam("grant_type", GrantType.REFRESH_TOKEN.getGrant())
            .formParam("refresh_token", refreshToken.getValue())
            .when()
            .post(ENDPOINT)
            .then()
            .log()
            .ifValidationFails()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body(not(empty()))
            .extract()
            .as(TokenResponse.class);
    assertThat(tokenResponse).describedAs("token response should be present").isNotNull();
    assertThat(tokenResponse.getAccess_token())
        .describedAs("access token should be present")
        .isNotBlank();
    assertThat(tokenResponse.getId_token()).describedAs("id token must not be present").isNull();
    assertThat(tokenResponse.getRefresh_token())
        .describedAs("refresh token should be present")
        .isNotBlank();
    assertThat(tokenResponse.getRefresh_token())
        .describedAs("refresh token must be different to old one")
        .isNotBlank()
        .isNotEqualTo(refreshToken.getValue());
    assertThat(tokenResponse.getToken_type())
        .describedAs("token type must be %s", BEARER)
        .isEqualTo(BEARER);
    assertThat(tokenResponse.getExpires_in())
        .describedAs("expires in must be %s", EXPIRY)
        .isEqualTo(EXPIRY);
    assertThat(tokenResponse.getError()).describedAs("error must not be present").isNull();
  }

  @Test
  void getTokenForPersonalizedRefreshTokenGrantSuccess() {

    OpaqueToken refreshToken =
        tokenService.createPersonalizedRefreshToken(
            "confidential-jwt", bwayne_user, Duration.ofMinutes(5));

    TokenResponse tokenResponse =
        given()
            .header(
                "Authorization",
                "Basic "
                    + Base64.getEncoder()
                        .encodeToString("confidential-jwt:demo".getBytes(StandardCharsets.UTF_8)))
            .contentType(ContentType.URLENC)
            .formParam("grant_type", GrantType.REFRESH_TOKEN.getGrant())
            .formParam("refresh_token", refreshToken.getValue())
            .when()
            .post(ENDPOINT)
            .then()
            .log()
            .ifValidationFails()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body(not(empty()))
            .extract()
            .as(TokenResponse.class);
    assertThat(tokenResponse).describedAs("token response should be present").isNotNull();
    assertThat(tokenResponse.getAccess_token())
        .describedAs("access token should be present")
        .isNotBlank();
    assertThat(tokenResponse.getId_token()).describedAs("id token must not be present").isNull();
    assertThat(tokenResponse.getRefresh_token())
        .describedAs("refresh token should be present")
        .isNotBlank();
    assertThat(tokenResponse.getRefresh_token())
        .describedAs("refresh token must be different to old one")
        .isNotBlank()
        .isNotEqualTo(refreshToken.getValue());
    assertThat(tokenResponse.getToken_type())
        .describedAs("token type must be %s", BEARER)
        .isEqualTo(BEARER);
    assertThat(tokenResponse.getExpires_in())
        .describedAs("expires in must be %s", EXPIRY)
        .isEqualTo(EXPIRY);
    assertThat(tokenResponse.getError()).describedAs("error must not be present").isNull();
  }

  @ValueSource(strings = {"urn:ietf:params:oauth:grant-type:token-exchange", "invalid"})
  @ParameterizedTest
  void getTokenForTokenExchangeGrantNotSupportedFail(String grant) {

    TokenResponse tokenResponse =
            given()
                    .header(
                            "Authorization",
                            "Basic "
                                    + Base64.getEncoder()
                                    .encodeToString("confidential-jwt:demo".getBytes(StandardCharsets.UTF_8)))
                    .contentType(ContentType.URLENC)
                    .formParam("grant_type", grant)
                    .when()
                    .post(ENDPOINT)
                    .then()
                    .log()
                    .ifValidationFails()
                    .statusCode(400)
                    .contentType(ContentType.JSON)
                    .body(not(empty()))
                    .extract()
                    .as(TokenResponse.class);
    assertThat(tokenResponse).describedAs("token response should be present").isNotNull();
    assertThat(tokenResponse.getAccess_token())
            .describedAs("access token must not be present")
            .isNull();
    assertThat(tokenResponse.getId_token()).describedAs("id token must not be present").isNull();
    assertThat(tokenResponse.getRefresh_token())
            .describedAs("refresh token must not be present")
            .isNull();
    assertThat(tokenResponse.getToken_type())
            .describedAs("token type must not be present")
            .isNull();
    assertThat(tokenResponse.getExpires_in())
            .describedAs("expires in must be %s", 0)
            .isEqualTo(0);
    assertThat(tokenResponse.getError())
            .describedAs("error must be present").isEqualTo("unsupported_grant_type");
  }
}
