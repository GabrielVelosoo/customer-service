package io.github.gabrielvelosoo.customerservice.infrastructure.security.service;

import io.github.gabrielvelosoo.customerservice.infrastructure.exception.KeycloakException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.*;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KeycloakUserServiceTest {

    @Mock
    Keycloak keycloak;

    @Mock
    RealmResource realmResource;

    @Mock
    UsersResource usersResource;

    @Mock
    UserResource userResource;

    @Mock
    RolesResource rolesResource;

    @Mock
    RoleResource roleResource;

    @Mock
    RoleMappingResource roleMappingResource;

    @Mock
    RoleScopeResource roleScopeResource;

    @InjectMocks
    KeycloakUserService keycloakUserService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(keycloakUserService, "realm", "test-realm");

        lenient().when(keycloak.realm(anyString())).thenReturn(realmResource);
        lenient().when(realmResource.users()).thenReturn(usersResource);
        lenient().when(usersResource.get(anyString())).thenReturn(userResource);

        lenient().when(realmResource.roles()).thenReturn(rolesResource);
        lenient().when(rolesResource.get(anyString())).thenReturn(roleResource);
        lenient().when(roleResource.toRepresentation()).thenReturn(new RoleRepresentation("USER", null, false));

        lenient().when(userResource.roles()).thenReturn(roleMappingResource);
        lenient().when(roleMappingResource.realmLevel()).thenReturn(roleScopeResource);
        lenient().doNothing().when(roleScopeResource).add(anyList());
        lenient().doNothing().when(userResource).resetPassword(any(CredentialRepresentation.class));
    }

    @Test
    void shouldCreateUserSuccessfully() {
        when(usersResource.search("test@example.com", true)).thenReturn(List.of());
        Response response = Response
                .status(Response.Status.CREATED)
                .header("Location", "http://localhost/users/123")
                .build();
        when(usersResource.create(any(UserRepresentation.class))).thenReturn(response);

        String userId = keycloakUserService.createUser("test@example.com", "test123", "John", "Doe");

        assertEquals("123", userId);
        verify(usersResource).create(any(UserRepresentation.class));
        verify(userResource).resetPassword(any(CredentialRepresentation.class));
        verify(roleScopeResource).add(anyList());
    }

    @Test
    void shouldReturnTrueWhenUserExists() {
        when(usersResource.search("test@example.com", true)).thenReturn(List.of(new UserRepresentation()));

        boolean exists = Boolean.TRUE.equals(ReflectionTestUtils.invokeMethod(keycloakUserService, "userExists", "test@example.com"));

        assertTrue(exists);
        verify(usersResource).search("test@example.com", true);
    }

    @Test
    void shouldReturnFalseWhenUserDoesNotExist() {
        when(usersResource.search("test@example.com", true)).thenReturn(List.of());

        boolean exists = Boolean.TRUE.equals(ReflectionTestUtils.invokeMethod(keycloakUserService, "userExists", "test@example.com"));

        assertFalse(exists);
        verify(usersResource).search("test@example.com", true);
    }

    @Test
    void shouldThrowExceptionWhenUserAlreadyExistsOnCreate() {
        when(usersResource.search("test@example.com", true)).thenReturn(List.of(new UserRepresentation()));

        KeycloakException e = assertThrows(
                KeycloakException.class,
                () -> keycloakUserService.createUser("test@example.com", "test123", "John", "Doe")
        );

        assertEquals("Failed to create user in Keycloak: User already exists in Keycloak: test@example.com", e.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenKeycloakReturnsErrorOnCreate() {
        when(usersResource.search("test@example.com", true)).thenReturn(List.of());
        Response response = mock(Response.class);
        when(usersResource.create(any(UserRepresentation.class))).thenReturn(response);
        when(response.getStatus()).thenReturn(500);

        KeycloakException e = assertThrows(
                KeycloakException.class,
                () -> keycloakUserService.createUser("test@example.com", "test123", "John", "Doe")
        );

        assertEquals("Failed to create user in Keycloak: Error creating user in Keycloak: 500", e.getMessage());
    }

    @Test
    void shouldAssignRoleToUser() {
        RoleRepresentation roleRepresentation = new RoleRepresentation();
        roleRepresentation.setName("USER");

        when(roleResource.toRepresentation()).thenReturn(roleRepresentation);

        keycloakUserService.assignRole("123", "USER");

        verify(rolesResource).get("USER");
        verify(roleScopeResource).add(List.of(roleRepresentation));
    }

    @Test
    void shouldThrowExceptionWhenAssignRoleFails() {
        when(rolesResource.get("USER")).thenThrow(new RuntimeException("Role not found"));

        KeycloakException e = assertThrows(
                KeycloakException.class,
                () -> keycloakUserService.assignRole("123", "USER")
        );

        assertEquals("Failed to assign role in Keycloak", e.getMessage());
        assertInstanceOf(RuntimeException.class, e.getCause());
    }

    @Test
    void shouldEditUser() {
        when(usersResource.get("123")).thenReturn(userResource);
        UserRepresentation userRep = new UserRepresentation();
        when(userResource.toRepresentation()).thenReturn(userRep);
        doNothing().when(userResource).update(any(UserRepresentation.class));

        keycloakUserService.editUser("123", "NewName", "NewLast");

        assertEquals("NewName", userRep.getFirstName());
        assertEquals("NewLast", userRep.getLastName());
        verify(userResource).update(userRep);
    }

    @Test
    void shouldThrowExceptionWhenEditUserFails() {
        when(usersResource.get("123")).thenReturn(userResource);
        when(userResource.toRepresentation()).thenThrow(new RuntimeException("Cannot fetch user"));

        KeycloakException e = assertThrows(
                KeycloakException.class,
                () -> keycloakUserService.editUser("123", "Name", "Last")
        );

        assertEquals("Failed to update user in Keycloak: Cannot fetch user", e.getMessage());
        assertInstanceOf(RuntimeException.class, e.getCause());
    }

    @Test
    void shouldDeleteUser() {
        when(usersResource.get("123")).thenReturn(userResource);
        doNothing().when(userResource).remove();

        keycloakUserService.deleteUser("123");

        verify(userResource).remove();
    }

    @Test
    void shouldThrowKeycloakExceptionOnDeleteUserError() {
        RuntimeException rootCause = new RuntimeException("Boom");
        when(usersResource.get("123")).thenThrow(rootCause);

        KeycloakException e = assertThrows(KeycloakException.class,
                () -> keycloakUserService.deleteUser("123"));

        assertEquals("Failed to delete user in Keycloak: 123", e.getMessage());
        assertSame(rootCause, e.getCause());
    }
}