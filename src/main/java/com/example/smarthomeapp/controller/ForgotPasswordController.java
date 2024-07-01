package com.example.smarthomeapp.controller;

import com.example.smarthomeapp.model.User;
import com.example.smarthomeapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ForgotPasswordController {

    @Autowired
    private UserService userService;

    @Autowired
    private JavaMailSender emailSender; // Inject the JavaMailSender

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email, Model model) {
        User user = userService.findUserByEmail(email);

        if (user != null) {
            // Отправка письма с инструкциями по сбросу пароля только при наличии пользователя
            userService.sendPasswordResetEmail(user, true);

            // Set the resetEmailSent attribute
            model.addAttribute("resetEmailSent", true);
            return "forgot-password";
        } else {
            // Set the emailNotFound attribute
            model.addAttribute("emailNotFound", true);
            return "forgot-password";
        }
    }
}