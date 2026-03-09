package org.example.controller;

import org.example.model.Car;
import org.example.model.enums.RoleType;
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

    private boolean isAdmin() {
        return this.userSession.isLoggedIn() && this.userSession.getUser().getRole() != null && this.userSession.getUser().getRole().equals(RoleType.ADMIN);
    }

    @GetMapping("/add-car")
    public String showAddForm(Model model) {
        if (this.isAdmin()) {
            return "redirect:/login";
        }

        model.addAttribute("car", new Car());
        model.addAttribute("loggedUser", userSession.getUser());
        return "formCarAdmin";
    }


    @PostMapping("/add-car")
    public String addCarToCatalogue(@ModelAttribute Car car) {
        if (this.isAdmin()) {
            return "redirect:/login";
        }
        car.setOwner(null);
        car.setDisponible(true);
        carService.saveCar(car);

        return "redirect:/catalogue";
    }

    @GetMapping("/delete/{id}")
    public String deleteCar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (this.isAdmin()) {
            return "redirect:/login";
        }

        try {
            this.carService.deleteCar(id);
            redirectAttributes.addFlashAttribute("success", "Annonce supprimée.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/catalogue";
    }

    @GetMapping("/edit-car/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        if (this.isAdmin()) {
            return "redirect:/login";
        }

        Car car = this.carRepository.findById(id).orElseThrow();
        model.addAttribute("car", car);
        model.addAttribute("loggedUser", this.userSession.getUser());
        return "editCar"; // Crée ce fichier HTML
    }

    @PostMapping("/save-edit")
    public String saveEdit(@ModelAttribute("car") Car car) {
        if (this.isAdmin()) {
            return "redirect:/login";
        }

        this.carRepository.save(car);

        return "redirect:/catalogue";
    }
}
