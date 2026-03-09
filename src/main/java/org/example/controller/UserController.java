package org.example.controller;


import org.example.model.User;
import org.example.model.enums.RoleType;
import org.example.repository.UserRepository;
import org.example.session.UserSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


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
        User user = this.userRepository.findById(pseudo).orElse(null);

        if (user != null && user.getPassword().equals(password)) {
            this.userSession.setUser(user);

            if (user.getRole() == RoleType.ADMIN) {
                return "redirect:/catalogue";
            } else {
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

        if (this.userRepository.existsById(user.getPseudo())) {
            return "redirect:/register?exists";
        }

        user.setRole(RoleType.USER);
        this.userRepository.save(user);
        return "redirect:/login";
    }

    @GetMapping("/logout")
    public String logout() {
        this.userSession.setUser(null);
        return "redirect:/login";
    }
}
