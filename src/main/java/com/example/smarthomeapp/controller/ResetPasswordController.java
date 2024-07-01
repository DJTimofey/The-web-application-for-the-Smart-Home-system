package com.example.smarthomeapp.controller;

import com.example.smarthomeapp.model.User;
import com.example.smarthomeapp.repository.UserRepository;
import com.example.smarthomeapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;

@Controller
public class ResetPasswordController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/reset-password")
    public String showResetPasswordForm(Model model, @RequestParam("token") String token) {
        User user = userService.findUserByResetToken(token);

        // Check if the user and the token are valid
        if (user != null && user.getTokenExpiry().isAfter(LocalDateTime.now())) {
            // Valid token, show the reset password form
            model.addAttribute("token", token);
            return "reset-password";
        } else {
            // Invalid or expired token, redirect to an error page or login page
            return "redirect:/login?resetError";
        }
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam("token") String token,
                                @RequestParam("password") String password,
                                @RequestParam("confirmPassword") String confirmPassword) {
        // Find the user by the reset token
        User user = userService.findUserByResetToken(token);

        // Check if the user and the token are still valid
        if (user != null && user.getTokenExpiry().isAfter(LocalDateTime.now())) {
            // Reset the password and save the user
            user.setPassword(passwordEncoder.encode(password));
            user.setResetToken(null);
            user.setTokenExpiry(null);
            userRepository.save(user);

            // Redirect to login page with a reset success message
            return "redirect:/login?resetSuccess";
        } else {
            // Invalid or expired token, redirect to an error page or login page
            return "redirect:/login?resetError";
        }
    }
}