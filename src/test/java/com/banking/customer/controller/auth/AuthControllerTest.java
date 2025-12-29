package com.banking.customer.controller.auth;

import com.banking.customer.service.dto.TokenRequest;
import com.banking.customer.service.dto.TokenResponse;
import com.banking.customer.service.keycloak.KeycloakService;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@QuarkusTest
class AuthControllerTest {

    @InjectMock
    KeycloakService keycloakService;

    @Test
    void testGetToken_WithValidCredentials_ReturnsToken() {
        TokenRequest request = new TokenRequest("testuser", "testpass");

        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setAccessToken("test-access-token");
        tokenResponse.setRefreshToken("test-refresh-token");
        tokenResponse.setExpiresIn(300);
        tokenResponse.setTokenType("Bearer");

        when(keycloakService.getToken(any(TokenRequest.class)))
            .thenReturn(tokenResponse);

        given()
            .contentType(ContentType.JSON)
            .body(request)
            .when()
            .post("/api/v1/auth/token")
            .then()
            .statusCode(200)
            .body("access_token", equalTo("test-access-token"))
            .body("refresh_token", equalTo("test-refresh-token"))
            .body("expires_in", equalTo(300))
            .body("token_type", equalTo("Bearer"));
    }

    @Test
    void testGetToken_WithInvalidCredentials_Returns401() {
        TokenRequest request = new TokenRequest("testuser", "wrongpass");

        when(keycloakService.getToken(any(TokenRequest.class)))
            .thenThrow(new RuntimeException("Invalid credentials"));

        given()
            .contentType(ContentType.JSON)
            .body(request)
            .when()
            .post("/api/v1/auth/token")
            .then()
            .statusCode(401)
            .body("error", equalTo("authentication_failed"))
            .body("message", equalTo("Invalid credentials or authentication error"));
    }

    @Test
    void testGetToken_WithMissingUsername_Returns400() {
        TokenRequest request = new TokenRequest();
        request.setPassword("testpass");

        given()
            .contentType(ContentType.JSON)
            .body(request)
            .when()
            .post("/api/v1/auth/token")
            .then()
            .statusCode(400);
    }

    @Test
    void testGetToken_WithMissingPassword_Returns400() {
        TokenRequest request = new TokenRequest();
        request.setUsername("testuser");

        given()
            .contentType(ContentType.JSON)
            .body(request)
            .when()
            .post("/api/v1/auth/token")
            .then()
            .statusCode(400);
    }

    @Test
    void testRefreshToken_WithValidToken_ReturnsNewToken() {
        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setAccessToken("new-access-token");
        tokenResponse.setRefreshToken("new-refresh-token");
        tokenResponse.setExpiresIn(300);
        tokenResponse.setTokenType("Bearer");

        when(keycloakService.refreshToken(anyString()))
            .thenReturn(tokenResponse);

        given()
            .queryParam("refreshToken", "valid-refresh-token")
            .when()
            .post("/api/v1/auth/refresh")
            .then()
            .statusCode(200)
            .body("access_token", equalTo("new-access-token"))
            .body("refresh_token", equalTo("new-refresh-token"))
            .body("expires_in", equalTo(300));
    }

    @Test
    void testRefreshToken_WithInvalidToken_Returns401() {
        when(keycloakService.refreshToken(anyString()))
            .thenThrow(new RuntimeException("Invalid refresh token"));

        given()
            .queryParam("refreshToken", "invalid-refresh-token")
            .when()
            .post("/api/v1/auth/refresh")
            .then()
            .statusCode(401)
            .body("error", equalTo("refresh_failed"))
            .body("message", equalTo("Invalid refresh token or refresh error"));
    }

    @Test
    void testRefreshToken_WithMissingToken_Returns400() {
        // Empty string fails @NotBlank validation, returns 400 from ConstraintViolationExceptionMapper
        given()
            .queryParam("refreshToken", "")
            .when()
            .post("/api/v1/auth/refresh")
            .then()
            .statusCode(400);
    }

    @Test
    void testHealth_ReturnsOk() {
        given()
            .when()
            .get("/api/v1/auth/health")
            .then()
            .statusCode(200)
            .contentType("text/plain")
            .body(equalTo("Auth endpoint is running"));
    }
}
