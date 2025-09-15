package io.github.gabrielvelosoo.customerservice.infrastructure.security.service;

import io.github.gabrielvelosoo.customerservice.domain.service.auth.IdentityProvider;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KeycloakUserService implements IdentityProvider {

    private static final Logger logger = LogManager.getLogger(KeycloakUserService.class);

    private final Keycloak keycloak;

    @Value("${keycloak.realm}")
    private String realm;

    @Override
    public String createUser(String email, String password, String name, String lastName) {
        try {
            List<UserRepresentation> existing = keycloak.realm(realm).users().search(email, true);
            if(!existing.isEmpty()) {
                logger.error("User '{}' already exists", email);
                throw new RuntimeException("User already exists in Keycloak: " + email);
            }
            UserRepresentation user = new UserRepresentation();
            user.setUsername(email);
            user.setEmail(email);
            user.setFirstName(name);
            user.setLastName(lastName);
            user.setEnabled(true);
            Response response = keycloak.realm(realm).users().create(user);
            if(response.getStatus() != 201) {
                logger.error("User '{}' was not created", email);
                throw new RuntimeException("Error creating user in Keycloak: " + response.getStatus());
            }
            String userId = CreatedResponseUtil.getCreatedId(response);
            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setTemporary(false);
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(password);
            keycloak.realm(realm).users().get(userId).resetPassword(credential);
            assignRole(userId, "USER");
            return userId;
        } catch(Exception e) {
            logger.error("Error creating user", e);
            throw new RuntimeException("Failed to create user in Keycloak: " + e.getMessage(), e);
        }
    }

    @Override
    public void assignRole(String userId, String role) {
        try {
            RoleRepresentation userRole = keycloak.realm(realm).roles().get(role).toRepresentation();
            keycloak.realm(realm).users().get(userId).roles().realmLevel().add(List.of(userRole));
        } catch(Exception e) {
            logger.error("Error assigning role", e);
            throw new RuntimeException("Failed to assign role in Keycloak: " + e.getMessage(), e);
        }
    }

    @Override
    public void editUser(String userId, String name, String lastName) {
        try {
            UserResource userResource = keycloak.realm(realm)
                    .users()
                    .get(userId);
            UserRepresentation user = userResource.toRepresentation();
            user.setFirstName(name);
            user.setLastName(lastName);
            userResource.update(user);
        } catch(Exception e) {
            logger.error("Error editing user", e);
            throw new RuntimeException("Failed to update user in Keycloak: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteUser(String userId) {
        try {
            logger.info("Deleting customer with id='{}'", userId);
            keycloak.realm(realm)
                    .users()
                    .get(userId)
                    .remove();
        } catch(Exception e) {
            logger.error("Error deleting user", e);
            throw new RuntimeException("Failed to delete user in Keycloak: " + e.getMessage(), e);
        }
    }
}
