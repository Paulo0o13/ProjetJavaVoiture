package org.example;

import org.example.model.User;
import org.example.model.enums.RoleType;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class Main implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Override
    public void run(String... args) {

        if (!this.userRepository.existsById("admin")) {
            User admin = new User();
            admin.setPseudo("admin");
            admin.setPassword("admin123");
            admin.setRole(RoleType.ADMIN);

            this.userRepository.save(admin);
        }

    }


}