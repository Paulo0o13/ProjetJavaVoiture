package org.example.listener;

import org.example.configuration.RabbitConfiguration;
import org.example.model.User;
import org.example.repository.UserRepository;
import org.example.service.CarService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TransactionListener {

    private final CarService carService;
    private final UserRepository userRepository;

    public TransactionListener(CarService carService, UserRepository userRepository) {
        this.carService = carService;
        this.userRepository = userRepository;
    }

    @RabbitListener(queues = RabbitConfiguration.RESPONSE_QUEUE)
    public void onBankResponse(Map<String, Object> response) {
        boolean approved = (boolean) response.get("approved");
        String pseudo = (String) response.get("userId");
        Long carId = ((Number) response.get("carId")).longValue();

        if (approved) {
            User buyer = this.userRepository.findById(pseudo).orElse(null);

            if (buyer != null) {
                this.carService.acquerirVoiture(carId, buyer);
                System.out.println("Succès : Propriétaire mis à jour.");
            }
        }
    }
}
