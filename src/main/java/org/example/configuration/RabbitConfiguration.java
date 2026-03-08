package org.example.configuration;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfiguration {

    // Noms des files imposés par la consigne
    public static final String REQUEST_QUEUE = "credit.request.queue";
    public static final String RESPONSE_QUEUE = "credit.response.queue";

    @Bean
    public Queue requestQueue() {
        return new Queue(REQUEST_QUEUE, false);
    }

    @Bean
    public Queue responseQueue() {
        return new Queue(RESPONSE_QUEUE, false);
    }
}
