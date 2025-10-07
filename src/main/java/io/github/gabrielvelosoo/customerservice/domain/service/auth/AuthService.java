package io.github.gabrielvelosoo.customerservice.domain.service.auth;

import io.github.gabrielvelosoo.customerservice.domain.entity.Customer;
import io.github.gabrielvelosoo.customerservice.domain.service.customer.CustomerService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Logger logger = LogManager.getLogger(AuthService.class);

    private final CustomerService customerService;

    public Customer getLoggedCustomer() {
        String keycloakUserId = getLoggedKeycloakUserId();
        logger.debug("Getting logged customer with keycloakUserId: '{}'", keycloakUserId);
        return customerService.findByKeycloakUserId(keycloakUserId);
    }

    private String getLoggedKeycloakUserId() {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return jwt.getSubject();
    }
}
