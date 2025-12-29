package com.banking.customer.service.keycloak;

import com.banking.customer.service.dto.TokenRequest;
import com.banking.customer.service.dto.TokenResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

@ApplicationScoped
public class KeycloakService {

    private static final Logger LOG = Logger.getLogger(KeycloakService.class);

    @Inject
    @RestClient
    KeycloakClient keycloakClient;

    @Inject
    ObjectMapper objectMapper;

    public TokenResponse getToken(TokenRequest tokenRequest) {
        LOG.infof("Requesting token from Keycloak for user: %s", tokenRequest.getUsername());

        try {
            String clientId = tokenRequest.getClientId() != null &&
                    !tokenRequest.getClientId().isEmpty() ? tokenRequest.getClientId() : "customer-service-cli";

            String jsonResponse = keycloakClient.getToken(tokenRequest.getGrantType(), clientId, tokenRequest.getUsername(), tokenRequest.getPassword());

            TokenResponse tokenResponse = objectMapper.readValue(jsonResponse, TokenResponse.class);
            LOG.info("Token obtained successfully");
            return tokenResponse;
        } catch (Exception e) {
            LOG.errorf(e, "Error obtaining token from Keycloak");
            throw new RuntimeException("Error obtaining token from Keycloak: " + e.getMessage(), e);
        }
    }

    public TokenResponse refreshToken(String refreshToken) {
        LOG.info("Refreshing token from Keycloak");
        try {
            String jsonResponse = keycloakClient.refreshToken("refresh_token", "customer-service-cli", refreshToken);

            TokenResponse tokenResponse = objectMapper.readValue(jsonResponse, TokenResponse.class);
            LOG.info("Token refreshed successfully");
            return tokenResponse;
        } catch (Exception e) {
            LOG.errorf(e, "Error refreshing token from Keycloak");
            throw new RuntimeException("Error refreshing token from Keycloak: " + e.getMessage(), e);
        }
    }
}
