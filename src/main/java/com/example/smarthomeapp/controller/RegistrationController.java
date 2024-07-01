package com.example.smarthomeapp.controller;

import com.example.smarthomeapp.model.User;
import com.example.smarthomeapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class RegistrationController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @RequestMapping("/registration")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "registration";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam("username") String username,
                               @RequestParam("password") String password,
                               @RequestParam("confirmPassword") String confirmPassword,
                               @RequestParam("email") String email,
                               @RequestParam("phone") String phone,
                               @RequestParam("address") String address,
                               Model model) {

        // Check if a user with the same username already exists
        if (userRepository.findByUsername(username) != null) {
            // Handle duplicate username (you can add a model attribute or error message)
            return "redirect:/registration?error";
        }

        // Check if the password and confirmPassword match
        if (!password.equals(confirmPassword)) {
            // Handle password mismatch (you can add a model attribute or error message)
            return "redirect:/registration?passwordMismatch";
        }

        // Create a new User object
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password)); // Hash the password
        user.setEmail(email);
        user.setPhone(phone); // Set phone
        user.setAddress(address); // Set address
        user.setRole("USER"); // Set role as "USER" for new registration

        try {
            userRepository.save(user);
            // You should log this using an appropriate logger
            System.out.println("User registered successfully: " + user.getUsername());
        } catch (Exception e) {
            // You should log this using an appropriate logger
            System.err.println("Error during user registration: " + e.getMessage());
            return "redirect:/registration?error";
        }

        // Redirect to the login page after successful registration
        return "login";
    }
}