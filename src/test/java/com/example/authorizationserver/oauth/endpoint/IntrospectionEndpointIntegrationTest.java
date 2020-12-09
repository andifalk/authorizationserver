package com.example.authorizationserver.oauth.endpoint;

import com.example.authorizationserver.annotation.WebIntegrationTest;
import com.example.authorizationserver.oauth.endpoint.introspection.resource.IntrospectionResponse;
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

import static com.example.authorizationserver.oauth.endpoint.introspection.IntrospectionEndpoint.ENDPOINT;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;

@WebIntegrationTest
class IntrospectionEndpointIntegrationTest {

  @Autowired
  private TokenService tokenService;
  @Autowired
  private ScimService scimService;
  @Autowired
  private WebApplicationContext webApplicationContext;
  private ScimUserEntity bwayne_user;

  @BeforeEach
  void initMockMvc() {
    RestAssuredMockMvc.webAppContextSetup(webApplicationContext);
    Optional<ScimUserEntity> bwayne = scimService.findUserByUserName("bwayne");
    bwayne.ifPresent(user -> bwayne_user = user);
  }

  @Test
  void introspectionWithPersonalJwtToken() {
    JsonWebToken jsonWebToken =
            tokenService.createPersonalizedJwtAccessToken(
                    bwayne_user, "confidential-jwt", "nonce", Set.of("OPENID"), Duration.ofMinutes(5));
    IntrospectionResponse introspectionResponse =
            given()
                    .header(
                            "Authorization",
                            "Basic "
                                    + Base64.getEncoder()
                                    .encodeToString("confidential-jwt:demo".getBytes(StandardCharsets.UTF_8)))
                    .contentType(ContentType.URLENC)
                    .formParam("token", jsonWebToken.getValue())
                    .when()
                    .post(ENDPOINT)
                    .then()
                    .log()
                    .ifValidationFails()
                    .statusCode(200)
                    .contentType(ContentType.JSON)
                    .body(not(empty()))
                    .extract()
                    .as(IntrospectionResponse.class);
    assertThat(introspectionResponse).isNotNull();
    assertThat(introspectionResponse.isActive()).isEqualTo(true);
    assertThat(introspectionResponse.getSub()).isEqualTo(bwayne_user.getIdentifier().toString());
  }

  @Test
  void introspectionWithAnonymousJwtToken() {
    JsonWebToken jsonWebToken =
            tokenService.createAnonymousJwtAccessToken("confidential-jwt", Set.of("OPENID"), Duration.ofMinutes(5));
    IntrospectionResponse introspectionResponse =
            given()
                    .header(
                            "Authorization",
                            "Basic "
                                    + Base64.getEncoder()
                                    .encodeToString("confidential-jwt:demo".getBytes(StandardCharsets.UTF_8)))
                    .contentType(ContentType.URLENC)
                    .formParam("token", jsonWebToken.getValue())
                    .when()
                    .post(ENDPOINT)
                    .then()
                    .log()
                    .ifValidationFails()
                    .statusCode(200)
                    .contentType(ContentType.JSON)
                    .body(not(empty()))
                    .extract()
                    .as(IntrospectionResponse.class);
    assertThat(introspectionResponse).isNotNull();
    assertThat(introspectionResponse.isActive()).isEqualTo(true);
    assertThat(introspectionResponse.getSub()).isEqualTo("confidential-jwt");
  }

  @Test
  void introspectionWithPersonalOpaqueToken() {

    OpaqueToken opaqueToken =
            tokenService.createPersonalizedOpaqueAccessToken(
                    bwayne_user, "confidential-opaque", Set.of("OPENID"), Duration.ofMinutes(5));

    IntrospectionResponse introspectionResponse =
            given()
                    .header(
                            "Authorization",
                            "Basic "
                                    + Base64.getEncoder()
                                    .encodeToString(
                                            "confidential-opaque:demo".getBytes(StandardCharsets.UTF_8)))
                    .contentType(ContentType.URLENC)
                    .formParam("token", opaqueToken.getValue())
                    .when()
                    .post(ENDPOINT)
                    .then()
                    .log()
                    .ifValidationFails()
                    .statusCode(200)
                    .contentType(ContentType.JSON)
                    .body(not(empty()))
                    .extract()
                    .as(IntrospectionResponse.class);
    assertThat(introspectionResponse).isNotNull();
    assertThat(introspectionResponse.isActive()).isEqualTo(true);
    assertThat(introspectionResponse.getSub()).isEqualTo(bwayne_user.getIdentifier().toString());
  }

  @Test
  void introspectionWithAnonymousOpaqueToken() {

    OpaqueToken opaqueToken =
            tokenService.createAnonymousOpaqueAccessToken("confidential-opaque", Set.of("OPENID"), Duration.ofMinutes(5));

    IntrospectionResponse introspectionResponse =
            given()
                    .header(
                            "Authorization",
                            "Basic "
                                    + Base64.getEncoder()
                                    .encodeToString(
                                            "confidential-opaque:demo".getBytes(StandardCharsets.UTF_8)))
                    .contentType(ContentType.URLENC)
                    .formParam("token", opaqueToken.getValue())
                    .when()
                    .post(ENDPOINT)
                    .then()
                    .log()
                    .ifValidationFails()
                    .statusCode(200)
                    .contentType(ContentType.JSON)
                    .body(not(empty()))
                    .extract()
                    .as(IntrospectionResponse.class);
    assertThat(introspectionResponse).isNotNull();
    assertThat(introspectionResponse.isActive()).isEqualTo(true);
    assertThat(introspectionResponse.getSub()).isEqualTo(TokenService.ANONYMOUS_TOKEN);
  }

  @Test
  void introspectionWithInvalidAuthentication() {

    IntrospectionResponse introspectionResponse =
            given()
                    .header("Authorization", "Basic 12345:122")
                    .contentType(ContentType.URLENC)
                    .formParam("token", "test")
                    .when()
                    .post(ENDPOINT)
                    .then()
                    .log()
                    .ifValidationFails()
                    .statusCode(401)
                    .contentType(ContentType.JSON)
                    .body(not(empty()))
                    .extract()
                    .as(IntrospectionResponse.class);
    assertThat(introspectionResponse).isNotNull();
    assertThat(introspectionResponse.getError()).isEqualTo("invalid_client");
  }

  @Test
  void introspectionWithInvalidToken() {

    OpaqueToken opaqueToken =
            tokenService.createPersonalizedOpaqueAccessToken(
                    bwayne_user, "confidential-opaque", Set.of("OPENID"), Duration.ofMinutes(5));

    IntrospectionResponse introspectionResponse =
            given()
                    .header(
                            "Authorization",
                            "Basic "
                                    + Base64.getEncoder()
                                    .encodeToString(
                                            "confidential-opaque:demo".getBytes(StandardCharsets.UTF_8)))
                    .contentType(ContentType.URLENC)
                    .formParam("token", "1234")
                    .when()
                    .post(ENDPOINT)
                    .then()
                    .log()
                    .ifValidationFails()
                    .statusCode(200)
                    .contentType(ContentType.JSON)
                    .body(not(empty()))
                    .extract()
                    .as(IntrospectionResponse.class);
    assertThat(introspectionResponse).isNotNull();
    assertThat(introspectionResponse.isActive()).isEqualTo(false);
    assertThat(introspectionResponse.getSub()).isNull();
  }

  @Test
  void introspectionWithExpiredToken() throws InterruptedException {

    OpaqueToken opaqueToken =
            tokenService.createPersonalizedOpaqueAccessToken(
                    bwayne_user, "confidential-opaque", Set.of("OPENID"), Duration.ofMillis(1));

    Thread.sleep(5);

    IntrospectionResponse introspectionResponse =
            given()
                    .header(
                            "Authorization",
                            "Basic "
                                    + Base64.getEncoder()
                                    .encodeToString(
                                            "confidential-opaque:demo".getBytes(StandardCharsets.UTF_8)))
                    .contentType(ContentType.URLENC)
                    .formParam("token", opaqueToken.getValue())
                    .when()
                    .post(ENDPOINT)
                    .then()
                    .log()
                    .ifValidationFails()
                    .statusCode(200)
                    .contentType(ContentType.JSON)
                    .body(not(empty()))
                    .extract()
                    .as(IntrospectionResponse.class);
    assertThat(introspectionResponse).isNotNull();
    assertThat(introspectionResponse.isActive()).isEqualTo(false);
    assertThat(introspectionResponse.getSub()).isNull();
  }

  @Test
  void introspectionWithMissingToken() {

    OpaqueToken opaqueToken =
            tokenService.createPersonalizedOpaqueAccessToken(
                    bwayne_user, "confidential-opaque", Set.of("OPENID"), Duration.ofMillis(1));

    IntrospectionResponse introspectionResponse =
            given()
                    .header(
                            "Authorization",
                            "Basic "
                                    + Base64.getEncoder()
                                    .encodeToString(
                                            "confidential-opaque:demo".getBytes(StandardCharsets.UTF_8)))
                    .contentType(ContentType.URLENC)
                    .formParam("dummy", "1234")
                    .when()
                    .post(ENDPOINT)
                    .then()
                    .log()
                    .ifValidationFails()
                    .statusCode(200)
                    .contentType(ContentType.JSON)
                    .body(not(empty()))
                    .extract()
                    .as(IntrospectionResponse.class);
    assertThat(introspectionResponse).isNotNull();
    assertThat(introspectionResponse.isActive()).isEqualTo(false);
    assertThat(introspectionResponse.getSub()).isNull();
  }
}
