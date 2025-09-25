package io.github.gabrielvelosoo.customerservice.infrastructure.messaging.producer;

import io.github.gabrielvelosoo.customerservice.application.dto.event.CustomerCreatedEvent;
import io.github.gabrielvelosoo.customerservice.application.dto.event.CustomerDeletedEvent;
import io.github.gabrielvelosoo.customerservice.application.dto.event.CustomerUpdatedEvent;
import io.github.gabrielvelosoo.customerservice.infrastructure.messaging.config.RabbitConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CustomerProducerTest {

    @Mock
    RabbitTemplate rabbitTemplate;

    @InjectMocks
    CustomerProducer customerProducer;

    CustomerCreatedEvent customerCreatedEvent;
    CustomerUpdatedEvent customerUpdatedEvent;
    CustomerDeletedEvent customerDeletedEvent;

    @BeforeEach
    void setUp() {
        customerCreatedEvent = new CustomerCreatedEvent(
                1L,
                "test@test.com",
                "John",
                "Doe",
                "test123"
        );

        customerUpdatedEvent = new CustomerUpdatedEvent(
                1L,
                "John",
                "Doe"
        );

        customerDeletedEvent = new CustomerDeletedEvent(
                1L
        );
    }

    @Test
    void shouldPublishCustomerCreatedEvent() {
        customerProducer.publishCustomerCreated(customerCreatedEvent);
        verify(rabbitTemplate).convertAndSend(RabbitConfig.EXCHANGE, RabbitConfig.CREATE_KEY, customerCreatedEvent);
    }

    @Test
    void shouldPublishCustomerUpdatedEvent() {
        customerProducer.publishCustomerUpdated(customerUpdatedEvent);
        verify(rabbitTemplate).convertAndSend(RabbitConfig.EXCHANGE, RabbitConfig.UPDATE_KEY, customerUpdatedEvent);
    }

    @Test
    void shouldPublishCustomerDeletedEvent() {
        customerProducer.publishCustomerDeleted(customerDeletedEvent);
        verify(rabbitTemplate).convertAndSend(RabbitConfig.EXCHANGE, RabbitConfig.DELETE_KEY, customerDeletedEvent);
    }
}