package org.example.configuration;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfiguration {

    public static final String LISTENER_QUEUE = "listener.queue";

    @Bean
    public Queue listenerQueue() {
        return new Queue(LISTENER_QUEUE, false);
    }


}
