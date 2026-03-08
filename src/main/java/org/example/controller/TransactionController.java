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
    @PostMapping("/buy-vehicle") // Remplace ton /sendMessage
    public ResponseEntity<?> buyVehicle(@RequestParam("carId") Long carId,
                                        @RequestParam("price") double price,
                                        @RequestParam("type") String type) {

        // On prépare la requête selon le Step 2 du PDF [cite: 31]
        Map<String, Object> request = Map.of(
                "userId", "PseudoDeLutlisateur", // Récupère ton utilisateur connecté
                "amount", price,                 // Le prix du véhicule
                "operationType", type,           // "rent" ou "buy"
                "carId", carId                   // Pour s'en souvenir au retour
        );

        // On envoie vers la file de REQUÊTE [cite: 67]
        rabbitTemplate.convertAndSend(RabbitConfiguration.REQUEST_QUEUE, request);

        return ResponseEntity.ok(Map.of("status", "Verification en cours..."));
    }






}
