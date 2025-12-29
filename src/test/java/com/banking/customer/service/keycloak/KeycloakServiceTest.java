package com.banking.customer.service.keycloak;

import com.banking.customer.service.dto.TokenRequest;
import com.banking.customer.service.dto.TokenResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@QuarkusTest
class KeycloakServiceTest {

    @Inject
    KeycloakService keycloakService;

    @InjectMock
    @RestClient
    KeycloakClient keycloakClient;

    @Inject
    ObjectMapper objectMapper;

    private String validTokenJson;
    private TokenResponse expectedTokenResponse;

    @BeforeEach
    void setUp() throws Exception {
        expectedTokenResponse = new TokenResponse();
        expectedTokenResponse.setAccessToken("test-access-token");
        expectedTokenResponse.setRefreshToken("test-refresh-token");
        expectedTokenResponse.setExpiresIn(300);
        expectedTokenResponse.setRefreshExpiresIn(1800);
        expectedTokenResponse.setTokenType("Bearer");

        validTokenJson = objectMapper.writeValueAsString(expectedTokenResponse);
    }

    @Test
    void testGetToken_WithValidCredentials_ReturnsToken() throws Exception {
        TokenRequest request = new TokenRequest();
        request.setUsername("testuser");
        request.setPassword("testpass");
        request.setGrantType("password");
        request.setClientId("customer-service-cli");

        when(keycloakClient.getToken(
            anyString(),
            anyString(),
            anyString(),
            anyString()
        )).thenReturn(validTokenJson);

        TokenResponse result = keycloakService.getToken(request);

        assertNotNull(result);
        assertEquals("test-access-token", result.getAccessToken());
        assertEquals("test-refresh-token", result.getRefreshToken());
        assertEquals(300, result.getExpiresIn());
        assertEquals("Bearer", result.getTokenType());
    }

    @Test
    void testGetToken_WithDefaultClientId_UsesDefault() throws Exception {
        TokenRequest request = new TokenRequest();
        request.setUsername("testuser");
        request.setPassword("testpass");
        request.setGrantType("password");
        request.setClientId(null);

        when(keycloakClient.getToken(
            "password",
            "customer-service-cli",
            "testuser",
            "testpass"
        )).thenReturn(validTokenJson);

        TokenResponse result = keycloakService.getToken(request);

        assertNotNull(result);
        assertEquals("test-access-token", result.getAccessToken());
    }

    @Test
    void testGetToken_WithCustomClientId_UsesCustom() throws Exception {
        TokenRequest request = new TokenRequest();
        request.setUsername("testuser");
        request.setPassword("testpass");
        request.setGrantType("password");
        request.setClientId("custom-client");

        when(keycloakClient.getToken(
            "password",
            "custom-client",
            "testuser",
            "testpass"
        )).thenReturn(validTokenJson);

        TokenResponse result = keycloakService.getToken(request);

        assertNotNull(result);
    }

    @Test
    void testGetToken_WhenKeycloakFails_ThrowsException() {
        TokenRequest request = new TokenRequest();
        request.setUsername("testuser");
        request.setPassword("wrongpass");

        when(keycloakClient.getToken(anyString(), anyString(), anyString(), anyString()))
            .thenThrow(new RuntimeException("Keycloak authentication failed"));

        assertThrows(RuntimeException.class, () -> keycloakService.getToken(request));
    }

    @Test
    void testGetToken_WithInvalidJson_ThrowsException() {
        TokenRequest request = new TokenRequest();
        request.setUsername("testuser");
        request.setPassword("testpass");

        when(keycloakClient.getToken(anyString(), anyString(), anyString(), anyString()))
            .thenReturn("invalid-json");

        assertThrows(RuntimeException.class, () -> keycloakService.getToken(request));
    }

    @Test
    void testRefreshToken_WithValidToken_ReturnsNewToken() throws Exception {
        String refreshToken = "valid-refresh-token";

        when(keycloakClient.refreshToken(
            "refresh_token",
            "customer-service-cli",
            refreshToken
        )).thenReturn(validTokenJson);

        TokenResponse result = keycloakService.refreshToken(refreshToken);

        assertNotNull(result);
        assertEquals("test-access-token", result.getAccessToken());
        assertEquals("test-refresh-token", result.getRefreshToken());
    }

    @Test
    void testRefreshToken_WhenKeycloakFails_ThrowsException() {
        String refreshToken = "invalid-refresh-token";

        when(keycloakClient.refreshToken(anyString(), anyString(), anyString()))
            .thenThrow(new RuntimeException("Invalid refresh token"));

        assertThrows(RuntimeException.class, () -> keycloakService.refreshToken(refreshToken));
    }

    @Test
    void testRefreshToken_WithInvalidJson_ThrowsException() {
        String refreshToken = "valid-refresh-token";

        when(keycloakClient.refreshToken(anyString(), anyString(), anyString()))
            .thenReturn("invalid-json");

        assertThrows(RuntimeException.class, () -> keycloakService.refreshToken(refreshToken));
    }
}
