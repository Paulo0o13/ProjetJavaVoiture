package org.example.listener;

import org.example.configuration.RabbitConfiguration;
import org.example.controller.TransactionController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TransactionListener {
    private static final Logger log = LoggerFactory.getLogger(TransactionListener.class);

    // Étape 4 : On écoute la file de RÉPONSE (credit.response.queue) [cite: 68]
    @RabbitListener(queues = RabbitConfiguration.RESPONSE_QUEUE)
    public void onBankResponse(Map<String, Object> response) {

        // Extraction des données provenant du service Bank [cite: 35, 36, 37]
        String userId = (String) response.get("userId");
        boolean approved = (boolean) response.get("approved");

        if (approved) {
            log.info("Félicitations {} ! La banque a approuvé l'opération.", userId);
            // Ici, tu appelleras ton carService pour valider l'achat [cite: 52]
        } else {
            log.warn("Dommage {}... La banque a refusé l'opération.", userId);
            // Ici, tu pourras gérer l'annulation [cite: 53]
        }
    }
}
