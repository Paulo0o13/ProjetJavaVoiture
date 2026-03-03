package org.example.service;

import org.example.model.Car;
import org.example.repository.CarRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CarsServiceImpl implements CarService {

    private final CarRepository carRepository;

    // Injection par constructeur
    public CarsServiceImpl(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    @Override
    public List<Car> getAllCars() {
        return carRepository.findAll();
    }

    @Override
    public void saveCar(Car car) {
        carRepository.save(car);
    }
}