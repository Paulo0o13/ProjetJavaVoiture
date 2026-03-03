package org.example.controller;

import org.example.model.Car;
import org.example.repository.CarRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class CarsController {

    private final CarRepository carRepository;

    public CarsController(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    // 1. Affiche UNIQUEMENT le formulaire sur /car
    @GetMapping("/car")
    public String showForm(Model model) {
        model.addAttribute("car", new Car());
        return "formCar";
    }

    // 2. Sauvegarde et redirige vers la liste des voitures (/cars)
    @PostMapping("/car")
    public String addOneCar(@ModelAttribute("car") Car car) {
        this.carRepository.save(car);
        // On redirige vers la nouvelle URL
        return "redirect:/cars";
    }

    // 3. Affiche UNIQUEMENT la liste sur /cars
    @GetMapping("/cars")
    public String findAllCars(Model model) {
        List<Car> cars = this.carRepository.findAll();
        model.addAttribute("cars", cars);
        return "listCar";
    }
}