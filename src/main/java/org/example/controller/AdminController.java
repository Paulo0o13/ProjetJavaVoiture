package org.example.controller;

import org.example.model.Car;
import org.example.repository.CarRepository;
import org.example.service.CarService;
import org.example.session.UserSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final CarService carService;
    private final UserSession userSession;
    private final CarRepository carRepository;

    public AdminController(CarService carService, UserSession userSession, CarRepository carRepository) {
        this.carService = carService;
        this.userSession = userSession;
        this.carRepository = carRepository;
    }

    // Méthode utilitaire pour vérifier si l'utilisateur est bien ADMIN
    private boolean isAdmin() {
        return userSession.isLoggedIn() && "ADMIN".equals(userSession.getUser().getRole());
    }

    // 1. Afficher le formulaire d'ajout
    @GetMapping("/add-car")
    public String showAddForm(Model model) {
        if (!isAdmin()){
            return "redirect:/login";
        }

        model.addAttribute("car", new Car());
        model.addAttribute("loggedUser", userSession.getUser());
        return "formCarAdmin";
    }


    @PostMapping("/add-car")
    public String addCarToCatalogue(@ModelAttribute Car car) {
        if (!isAdmin()){
            return "redirect:/login";
        }
        car.setOwner(null);
        car.setDisponible(true);
        carService.saveCar(car);

        return "redirect:/catalogue";
    }

    @GetMapping("/delete/{id}")
    public String deleteCar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (!isAdmin()){
            return "redirect:/login";
        }

        try {
            carService.deleteCar(id);
            redirectAttributes.addFlashAttribute("success", "Annonce supprimée.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/catalogue";
    }

    // 1. Affiche le formulaire avec les infos de la voiture
    @GetMapping("/edit-car/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        if (!isAdmin()) return "redirect:/login";

        Car car = carRepository.findById(id).orElseThrow();
        model.addAttribute("car", car);
        model.addAttribute("loggedUser", userSession.getUser());
        return "editCar"; // Crée ce fichier HTML
    }

    // 2. Reçoit la voiture modifiée et l'enregistre
    @PostMapping("/save-edit")
    public String saveEdit(@ModelAttribute("car") Car car) {
        if (!isAdmin()) return "redirect:/login";

        // IMPORTANT : Puisque l'ID est présent dans l'objet 'car',
        // .save() va faire un UPDATE au lieu d'un INSERT.
        carRepository.save(car);

        return "redirect:/catalogue";
    }
}
