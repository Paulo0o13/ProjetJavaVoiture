package org.example.service;

import org.example.model.Car;
import org.example.model.User;
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
        return carRepository.findAll();
    }

    @Override
    public void saveCar(Car car) {
        carRepository.save(car);
    }

    @Override
    public void acquerirVoiture(Long id, User user){
        Car car = carRepository.findById(id).get();
        car.setOwner(user);
        car.setDisponible(false);
        carRepository.save(car);

    }

    @Override
    public void deleteCar(Long id) {
        // 1. On va chercher la voiture en base de données pour avoir son état RÉEL
        Car car = carRepository.findById(id).orElse(null);

        if (car != null) {
            // 2. On vérifie si elle est encore disponible (donc n'appartient à personne)
            if (car.isDisponible()) {
                carRepository.deleteById(id);
            } else {
                // 3. Optionnel : On peut lever une erreur ou juste ne rien faire
                // Car on ne veut pas supprimer la voiture d'un client !
                throw new RuntimeException("Impossible de supprimer : cette voiture a déjà été acquise !");
            }
        }
    }

    // Dans CarServiceImpl.java
    @Override
    public void releaseVoiture(Long carId) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new RuntimeException("Voiture non trouvée"));

        if ("LOCATION".equals(car.getTypeOffre())) {
            // Remise en stock
            car.setOwner(null);
            car.setDisponible(true);
            carRepository.save(car);
        } else {
            // Achat : Suppression définitive
            carRepository.delete(car);
        }
    }
}