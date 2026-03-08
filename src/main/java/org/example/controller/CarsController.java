package org.example.controller;

import org.example.configuration.RabbitConfiguration;
import org.example.model.Car;
import org.example.repository.CarRepository;
import org.example.service.CarService;
import org.example.session.UserSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.example.model.User;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class CarsController {

    private final CarService carService;
    private final CarRepository carRepository;
    private final UserSession userSession;
    private final RabbitTemplate rabbitTemplate;

    public CarsController(CarService carService, CarRepository carRepository, UserSession userSession, RabbitTemplate rabbitTemplate) {
        this.carService = carService;
        this.carRepository = carRepository;
        this.userSession = userSession;
        this.rabbitTemplate = rabbitTemplate;
    }








    @GetMapping("/car")
    public String showForm(Model model) {
        if (!userSession.isLoggedIn()){
            return "redirect:/login";
        }

        model.addAttribute("loggedUser", userSession.getUser());
        model.addAttribute("car", new Car());
        return "formCar";
    }

    @PostMapping("/car")
    public String addOneCar(@ModelAttribute("car") Car car) {
        if (!userSession.isLoggedIn()) {
            return "redirect:/login";
        }

        car.setOwner(userSession.getUser());
        this.carService.saveCar(car);
        return "redirect:/cars";
    }

    @GetMapping("/cars")
    public String findAllCars(Model model) {
        if (!userSession.isLoggedIn()) {
            return "redirect:/login";
        }
        model.addAttribute("loggedUser", userSession.getUser());
        List<Car> cars = this.carRepository.findByOwnerPseudo(userSession.getUser().getPseudo());
        model.addAttribute("cars", cars);
        return "listCar";
    }


    @GetMapping("/catalogue")
    public String showCatalogue(Model model) {
        List<Car> cars = carRepository.findByDisponibleTrue();
        model.addAttribute("cars", cars);
        if (!userSession.isLoggedIn()) {
            return "redirect:/login";
        }
        model.addAttribute("loggedUser", userSession.getUser());
        return "catalogue";
    }

    /*@GetMapping("/buy/{id}")
    public String buyCar(@PathVariable Long id) {
        if (!userSession.isLoggedIn()) {
            return "redirect:/login";
        }
        User buyer = userSession.getUser();
        carService.acquerirVoiture(id, buyer);
        return "redirect:/cars";
    }*/

    @GetMapping("/buy/{id}")
    public String buyCar(@PathVariable Long id) {
        if (!userSession.isLoggedIn()) {
            return "redirect:/login";
        }

        // 1. Récupérer les infos du véhicule et de l'acheteur
        Car car = carRepository.findById(id).orElseThrow();
        User buyer = userSession.getUser();

        // 2. Préparer la requête pour la banque (Step 2 du PDF) [cite: 31]
        Map<String, Object> creditRequest = new HashMap<>();
        creditRequest.put("userId", buyer.getPseudo());    // Identifiant [cite: 32]
        creditRequest.put("amount", car.getPrix());        // Montant [cite: 33]
        creditRequest.put("operationType", car.getTypeOffre()); // Type [cite: 34]
        creditRequest.put("carId", id); // Indispensable pour la réponse plus tard

        // 3. Envoyer le message vers la file de requête [cite: 47, 67]
        rabbitTemplate.convertAndSend(RabbitConfiguration.REQUEST_QUEUE, creditRequest);

        // 4. Rediriger vers une page d'attente ou le catalogue
        // L'utilisateur ne verra pas sa voiture tout de suite, il faut que la banque réponde !
        return "redirect:/catalogue?pending=true";
    }

    @GetMapping("/catalogue/data")
    public String getCatalogueData(Model model) {
        // Si l'utilisateur n'est pas connecté, on ne lui envoie rien
        if (!userSession.isLoggedIn()) {
            model.addAttribute("cars", new ArrayList<Car>()); // Liste vide
            return "catalogue :: #car-list";
        }

        // Sinon, on envoie la liste normale
        List<Car> cars = carRepository.findByOwnerPseudo(null);
        model.addAttribute("cars", cars);
        model.addAttribute("loggedUser", userSession.getUser());

        return "catalogue :: #car-list";
    }


    @PostMapping("/cars/release/{id}")
    public String releaseCar(@PathVariable Long id) {
        // Sécurité : Vérifier si l'utilisateur est connecté
        if (!userSession.isLoggedIn()) {
            return "redirect:/login";
        }

        // On pourrait aussi vérifier si la voiture appartient bien à l'utilisateur
        // avant de le laisser la supprimer/rendre (Sécurité sup)

        carService.releaseVoiture(id);

        return "redirect:/cars";
    }

}