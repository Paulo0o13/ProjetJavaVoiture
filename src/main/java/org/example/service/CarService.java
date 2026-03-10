package org.example.service;

import org.example.model.Car;
import org.example.model.User;

import java.util.List;
import java.util.UUID;


public interface CarService {

    List<Car> getAllCars();

    void saveCar(Car car);

    void acquerirVoiture(Long carId, User user);

    void deleteCar(Long carId);

    void releaseVoiture(Long carId);

    UUID memoriserTransaction(Long carId, String pseudo);

    CarServiceImpl.TransactionInfo recupererEtNettoyerTransaction(UUID transactionId);


}