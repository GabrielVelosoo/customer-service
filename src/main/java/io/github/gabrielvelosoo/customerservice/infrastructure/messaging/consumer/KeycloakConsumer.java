package io.github.gabrielvelosoo.customerservice.infrastructure.messaging.consumer;

import io.github.gabrielvelosoo.customerservice.application.dto.event.CustomerCreatedEvent;
import io.github.gabrielvelosoo.customerservice.application.dto.event.CustomerDeletedEvent;
import io.github.gabrielvelosoo.customerservice.application.dto.event.CustomerUpdatedEvent;
import io.github.gabrielvelosoo.customerservice.domain.entity.Customer;
import io.github.gabrielvelosoo.customerservice.domain.service.auth.IdentityProvider;
import io.github.gabrielvelosoo.customerservice.domain.service.customer.CustomerService;
import io.github.gabrielvelosoo.customerservice.infrastructure.messaging.config.RabbitConfig;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KeycloakConsumer {

    private static final Logger logger = LogManager.getLogger(KeycloakConsumer.class);

    private final IdentityProvider identityProvider;
    private final CustomerService customerService;

    @RabbitListener(queues = RabbitConfig.CREATE_QUEUE, containerFactory = "rabbitListenerContainerFactory")
    public void handleCustomerCreated(CustomerCreatedEvent event) {
        try {
            Customer customer = customerService.findById(event.customerId());
            if(customer.getKeycloakUserId() != null) {
                logger.warn("Customer '{}' already has keycloakUserId, skipping creation", event.customerId());
                return;
            }
            String keycloakUserId = identityProvider.createUser(
                    event.email(),
                    event.name(),
                    event.lastName()
            );
            customer.setKeycloakUserId(keycloakUserId);
            customerService.save(customer);
            logger.info("Keycloak user created and linked for customerId '{}'", event.customerId());
        } catch(Exception e) {
            logger.error("Failed to create Keycloak user for customerId '{}'", event.customerId(), e);
            throw e;
        }
    }

    @RabbitListener(queues = RabbitConfig.UPDATE_QUEUE, containerFactory = "rabbitListenerContainerFactory")
    public void handleCustomerUpdated(CustomerUpdatedEvent event) {
        try {
            Customer customer = customerService.findById(event.customerId());
            if(customer.getKeycloakUserId() == null) {
                logger.warn("Customer '{}' has no KeycloakUserId yet, skipping update", event.customerId());
                return;
            }
            identityProvider.editUser(customer.getKeycloakUserId(), event.name(), event.lastName());
            logger.info("Keycloak user updated for customerId '{}'", event.customerId());
        } catch(Exception e) {
            logger.error("Failed to updated Keycloak user for customerId '{}'", event.customerId(), e);
            throw e;
        }
    }

    @RabbitListener(queues = RabbitConfig.DELETE_QUEUE, containerFactory = "rabbitListenerContainerFactory")
    public void handleCustomerDeleted(CustomerDeletedEvent event) {
        try {
            Customer customer = customerService.findById(event.customerId());
            if(customer.getKeycloakUserId() == null) {
                logger.warn("Customer '{}' has no KeycloakUserId, skipping deletion in Keycloak", event.customerId());
                return;
            }
            identityProvider.deleteUser(customer.getKeycloakUserId());
            logger.info("Keycloak user deleted for customerId '{}'", event.customerId());
        } catch(Exception e) {
            logger.error("Failed to deleted Keycloak user for customerId '{}'", event.customerId(), e);
            throw e;
        }
    }
}
