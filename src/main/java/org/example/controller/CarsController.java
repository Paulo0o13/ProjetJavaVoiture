package org.example.controller;

import jakarta.servlet.http.HttpSession;
import org.example.model.Car;
import org.example.model.User;
import org.example.repository.CarRepository;
import org.example.service.CarService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class CarsController {

    private final CarService carService;
    private final CarRepository carRepository;

    public CarsController(CarService carService, CarRepository carRepository) {
        this.carService = carService;
        this.carRepository = carRepository;
    }

    @GetMapping("/car")
    public String showForm(Model model, HttpSession session) {
        if (session.getAttribute("loggedUser") == null) return "redirect:/login";

        model.addAttribute("car", new Car());
        return "formCar";
    }

    @PostMapping("/car")
    public String addOneCar(@ModelAttribute("car") Car car, HttpSession session) {
        User user = (User) session.getAttribute("loggedUser");
        if (user == null) return "redirect:/login";

        car.setOwner(user);
        this.carService.saveCar(car);
        return "redirect:/cars";
    }

    @GetMapping("/cars")
    public String findAllCars(Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedUser");
        if (user == null) return "redirect:/login";
        List<Car> cars = this.carRepository.findByOwnerPseudo(user.getPseudo());
        model.addAttribute("cars", cars);
        return "listCar";
    }
}