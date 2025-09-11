package io.github.gabrielvelosoo.customerservice.infrastructure.security.config;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakConfig {

    @Value("${keycloak.auth-server-url}")
    private String serverUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Bean
    public Keycloak keycloak(
            @Value("${keycloak.admin-username}") String adminUser,
            @Value("${keycloak.admin-password}") String adminPassword
    ) {
        return KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realm)
                .clientId(clientId)
                .username(adminUser)
                .password(adminPassword)
                .grantType(OAuth2Constants.PASSWORD)
                .build();
    }
}
