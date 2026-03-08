package org.example.controller;

import org.example.model.Car;
import org.example.repository.CarRepository;
import org.example.service.CarService;
import org.example.session.UserSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.example.model.User;

import java.util.ArrayList;
import java.util.List;

@Controller
public class CarsController {

    private final CarService carService;
    private final CarRepository carRepository;
    private final UserSession userSession;

    public CarsController(CarService carService, CarRepository carRepository, UserSession userSession) {
        this.carService = carService;
        this.carRepository = carRepository;
        this.userSession = userSession;
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

    @GetMapping("/buy/{id}")
    public String buyCar(@PathVariable Long id) {
        if (!userSession.isLoggedIn()) {
            return "redirect:/login";
        }
        User buyer = userSession.getUser();
        carService.acquerirVoiture(id, buyer);
        return "redirect:/cars";
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