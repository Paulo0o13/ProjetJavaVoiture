package org.example.controller;


import org.example.model.User;
import org.example.repository.UserRepository;
import org.example.session.UserSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
public class UserController {

    private final UserRepository userRepository;
    private final UserSession userSession;

    public UserController(UserRepository userRepository, UserSession userSession) {
        this.userRepository = userRepository;
        this.userSession = userSession;
    }


    @GetMapping("/login")
    public String showLogin() {
        return "formLog";
    }

    @PostMapping("/login")
    public String login(@RequestParam String pseudo, @RequestParam String password) {
        User user = userRepository.findById(pseudo).orElse(null);

        if (user != null && user.getPassword().equals(password)) {
            userSession.setUser(user);

            if("ADMIN".equals(user.getRole())) {
                return "redirect:/catalogue";
            }else{
                return "redirect:/cars";
            }

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
        if (userRepository.existsById(user.getPseudo())) {
            return "redirect:/register?exists";
        }

        user.setRole("USER");
        userRepository.save(user);
        return "redirect:/login";
    }

    @GetMapping("/logout")
    public String logout() {
        userSession.setUser(null);
        return "redirect:/login";
    }
}
