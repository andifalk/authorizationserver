package com.example.authorizationserver.oauth.endpoint;

import com.example.authorizationserver.annotation.WebIntegrationTest;
import com.example.authorizationserver.oauth.common.GrantType;
import com.example.authorizationserver.oauth.common.TokenType;
import com.example.authorizationserver.oauth.endpoint.token.resource.TokenResponse;
import com.example.authorizationserver.scim.model.ScimUserEntity;
import com.example.authorizationserver.scim.service.ScimService;
import com.example.authorizationserver.token.store.TokenService;
import com.example.authorizationserver.token.store.model.JsonWebToken;
import com.example.authorizationserver.token.store.model.OpaqueToken;
import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.Optional;
import java.util.Set;

import static com.example.authorizationserver.oauth.endpoint.token.TokenEndpoint.ENDPOINT;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;

@WebIntegrationTest
class TokenExchangeEndpointIntegrationTest {

    public static final int EXPIRY = 600;
    public static final String BEARER = "Bearer";
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ScimService scimService;

    @Autowired
    private TokenService tokenService;

    private ScimUserEntity bwayne_user;

    @BeforeEach
    void initMockMvc() {
        RestAssuredMockMvc.webAppContextSetup(webApplicationContext);
        Optional<ScimUserEntity> bwayne = scimService.findUserByUserName("bwayne");
        bwayne.ifPresent(user -> bwayne_user = user);
    }

    @Test
    void exchangeJwtAccessTokenSuccess() {

        JsonWebToken jwtAccessToken = tokenService.createPersonalizedJwtAccessToken(bwayne_user, "confidential-jwt", "1234", Set.of("openid", "profile"), Duration.ofMinutes(10));

        TokenResponse tokenExchangeResponse =
                given()
                        .header(
                                "Authorization",
                                "Basic "
                                        + Base64.getEncoder()
                                        .encodeToString("token-exchange:demo".getBytes(StandardCharsets.UTF_8)))
                        .contentType(ContentType.URLENC)
                        .formParam("grant_type", GrantType.TOKEN_EXCHANGE.getGrant())
                        .formParam("resource", "myresource")
                        .formParam("audience", "myaudience")
                        .formParam("scope", "openid profile address")
                        .formParam("requested_token_type", TokenType.ACCESS_TOKEN.getIdentifier())
                        .formParam("subject_token", jwtAccessToken.getValue())
                        .formParam("subject_token_type", TokenType.ACCESS_TOKEN.getIdentifier())
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
        assertThat(tokenExchangeResponse).describedAs("token exchange response should be present").isNotNull();
        assertThat(tokenExchangeResponse.getAccess_token())
                .describedAs("access token should be present")
                .isNotBlank();
        assertThat(tokenExchangeResponse.getIssued_token_type()).describedAs("issued token type should be present").isNotBlank();
        assertThat(tokenExchangeResponse.getRefresh_token())
                .describedAs("refresh token should be present")
                .isNotBlank();
        assertThat(tokenExchangeResponse.getToken_type())
                .describedAs("token type must be %s", BEARER)
                .isEqualTo(BEARER);
        assertThat(tokenExchangeResponse.getScope())
                .describedAs("scope must be %s", "openid profile address")
                .isEqualTo("openid profile address");
        assertThat(tokenExchangeResponse.getExpires_in())
                .describedAs("expires in must be %s", EXPIRY)
                .isEqualTo(EXPIRY);
        assertThat(tokenExchangeResponse.getError()).describedAs("error must not be present").isNull();
    }

    @Test
    void exchangeAnonymousJwtAccessTokenSuccess() {

        JsonWebToken jwtAccessToken = tokenService.createAnonymousJwtAccessToken("confidential-jwt", Set.of("openid", "profile"), Duration.ofMinutes(10));

        TokenResponse tokenExchangeResponse =
                given()
                        .header(
                                "Authorization",
                                "Basic "
                                        + Base64.getEncoder()
                                        .encodeToString("token-exchange:demo".getBytes(StandardCharsets.UTF_8)))
                        .contentType(ContentType.URLENC)
                        .formParam("grant_type", GrantType.TOKEN_EXCHANGE.getGrant())
                        .formParam("resource", "myresource")
                        .formParam("audience", "myaudience")
                        .formParam("scope", "openid profile address")
                        .formParam("requested_token_type", TokenType.ACCESS_TOKEN.getIdentifier())
                        .formParam("subject_token", jwtAccessToken.getValue())
                        .formParam("subject_token_type", TokenType.ACCESS_TOKEN.getIdentifier())
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
        assertThat(tokenExchangeResponse).describedAs("token exchange response should be present").isNotNull();
        assertThat(tokenExchangeResponse.getAccess_token())
                .describedAs("access token should be present")
                .isNotBlank();
        assertThat(tokenExchangeResponse.getIssued_token_type()).describedAs("issued token type should be present").isNotBlank();
        assertThat(tokenExchangeResponse.getRefresh_token())
                .describedAs("refresh token should be present")
                .isNotBlank();
        assertThat(tokenExchangeResponse.getToken_type())
                .describedAs("token type must be %s", BEARER)
                .isEqualTo(BEARER);
        assertThat(tokenExchangeResponse.getScope())
                .describedAs("scope must be %s", "openid profile address")
                .isEqualTo("openid profile address");
        assertThat(tokenExchangeResponse.getExpires_in())
                .describedAs("expires in must be %s", EXPIRY)
                .isEqualTo(EXPIRY);
        assertThat(tokenExchangeResponse.getError()).describedAs("error must not be present").isNull();
    }

    @Test
    void exchangeOpaqueAccessTokenSuccess() {

        OpaqueToken opaqueAccessToken = tokenService.createPersonalizedOpaqueAccessToken(bwayne_user, "confidential-jwt", Set.of("openid", "profile"), Duration.ofMinutes(10));

        TokenResponse tokenExchangeResponse =
                given()
                        .header(
                                "Authorization",
                                "Basic "
                                        + Base64.getEncoder()
                                        .encodeToString("token-exchange:demo".getBytes(StandardCharsets.UTF_8)))
                        .contentType(ContentType.URLENC)
                        .formParam("grant_type", GrantType.TOKEN_EXCHANGE.getGrant())
                        .formParam("resource", "myresource")
                        .formParam("audience", "myaudience")
                        .formParam("scope", "openid profile address")
                        .formParam("requested_token_type", TokenType.ACCESS_TOKEN.getIdentifier())
                        .formParam("subject_token", opaqueAccessToken.getValue())
                        .formParam("subject_token_type", TokenType.ACCESS_TOKEN.getIdentifier())
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
        assertThat(tokenExchangeResponse).describedAs("token exchange response should be present").isNotNull();
        assertThat(tokenExchangeResponse.getAccess_token())
                .describedAs("access token should be present")
                .isNotBlank();
        assertThat(tokenExchangeResponse.getIssued_token_type()).describedAs("issued token type should be present").isNotBlank();
        assertThat(tokenExchangeResponse.getRefresh_token())
                .describedAs("refresh token should be present")
                .isNotBlank();
        assertThat(tokenExchangeResponse.getToken_type())
                .describedAs("token type must be %s", BEARER)
                .isEqualTo(BEARER);
        assertThat(tokenExchangeResponse.getScope())
                .describedAs("scope must be %s", "openid profile address")
                .isEqualTo("openid profile address");
        assertThat(tokenExchangeResponse.getExpires_in())
                .describedAs("expires in must be %s", EXPIRY)
                .isEqualTo(EXPIRY);
        assertThat(tokenExchangeResponse.getError()).describedAs("error must not be present").isNull();
    }

    @Test
    void exchangeJwtIDTokenSuccess() {

        JsonWebToken jwtIDToken = tokenService.createIdToken(bwayne_user, "confidential-jwt", "1234", Set.of("openid", "profile"), Duration.ofMinutes(10));

        TokenResponse tokenExchangeResponse =
                given()
                        .header(
                                "Authorization",
                                "Basic "
                                        + Base64.getEncoder()
                                        .encodeToString("token-exchange:demo".getBytes(StandardCharsets.UTF_8)))
                        .contentType(ContentType.URLENC)
                        .formParam("grant_type", GrantType.TOKEN_EXCHANGE.getGrant())
                        .formParam("resource", "myresource")
                        .formParam("audience", "myaudience")
                        .formParam("scope", "openid profile address")
                        .formParam("requested_token_type", TokenType.ACCESS_TOKEN.getIdentifier())
                        .formParam("subject_token", jwtIDToken.getValue())
                        .formParam("subject_token_type", TokenType.ID_TOKEN.getIdentifier())
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
        assertThat(tokenExchangeResponse).describedAs("token exchange response should be present").isNotNull();
        assertThat(tokenExchangeResponse.getAccess_token())
                .describedAs("access token should be present")
                .isNotBlank();
        assertThat(tokenExchangeResponse.getIssued_token_type()).describedAs("issued token type should be present").isNotBlank();
        assertThat(tokenExchangeResponse.getRefresh_token())
                .describedAs("refresh token should be present")
                .isNotBlank();
        assertThat(tokenExchangeResponse.getToken_type())
                .describedAs("token type must be %s", BEARER)
                .isEqualTo(BEARER);
        assertThat(tokenExchangeResponse.getScope())
                .describedAs("scope must be %s", "openid profile address")
                .isEqualTo("openid profile address");
        assertThat(tokenExchangeResponse.getExpires_in())
                .describedAs("expires in must be %s", EXPIRY)
                .isEqualTo(EXPIRY);
        assertThat(tokenExchangeResponse.getError()).describedAs("error must not be present").isNull();
    }

    @Test
    void exchangeJwtRefreshTokenSuccess() {

        OpaqueToken refreshToken = tokenService.createPersonalizedRefreshToken("confidential-jwt", bwayne_user, Set.of("openid", "profile"), Duration.ofMinutes(10));

        TokenResponse tokenExchangeResponse =
                given()
                        .header(
                                "Authorization",
                                "Basic "
                                        + Base64.getEncoder()
                                        .encodeToString("token-exchange:demo".getBytes(StandardCharsets.UTF_8)))
                        .contentType(ContentType.URLENC)
                        .formParam("grant_type", GrantType.TOKEN_EXCHANGE.getGrant())
                        .formParam("resource", "myresource")
                        .formParam("audience", "myaudience")
                        .formParam("scope", "openid profile address")
                        .formParam("requested_token_type", TokenType.ACCESS_TOKEN.getIdentifier())
                        .formParam("subject_token", refreshToken.getValue())
                        .formParam("subject_token_type", TokenType.REFRESH_TOKEN.getIdentifier())
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
        assertThat(tokenExchangeResponse).describedAs("token exchange response should be present").isNotNull();
        assertThat(tokenExchangeResponse.getAccess_token())
                .describedAs("access token should be present")
                .isNotBlank();
        assertThat(tokenExchangeResponse.getIssued_token_type()).describedAs("issued token type should be present").isNotBlank();
        assertThat(tokenExchangeResponse.getRefresh_token())
                .describedAs("refresh token should be present")
                .isNotBlank();
        assertThat(tokenExchangeResponse.getToken_type())
                .describedAs("token type must be %s", BEARER)
                .isEqualTo(BEARER);
        assertThat(tokenExchangeResponse.getScope())
                .describedAs("scope must be %s", "openid profile address")
                .isEqualTo("openid profile address");
        assertThat(tokenExchangeResponse.getExpires_in())
                .describedAs("expires in must be %s", EXPIRY)
                .isEqualTo(EXPIRY);
        assertThat(tokenExchangeResponse.getError()).describedAs("error must not be present").isNull();
    }
}
