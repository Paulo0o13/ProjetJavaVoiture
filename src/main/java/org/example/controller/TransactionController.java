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

    @PostMapping("/buy-vehicle")
    public ResponseEntity<?> buyVehicle(@RequestParam("carId") Long carId,
                                        @RequestParam("price") double price,
                                        @RequestParam("type") String type) {

        Map<String, Object> request = Map.of(
                "userId", "PseudoDeLutlisateur",
                "amount", price,
                "operationType", type,
                "carId", carId
        );

        this.rabbitTemplate.convertAndSend(RabbitConfiguration.REQUEST_QUEUE, request);

        return ResponseEntity.ok(Map.of("status", "Verification en cours..."));
    }


}
