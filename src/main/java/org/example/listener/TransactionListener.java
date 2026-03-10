package org.example.listener;

import org.example.configuration.RabbitConfiguration;
import org.example.model.User;
import org.example.repository.UserRepository;
import org.example.service.CarService;
import org.example.service.CarServiceImpl;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class TransactionListener {

    private final CarService carService;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public TransactionListener(CarService carService, UserRepository userRepository, SimpMessagingTemplate messagingTemplate) {
        this.carService = carService;
        this.userRepository = userRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @RabbitListener(queues = RabbitConfiguration.RESPONSE_QUEUE)
    public void onBankResponse(Map<String, Object> response) {

        Boolean approved = (Boolean) response.get("approved");
        String message = (String) response.get("message");

        String transactionIdString = (String) response.get("transactionId");

        if (transactionIdString == null) {
            System.err.println("Erreur : Aucun transactionId reçu de la banque.");
            return;
        }

        UUID transactionId = UUID.fromString(transactionIdString);

        CarServiceImpl.TransactionInfo info = this.carService.recupererEtNettoyerTransaction(transactionId);

        if (info == null) {
            System.err.println("Transaction ignorée : L'ID " + transactionId + " est introuvable. Elle a peut-être déjà été traitée.");
            return;
        }

        if (Boolean.TRUE.equals(approved)) {
            User buyer = this.userRepository.findById(info.pseudoAcheteur()).orElse(null);

            if (buyer != null) {
                this.carService.acquerirVoiture(info.carId(), buyer);
                System.out.println("Succès : Banque OK. Voiture " + info.carId() + " attribuée à " + buyer.getPseudo());
            } else {
                System.err.println("Erreur : L'utilisateur " + info.pseudoAcheteur() + " n'existe plus en base de données.");
            }
        } else {
            System.out.println("Refus : La banque a rejeté la transaction pour l'utilisateur " + info.pseudoAcheteur() + ". Motif : " + message);
        }
        Map<String, Object> wsMessage = new HashMap<>();
        wsMessage.put("approved", approved);

        messagingTemplate.convertAndSend("/topicAchat/resultatMess", wsMessage);
    }
}
