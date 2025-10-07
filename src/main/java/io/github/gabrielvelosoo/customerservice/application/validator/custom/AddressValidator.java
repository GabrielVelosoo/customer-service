package io.github.gabrielvelosoo.customerservice.application.validator.custom;

import io.github.gabrielvelosoo.customerservice.domain.entity.Address;
import io.github.gabrielvelosoo.customerservice.domain.entity.Customer;
import io.github.gabrielvelosoo.customerservice.domain.service.auth.AuthService;
import io.github.gabrielvelosoo.customerservice.infrastructure.exception.BusinessRuleException;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AddressValidator {

    private static final Logger logger = LogManager.getLogger(AddressValidator.class);

    private final AuthService authService;

    public void validateOnUpdateAndDelete(Address address) {
        logger.debug("Validating address update/delete for address id: '{}'", address.getId());
        if(!addressBelongsToLoggedCustomer(address)) {
            Long loggedId = authService.getLoggedCustomer() != null ? authService.getLoggedCustomer().getId() : null;
            Long ownerId = address.getCustomer() != null ? address.getCustomer().getId() : null;
            logger.warn("Unauthorized access: Logged user '{}' tried to manage address '{}' belonging to customer '{}'",
                    loggedId, address.getId(), ownerId);
            throw new BusinessRuleException("You don’t have permission to manage this address");
        }
    }

    private boolean addressBelongsToLoggedCustomer(Address address) {
        Customer customer = authService.getLoggedCustomer();
        return customer != null
                && address.getCustomer() != null
                && customer.getId().equals(address.getCustomer().getId());
    }
}
