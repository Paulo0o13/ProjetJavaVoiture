package org.example.controller;

import jakarta.servlet.http.HttpSession;
import org.example.model.Car;
import org.example.model.User;
import org.example.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.awt.print.Book;

@Controller
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/login")
    public String showLogin() {
        return "formLog";
    }

    @PostMapping("/login")
    public String login(@RequestParam String pseudo, @RequestParam String password, HttpSession session) {
        User user = userRepository.findById(pseudo).orElse(null);

        if (user != null && user.getPassword().equals(password)) {
            session.setAttribute("loggedUser", user);
            return "redirect:/cars";
        }
        return "redirect:/login?error";
    }

    @GetMapping("/register")
    public String showRegister(Model model) {
        model.addAttribute("user", new User());
        return "formRegister";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute User user) {
        userRepository.save(user);
        return "redirect:/login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
