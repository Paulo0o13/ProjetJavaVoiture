package org.example.controller;

import org.example.configuration.RabbitConfiguration;
import org.example.model.Car;
import org.example.model.User;
import org.example.repository.CarRepository;
import org.example.service.CarService;
import org.example.session.UserSession;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

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
        if (this.userSession.isLoggedOut()) {
            return "redirect:/login";
        }

        model.addAttribute("loggedUser", userSession.getUser());
        model.addAttribute("car", new Car());
        return "formCar";
    }

    @PostMapping("/car")
    public String addOneCar(@ModelAttribute("car") Car car) {
        if (this.userSession.isLoggedOut()) {
            return "redirect:/login";
        }

        car.setOwner(userSession.getUser());
        this.carService.saveCar(car);
        return "redirect:/cars";
    }

    @GetMapping("/cars")
    public String findAllCars(Model model) {
        if (this.userSession.isLoggedOut()) {
            return "redirect:/login";
        }
        model.addAttribute("loggedUser", this.userSession.getUser());
        List<Car> cars = this.carRepository.findByOwnerPseudo(this.userSession.getUser().getPseudo());
        model.addAttribute("cars", cars);
        return "listCar";
    }


    @GetMapping("/catalogue")
    public String showCatalogue(Model model) {
        List<Car> cars = this.carRepository.findByDisponibleTrue();
        model.addAttribute("cars", cars);
        if (this.userSession.isLoggedOut()) {
            return "redirect:/login";
        }
        model.addAttribute("loggedUser", userSession.getUser());
        return "catalogue";
    }


    @GetMapping("/buy/{id}")
    public String buyCar(@PathVariable Long id) {
        if (this.userSession.isLoggedOut()) {
            return "redirect:/login";
        }

        Car car = this.carRepository.findById(id).orElseThrow();
        User buyer = this.userSession.getUser();

        Map<String, Object> creditRequest = new HashMap<>();
        creditRequest.put("userId", buyer.getPseudo());    // Identifiant [cite: 32]
        creditRequest.put("amount", car.getPrix());        // Montant [cite: 33]
        creditRequest.put("operationType", car.getTypeOffre()); // Type [cite: 34]
        creditRequest.put("carId", id); // Indispensable pour la réponse plus tard

        this.rabbitTemplate.convertAndSend(RabbitConfiguration.REQUEST_QUEUE, creditRequest);

        return "redirect:/catalogue?pending=true";
    }

    @GetMapping("/catalogue/data")
    public String getCatalogueData(Model model) {
        if (this.userSession.isLoggedOut()) {
            model.addAttribute("cars", new ArrayList<Car>());
            return "catalogue :: #car-list";
        }

        List<Car> cars = this.carRepository.findByOwnerPseudo(null);
        model.addAttribute("cars", cars);
        model.addAttribute("loggedUser", userSession.getUser());

        return "catalogue :: #car-list";
    }


    @PostMapping("/cars/release/{id}")
    public String releaseCar(@PathVariable Long id) {
        if (this.userSession.isLoggedOut()) {
            return "redirect:/login";
        }

        this.carService.releaseVoiture(id);

        return "redirect:/cars";
    }

}