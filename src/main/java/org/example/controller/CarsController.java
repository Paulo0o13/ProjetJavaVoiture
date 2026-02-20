package org.example.controller;

import org.example.model.Car;
import org.example.repository.CarRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.awt.print.Book;

@RestController
public class CarsController {


    private final CarRepository carRepository;

    public CarsController(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    @GetMapping("/car")
    public Iterable<Car> findAllCars() {
        return this.carRepository.findAll();
    }

    @PostMapping("/car")
    public Car addOneCar(@RequestBody Car car) {
        return this.carRepository.save(car);
    }
}
