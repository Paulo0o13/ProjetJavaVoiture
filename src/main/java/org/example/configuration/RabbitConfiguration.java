package org.example.configuration;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfiguration {

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

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
