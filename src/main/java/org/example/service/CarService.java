package org.example.service;

import org.example.model.Car;
import java.util.List;

public interface CarService {
    List<Car> getAllCars();
    void saveCar(Car car);
}