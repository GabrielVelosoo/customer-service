package io.github.gabrielvelosoo.customerservice.domain.service.auth;

public interface IdentityProvider {

    String createUser(String email, String password, String name, String lastName);
    void assignRole(String userId, String role);
    void editUser(String userId, String name, String lastName);
    void deleteUser(String userId);
}
