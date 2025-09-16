package io.github.gabrielvelosoo.customerservice;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@EnableRabbit
public class CustomerServiceApplication {

    public static void main(String[] args) {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(CustomerServiceApplication.class);
        builder.bannerMode(Banner.Mode.OFF);
        builder.run(args);
        ConfigurableApplicationContext context = builder.context();
        ConfigurableEnvironment environment = context.getEnvironment();
        String applicationName = environment.getProperty("spring.application.name");
        System.out.println("Microservice name: " + applicationName);
    }
}
