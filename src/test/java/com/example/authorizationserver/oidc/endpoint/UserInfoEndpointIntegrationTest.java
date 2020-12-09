package com.example.authorizationserver.oidc.endpoint;

import com.example.authorizationserver.annotation.WebIntegrationTest;
import com.example.authorizationserver.oidc.endpoint.userinfo.UserInfo;
import com.example.authorizationserver.scim.model.ScimUserEntity;
import com.example.authorizationserver.scim.service.ScimService;
import com.example.authorizationserver.token.store.TokenService;
import com.example.authorizationserver.token.store.model.JsonWebToken;
import com.example.authorizationserver.token.store.model.OpaqueToken;
import com.nimbusds.jose.JOSEException;
import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.WebApplicationContext;

import java.time.Duration;
import java.util.Collections;
import java.util.Optional;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;

@WebIntegrationTest
class UserInfoEndpointIntegrationTest {

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
    void userInfoWithJwtToken() throws JOSEException {
        JsonWebToken jsonWebToken =
                tokenService.createPersonalizedJwtAccessToken(
                        bwayne_user, "confidential-demo", "nonce", Collections.singleton("OPENID"), Duration.ofMinutes(5));
        UserInfo userInfo =
                given()
                        .header("Authorization", "Bearer " + jsonWebToken.getValue())
                        .when()
                        .get("/userinfo")
                        .then()
                        .log()
                        .ifValidationFails()
                        .statusCode(200)
                        .contentType(ContentType.JSON)
                        .body(not(empty()))
                        .extract()
                        .as(UserInfo.class);
        assertThat(userInfo).isNotNull();
        assertThat(userInfo.getName()).isEqualTo("bwayne");
    }

    @Test
    void userInfoWithOpaqueToken() {

        OpaqueToken opaqueToken =
                tokenService.createPersonalizedOpaqueAccessToken(
                        bwayne_user, "confidential-demo", Collections.singleton("OPENID"), Duration.ofMinutes(5));

        UserInfo userInfo =
                given()
                        .header("Authorization", "Bearer " + opaqueToken.getValue())
                        .when()
                        .get("/userinfo")
                        .then()
                        .log()
                        .ifValidationFails()
                        .statusCode(200)
                        .contentType(ContentType.JSON)
                        .body(not(empty()))
                        .extract()
                        .as(UserInfo.class);
        assertThat(userInfo).isNotNull();
        assertThat(userInfo.getName()).isEqualTo("bwayne");
    }

    @Test
    void userInfoWithInvalidToken() {

        UserInfo userInfo =
                given()
                        .header("Authorization", "Bearer 12345")
                        .when()
                        .get("/userinfo")
                        .then()
                        .log()
                        .ifValidationFails()
                        .statusCode(401)
                        .contentType(ContentType.JSON)
                        .body(not(empty()))
                        .extract()
                        .as(UserInfo.class);
        assertThat(userInfo).isNotNull();
        assertThat(userInfo.getError()).isEqualTo("invalid_token");
        assertThat(userInfo.getError_description()).isEqualTo("Access Token is invalid");
    }

    @Test
    void userInfoWithMissingToken() {

        UserInfo userInfo =
                given()
                        .when()
                        .get("/userinfo")
                        .then()
                        .log()
                        .ifValidationFails()
                        .statusCode(401)
                        .contentType(ContentType.JSON)
                        .body(not(empty()))
                        .extract()
                        .as(UserInfo.class);
        assertThat(userInfo).isNotNull();
        assertThat(userInfo.getError()).isEqualTo("invalid_token");
        assertThat(userInfo.getError_description()).isEqualTo("Access Token is required");
    }
}
