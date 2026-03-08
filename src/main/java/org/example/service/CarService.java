package org.example.service;

import org.example.model.Car;
import org.example.model.User;
import org.springframework.web.context.annotation.SessionScope;

import java.util.List;


public interface CarService {
    List<Car> getAllCars();
    void saveCar(Car car);
    void acquerirVoiture(Long carId, User user);
    void deleteCar(Long carId);
    void releaseVoiture(Long carId);

}