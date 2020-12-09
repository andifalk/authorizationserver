package com.example.authorizationserver.oauth.endpoint;

import com.example.authorizationserver.annotation.WebIntegrationTest;
import com.example.authorizationserver.oauth.client.model.RegisteredClient;
import com.example.authorizationserver.oauth.common.GrantType;
import com.example.authorizationserver.oauth.endpoint.token.resource.TokenResponse;
import com.example.authorizationserver.oauth.store.AuthorizationCode;
import com.example.authorizationserver.oauth.store.AuthorizationCodeService;
import com.example.authorizationserver.scim.model.ScimUserEntity;
import com.example.authorizationserver.scim.service.ScimService;
import com.example.authorizationserver.token.store.TokenService;
import com.example.authorizationserver.token.store.model.OpaqueToken;
import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.Base64;
import java.util.Optional;
import java.util.Set;

import static com.example.authorizationserver.oauth.endpoint.token.TokenEndpoint.ENDPOINT;
import static com.example.authorizationserver.oauth.pkce.ProofKeyForCodeExchangeVerifier.CHALLENGE_METHOD_S_256;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;

@WebIntegrationTest
class TokenEndpointIntegrationTest {

    public static final int EXPIRY = 600;
    public static final String BEARER = "Bearer";
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ScimService scimService;

    @Autowired
    private AuthorizationCodeService authorizationCodeService;

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
    void getOpenIdConnectTokensForAuthorizationCodeSuccess() {

        AuthorizationCode authorizationCode =
                authorizationCodeService.createAndStoreAuthorizationState(
                        "confidential-jwt",
                        RegisteredClient.DEFAULT_REDIRECT_URI,
                        Set.of("openid", "profile"),
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
    void getOAuthTokensForAuthorizationCodeSuccess() {

        AuthorizationCode authorizationCode =
                authorizationCodeService.createAndStoreAuthorizationState(
                        "confidential-jwt",
                        RegisteredClient.DEFAULT_REDIRECT_URI,
                        Set.of("profile"),
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
    void getTokenForAuthorizationCodeWithPkceSuccess() throws NoSuchAlgorithmException {

        String codeVerifier = RandomStringUtils.random(64, true, true);
        String codeChallenge = hashCodeChallenge(codeVerifier);

        AuthorizationCode authorizationCode =
                authorizationCodeService.createAndStoreAuthorizationState(
                        "public-jwt",
                        RegisteredClient.DEFAULT_REDIRECT_URI,
                        Set.of("openid", "profile"),
                        bwayne_user.getIdentifier().toString(),
                        "1234",
                        codeChallenge,
                        CHALLENGE_METHOD_S_256);

        TokenResponse tokenResponse =
                given()
                        .header(
                                "Authorization",
                                "Basic "
                                        + Base64.getEncoder()
                                        .encodeToString("public-jwt:demo".getBytes(StandardCharsets.UTF_8)))
                        .contentType(ContentType.URLENC)
                        .formParam("grant_type", GrantType.AUTHORIZATION_CODE.getGrant())
                        .formParam("code", authorizationCode.getCode())
                        .formParam("redirect_uri", authorizationCode.getRedirectUri().toString())
                        .formParam("client_id", "public-jwt")
                        .formParam("code_verifier", codeVerifier)
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
                tokenService.createAnonymousRefreshToken("confidential-jwt", Set.of("OPENID"), Duration.ofMinutes(5));

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
                        "confidential-jwt", bwayne_user, Set.of("OPENID"), Duration.ofMinutes(5));

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

    private String hashCodeChallenge(String codeVerifier) throws NoSuchAlgorithmException {
        final MessageDigest digest = MessageDigest.getInstance("SHA-256");
        final byte[] hashedBytes = digest.digest(codeVerifier.getBytes(UTF_8));
        return Base64.getUrlEncoder().withoutPadding().encodeToString(hashedBytes);
    }
}
