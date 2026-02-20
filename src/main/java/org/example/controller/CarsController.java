package org.example.controller;

import org.example.model.Car;
import org.example.repository.CarRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.awt.print.Book;
import java.util.List;

@Controller
public class CarsController {


    private final CarRepository carRepository;

    public CarsController(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    @GetMapping("/car")
    public String findAllCars(Model model) {
        List<Car> cars = this.carRepository.findAll();
        model.addAttribute("cars", cars);
        model.addAttribute("car", new Car());
        return "formCar";
    }

    @PostMapping("/car")
    public Car addOneCar(@RequestBody Car car) {
        return this.carRepository.save(car);
    }
}
