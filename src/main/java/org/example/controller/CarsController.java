package org.example.controller;

import org.example.model.Car;
import org.example.repository.CarRepository;
import org.example.service.CarService;
import org.example.session.UserSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
}