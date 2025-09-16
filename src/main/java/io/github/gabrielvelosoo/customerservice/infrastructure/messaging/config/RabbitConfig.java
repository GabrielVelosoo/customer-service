package io.github.gabrielvelosoo.customerservice.infrastructure.messaging.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE = "CustomerExchange";

    public static final String CREATE_QUEUE = "CustomerCreateQueue";
    public static final String UPDATE_QUEUE = "CustomerUpdateQueue";
    public static final String DELETE_QUEUE = "CustomerDeleteQueue";

    public static final String CREATE_DLQ = "CustomerCreateQueue.DLQ";
    public static final String UPDATE_DLQ = "CustomerUpdateQueue.DLQ";
    public static final String DELETE_DLQ = "CustomerDeleteQueue.DLQ";

    public static final String CREATE_KEY = "customer.created";
    public static final String UPDATE_KEY = "customer.updated";
    public static final String DELETE_KEY = "customer.deleted";

    @Bean
    public DirectExchange customerExchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public Queue createQueue() {
        return QueueBuilder.durable(CREATE_QUEUE)
                .withArgument("x-dead-letter-exchange", EXCHANGE)
                .withArgument("x-dead-letter-routing-key", CREATE_DLQ)
                .build();
    }

    @Bean
    public Queue updateQueue() {
        return QueueBuilder.durable(UPDATE_QUEUE)
                .withArgument("x-dead-letter-exchange", EXCHANGE)
                .withArgument("x-dead-letter-routing-key", UPDATE_DLQ)
                .build();
    }

    @Bean
    public Queue deleteQueue() {
        return QueueBuilder.durable(DELETE_QUEUE)
                .withArgument("x-dead-letter-exchange", EXCHANGE)
                .withArgument("x-dead-letter-routing-key", DELETE_DLQ)
                .build();
    }

    @Bean
    public Queue createDLQ() {
        return QueueBuilder.durable(CREATE_DLQ).build();
    }

    @Bean
    public Queue updateDLQ() {
        return QueueBuilder.durable(UPDATE_DLQ).build();
    }

    @Bean
    public Queue deleteDLQ() {
        return QueueBuilder.durable(DELETE_DLQ).build();
    }

    @Bean
    public Binding createBinding() {
        return BindingBuilder.bind(createQueue()).to(customerExchange()).with(CREATE_KEY);
    }

    @Bean
    public Binding updateBinding() {
        return BindingBuilder.bind(updateQueue()).to(customerExchange()).with(UPDATE_KEY);
    }

    @Bean
    public Binding deleteBinding() {
        return BindingBuilder.bind(deleteQueue()).to(customerExchange()).with(DELETE_KEY);
    }

    @Bean
    public Binding createDLQBinding() {
        return BindingBuilder.bind(createDLQ()).to(customerExchange()).with(CREATE_DLQ);
    }

    @Bean
    public Binding updateDLQBinding() {
        return BindingBuilder.bind(updateDLQ()).to(customerExchange()).with(UPDATE_DLQ);
    }

    @Bean
    public Binding deleteDLQBinding() {
        return BindingBuilder.bind(deleteDLQ()).to(customerExchange()).with(DELETE_DLQ);
    }
}
