package org.example.service;

import org.example.model.Car;
import org.example.model.User;
import org.example.model.enums.OfferType;
import org.example.repository.CarRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;

    public CarServiceImpl(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    @Override
    public List<Car> getAllCars() {
        return this.carRepository.findAll();
    }

    @Override
    public void saveCar(Car car) {
        this.carRepository.save(car);
    }

    @Override
    public void acquerirVoiture(Long id, User user) {
        Car car = this.carRepository.findById(id).orElseThrow(() -> new RuntimeException("Aucune voiture trouvée avec l'id : " + id));
        car.setOwner(user);
        car.setDisponible(false);
        this.carRepository.save(car);

    }

    @Override
    public void deleteCar(Long id) {

        this.carRepository.findById(id).ifPresent(car -> {
            if (car.isDisponible()) {
                this.carRepository.deleteById(id);
            } else {
                throw new RuntimeException("Impossible de supprimer : cette voiture a déjà été acquise !");
            }
        });
    }

    @Override
    public void releaseVoiture(Long carId) {
        Car car = this.carRepository.findById(carId)
                .orElseThrow(() -> new RuntimeException("Voiture non trouvée"));

        if (car.getTypeOffre() == OfferType.LOCATION) {
            car.setOwner(null);
            car.setDisponible(true);
            this.carRepository.save(car);
        } else {
            this.carRepository.delete(car);
        }
    }
}