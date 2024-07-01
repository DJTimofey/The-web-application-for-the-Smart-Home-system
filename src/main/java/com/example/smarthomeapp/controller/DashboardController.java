package com.example.smarthomeapp.controller;

import com.example.smarthomeapp.model.User;
import com.example.smarthomeapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
public class DashboardController {

    @Autowired
    private UserRepository userRepository; // Предположим, что у вас есть UserRepository для работы с пользователями
    @Autowired
    private UserDetailsService userDetailsService;
    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        // Получаем имя пользователя из аутентификации
        String username = principal.getName();

        // Получаем данные пользователя из базы данных
        User user = userRepository.findByUsername(username);

        // Передаем данные пользователя в модель для отображения на странице
        model.addAttribute("user", user);

        // Возвращаем имя вашего HTML-файла с дашбордом
        return "dashboard";
    }
    @PostMapping("/edit-profile")
    public String editProfile(@RequestParam("username") String newUsername,
                              @RequestParam("password") String password,
                              @RequestParam("email") String email,
                              @RequestParam("phone") String phone,
                              @RequestParam("address") String address,
                              Principal principal) {

        // Update user's information
        User user = userRepository.findByUsername(principal.getName());

        // Check if the password needs to be encrypted
        if (!isPasswordBCryptEncoded(password)) {
            password = BCrypt.hashpw(password, BCrypt.gensalt());
        }

        // Update user's information
        user.setUsername(newUsername);
        user.setPassword(password);
        user.setEmail(email);
        user.setPhone(phone);
        user.setAddress(address);

        // Save the updated user
        userRepository.save(user);

        // Load UserDetails using UserDetailsService
        UserDetails userDetails = userDetailsService.loadUserByUsername(newUsername);

        // Create a new authentication object with updated UserDetails
        Authentication newAuthentication = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());

        // Set the new authentication object in the security context
        SecurityContextHolder.getContext().setAuthentication(newAuthentication);

        return "redirect:/dashboard";
    }

    // Метод для проверки, зашифрован ли пароль
    private boolean isPasswordBCryptEncoded(String password) {
        // BCrypt passwords start with "$2a$"
        return password.startsWith("$2a$");
    }
}