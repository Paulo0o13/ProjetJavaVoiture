package org.example.controller;

import org.example.configuration.RabbitConfiguration;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
public class TransactionController {

    private final RabbitTemplate rabbitTemplate;

    public TransactionController(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;

    }

    /*
     * http://localhost:15672
     *
     * */
    @PostMapping("/sendMessage")
    public ResponseEntity<?> setName(@RequestParam("message") String message) {

        rabbitTemplate.convertAndSend(
                RabbitConfiguration.LISTENER_QUEUE,
                Map.of("msg", message)
        );
        return ResponseEntity.ok(Map.of("sent", true));
    }






}
