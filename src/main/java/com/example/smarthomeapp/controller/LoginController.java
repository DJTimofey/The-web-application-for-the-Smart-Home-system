package com.example.smarthomeapp.controller;

import com.example.smarthomeapp.model.User;
import com.example.smarthomeapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
public class LoginController {

    @Autowired
    private UserService userService; // Add this line to inject UserService
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("registerAction", "/processRegistration");
        return "login"; // Name of your HTML file with the login form
    }


    @GetMapping("/forgot-password")
    public String showForgotPasswordForm(Model model) {
        return "forgot-password";
    }

    @PostMapping("/login")
    public String loginSuccess(@RequestParam String username, @RequestParam String password, @RequestParam String role, RedirectAttributes redirectAttributes) {
        User user = userService.findUserByEmail(username);
        Long userId = Long.parseLong(username);
        if (user != null && !userService.isUserBlocked(userId)) {
            // Получаем роль пользователя из базы данных
            String userRole = userService.getUserRole(username);
            System.out.println("User Role: " + userRole);

            // Проверяем, совпадает ли роль с введенной ролью
            if (userRole != null && userRole.equals(role)) {
                // Проверка аутентификации пользователя
                if (passwordEncoder.matches(password, user.getPassword())) {
                    if ("USER".equals(role)) {
                        System.out.println("USER " + username + " has logged in");
                        return "redirect:/dashboard";
                    } else if ("ADMIN".equals(role)) {
                        System.out.println("ADMIN " + username + " has logged in");
                        return "redirect:/admin";
                    } else if ("OPERATOR".equals(role)) {
                        System.out.println("OPERATOR " + username + " has logged in");
                        return "redirect:/operator";
                    }
                }
            }
        }

        // Если пользователь заблокирован или аутентификация не удалась, перенаправляем на страницу входа с сообщением об ошибке
        redirectAttributes.addFlashAttribute("error", "true");
        redirectAttributes.addFlashAttribute("error", "Неправильный логин или пароль");
        return "/login";
    }

}