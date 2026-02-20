package org.example;

import org.example.model.Car;
import org.example.repository.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class Main implements CommandLineRunner {
public static void main(String[] args) { SpringApplication.run(Main.class, args); }

    @Autowired
    private CarRepository carRepository;

    @Override
    public void run(String... args) throws Exception {
        Car car = new Car();
        car.setMarque("Peugeot");
        car.setModele("206+");
        car.setAnnee(2009);
        car.setCouleur("grise");

        carRepository.save(car);

        List<Car> allCars =  carRepository.findAll();


        System.out.println("All items in repository: "+ carRepository.findAll());


    }
}