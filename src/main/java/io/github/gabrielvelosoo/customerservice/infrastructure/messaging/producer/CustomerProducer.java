package io.github.gabrielvelosoo.customerservice.infrastructure.messaging.producer;

import io.github.gabrielvelosoo.customerservice.application.dto.event.CustomerCreatedEvent;
import io.github.gabrielvelosoo.customerservice.application.dto.event.CustomerDeletedEvent;
import io.github.gabrielvelosoo.customerservice.application.dto.event.CustomerUpdatedEvent;
import io.github.gabrielvelosoo.customerservice.infrastructure.messaging.config.RabbitConfig;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomerProducer {

    private static final Logger logger = LogManager.getLogger(CustomerProducer.class);

    private final RabbitTemplate rabbitTemplate;

    public void publishCustomerCreated(CustomerCreatedEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE,
                RabbitConfig.CREATE_KEY,
                event
        );
        logger.info("Published CustomerCreatedEvent for id '{}'", event.customerId());
    }

    public void publishCustomerUpdated(CustomerUpdatedEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE,
                RabbitConfig.UPDATE_KEY,
                event
        );
        logger.info("Published CustomerUpdatedEvent  for id '{}'", event.customerId());
    }

    public void publishCustomerDeleted(CustomerDeletedEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE,
                RabbitConfig.DELETE_KEY,
                event
        );
        logger.info("Published CustomerDeletedEvent  for id '{}'", event.customerId());
    }
}
