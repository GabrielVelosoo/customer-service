package io.github.gabrielvelosoo.customerservice.infrastructure.messaging.consumer;

import io.github.gabrielvelosoo.customerservice.application.dto.event.CustomerCreatedEvent;
import io.github.gabrielvelosoo.customerservice.application.dto.event.CustomerDeletedEvent;
import io.github.gabrielvelosoo.customerservice.application.dto.event.CustomerUpdatedEvent;
import io.github.gabrielvelosoo.customerservice.domain.repository.CustomerRepository;
import io.github.gabrielvelosoo.customerservice.domain.service.auth.IdentityProvider;
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
    private final CustomerRepository customerRepository;

    @RabbitListener(queues = RabbitConfig.CREATE_QUEUE, containerFactory = "rabbitListenerContainerFactory")
    public void handleCustomerCreated(CustomerCreatedEvent event) {
        try {
            if(customerRepository.existsById(event.customerId())) {
                logger.warn("Customer '{}' already exists, skipping creation", event.customerId());
                return;
            }
            identityProvider.createUser(event.email(), event.password(), event.name(), event.lastName());
            logger.info("Keycloak user created for customerId '{}'", event.customerId());
        } catch(Exception e) {
            logger.error("Failed to create Keycloak user for customerId '{}'", event.customerId(), e);
            throw e;
        }
    }

    @RabbitListener(queues = RabbitConfig.UPDATE_QUEUE, containerFactory = "rabbitListenerContainerFactory")
    public void handleCustomerUpdated(CustomerUpdatedEvent event) {
        try {
            identityProvider.editUser(event.keycloakUserId(), event.name(), event.lastName());
            logger.info("Keycloak user updated for customerId '{}'", event.customerId());
        } catch(Exception e) {
            logger.error("Failed to updated Keycloak user for customerId '{}'", event.customerId(), e);
            throw e;
        }
    }

    @RabbitListener(queues = RabbitConfig.DELETE_QUEUE, containerFactory = "rabbitListenerContainerFactory")
    public void handleCustomerDeleted(CustomerDeletedEvent event) {
        try {
            identityProvider.deleteUser(event.keycloakUserId());
            logger.info("Keycloak user deleted for customerId '{}'", event.customerId());
        } catch(Exception e) {
            logger.error("Failed to deleted Keycloak user for customerId '{}'", event.customerId(), e);
            throw e;
        }
    }
}
