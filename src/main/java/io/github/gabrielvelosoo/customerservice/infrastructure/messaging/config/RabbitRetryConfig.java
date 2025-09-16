package io.github.gabrielvelosoo.customerservice.infrastructure.messaging.config;

import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitRetryConfig {

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setAdviceChain(
                RetryInterceptorBuilder.stateless()
                        .maxAttempts(5)
                        .backOffOptions(5000, 2.0, 20000)
                        .recoverer(new RejectAndDontRequeueRecoverer())
                        .build()
        );
        return factory;
    }
}
