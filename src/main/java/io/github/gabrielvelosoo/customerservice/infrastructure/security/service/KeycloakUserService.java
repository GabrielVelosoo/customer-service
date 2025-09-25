package io.github.gabrielvelosoo.customerservice.infrastructure.security.service;

import io.github.gabrielvelosoo.customerservice.domain.service.auth.IdentityProvider;
import io.github.gabrielvelosoo.customerservice.infrastructure.exception.KeycloakException;
import io.github.gabrielvelosoo.customerservice.infrastructure.security.util.PasswordGenerator;
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
    public String createUser(String email, String name, String lastName) {
        logger.info("Creating Keycloak user with email='{}'", email);
        try {
            if(userExists(email)) {
                logger.error("User '{}' already exists", email);
                throw new KeycloakException("User already exists in Keycloak: " + email);
            }

            UserRepresentation user = new UserRepresentation();
            user.setUsername(email);
            user.setEmail(email);
            user.setFirstName(name);
            user.setLastName(lastName);
            user.setEnabled(true);

            Response response = keycloak.realm(realm).users().create(user);
            if(response.getStatus() != 201) {
                logger.error("Failed to create Keycloak user '{}' (status='{}')", email, response.getStatus());
                throw new RuntimeException("Error creating user in Keycloak: " + response.getStatus());
            }

            String userId = CreatedResponseUtil.getCreatedId(response);
            logger.info("Keycloak user '{}' created successfully with id='{}'", email, userId);

            String tempPassword = PasswordGenerator.generate(10);
            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setTemporary(true);
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(tempPassword);
            keycloak.realm(realm).users().get(userId).resetPassword(credential);
            logger.info("Password set for user '{}'", userId);

            assignRole(userId, "USER");
            return userId;
        } catch(Exception e) {
            logger.error("Error creating Keycloak user '{}'", email, e);
            throw new KeycloakException("Failed to create user in Keycloak: " + e.getMessage(), e);
        }
    }

    private boolean userExists(String email) {
        logger.debug("Checking if Keycloak user '{}' exists", email);
        List<UserRepresentation> existing = keycloak.realm(realm).users().search(email, true);
        boolean exists = !existing.isEmpty();
        logger.debug("User '{}' exists? '{}'", email, exists);
        return exists;
    }

    @Override
    public void assignRole(String userId, String role) {
        logger.info("Assigning role '{}' to Keycloak user '{}'", role, userId);
        try {
            RoleRepresentation userRole = keycloak.realm(realm).roles().get(role).toRepresentation();
            keycloak.realm(realm).users().get(userId).roles().realmLevel().add(List.of(userRole));
            logger.info("Role '{}' assigned successfully to user '{}'", role, userId);
        } catch(Exception e) {
            logger.error("Error assigning role '{}' to user '{}'", role, userId, e);
            throw new KeycloakException("Failed to assign role in Keycloak", e);
        }
    }

    @Override
    public void editUser(String userId, String name, String lastName) {
        logger.info("Editing Keycloak user '{}', setting name='{}', lastName='{}'", userId, name, lastName);
        try {
            UserResource userResource = keycloak.realm(realm)
                    .users()
                    .get(userId);
            UserRepresentation user = userResource.toRepresentation();
            user.setFirstName(name);
            user.setLastName(lastName);
            userResource.update(user);
            logger.info("Keycloak user '{}' updated successfully", userId);
        } catch(Exception e) {
            logger.error("Error editing Keycloak user '{}'", userId, e);
            throw new KeycloakException("Failed to update user in Keycloak: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteUser(String userId) {
        logger.info("Deleting Keycloak user '{}'", userId);
        try {
            keycloak.realm(realm)
                    .users()
                    .get(userId)
                    .remove();
            logger.info("Keycloak user '{}' deleted successfully", userId);
        } catch(Exception e) {
            logger.error("Error deleting Keycloak user '{}'", userId, e);
            throw new KeycloakException("Failed to delete user in Keycloak: " + userId, e);
        }
    }
}
