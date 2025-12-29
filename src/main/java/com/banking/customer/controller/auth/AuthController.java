package com.banking.customer.controller.auth;

import com.banking.customer.service.dto.TokenRequest;
import com.banking.customer.service.dto.TokenResponse;
import com.banking.customer.service.keycloak.KeycloakService;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

import java.util.HashMap;
import java.util.Map;

@Path("/api/v1/auth")
@Produces(MediaType.APPLICATION_JSON)
@PermitAll
public class AuthController {

    private static final Logger LOG = Logger.getLogger(AuthController.class);

    @Inject
    KeycloakService keycloakService;

    @POST
    @Path("/token")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getToken(@Valid TokenRequest tokenRequest) {
        LOG.infof("Token request received for user: %s", tokenRequest.getUsername());
        try {
            TokenResponse tokenResponse = keycloakService.getToken(tokenRequest);
            return Response.ok(tokenResponse).build();
        } catch (Exception e) {
            LOG.errorf(e, "Error generating token for user: %s", tokenRequest.getUsername());
            Map<String, String> error = new HashMap<>();
            error.put("error", "authentication_failed");
            error.put("message", "Invalid credentials or authentication error");
            error.put("details", e.getMessage());
            return Response.status(Response.Status.UNAUTHORIZED).entity(error).build();
        }
    }

    @POST
    @Path("/refresh")
    public Response refreshToken(@NotBlank(message = "Refresh token is required") @QueryParam("refreshToken") String refreshToken) {
        LOG.info("Token refresh request received");
        try {
            TokenResponse tokenResponse = keycloakService.refreshToken(refreshToken);
            return Response.ok(tokenResponse).build();
        } catch (Exception e) {
            LOG.errorf(e, "Error refreshing token");
            Map<String, String> error = new HashMap<>();
            error.put("error", "refresh_failed");
            error.put("message", "Invalid refresh token or refresh error");
            error.put("details", e.getMessage());
            return Response.status(Response.Status.UNAUTHORIZED).entity(error).build();
        }
    }

    @GET
    @Path("/health")
    @Produces(MediaType.TEXT_PLAIN)
    public String health() {
        return "Auth endpoint is running";
    }
}
