package com.example.smarthomeapp.service;

import com.example.smarthomeapp.model.User;
import com.example.smarthomeapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    public User findUserByEmail(String email) {
        logger.info("Поиск пользователя по почте: {}", email);
        User user = userRepository.findByEmail(email);
        if (user != null) {
            logger.info("Пользователь найден: {}", user.getUsername());
        } else {
            logger.info("Пользователь не найден по почте: {}", email);
        }
        return user;
    }
    public User findUserByUsername(String username) {
        logger.info("Поиск пользователя по имени: {}", username);
        User user = userRepository.findByUsername(username);
        if (user != null) {
            logger.info("Пользователь найден: {}", user.getUsername());
        } else {
            logger.info("Пользователь не найден по имени: {}", username);
        }
        return user;
    }
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }
    public List<User> findUsersWithoutValidTokens() {
        // Find users with either null tokens or expired tokens
        return userRepository.findByResetTokenIsNullOrTokenExpiryBefore(LocalDateTime.now());
    }
    @Autowired
    private JavaMailSender emailSender; // Inject the JavaMailSender

    public void updateExistingPasswords() {
        List<User> users = userRepository.findAll();

        for (User user : users) {
            // Assuming the password is stored as a numeric string
            String numericPassword = user.getPassword();

            // Check if the password is already in BCrypt format
            if (!isPasswordBCryptEncoded(numericPassword)) {
                // Convert numeric password to string
                String passwordAsString = String.valueOf(numericPassword);

                // Encode the password
                String encodedPassword = passwordEncoder.encode(passwordAsString);

                // Update the user's password
                user.setPassword(encodedPassword);

                // Save the updated user
                userRepository.save(user);
            }
        }
    }

    private boolean isPasswordBCryptEncoded(String password) {
        // BCrypt passwords start with "$2a$"
        return password.startsWith("$2a$");
    }
    public void sendPasswordResetEmail(User user, boolean sendEmail) {
        // Generate token for password reset
        String resetToken = UUID.randomUUID().toString();
        user.setResetToken(resetToken);

        // Set token expiry (for example, 1 hour from now)
        user.setTokenExpiry(LocalDateTime.now().plusHours(1));

        // Save the user to persist the token and its expiry in the database
        userRepository.save(user);

        // Проверка срока годности токена перед отправкой письма
        if (sendEmail && user.getTokenExpiry().isAfter(LocalDateTime.now())) {
            // Отправка письма с инструкциями по сбросу пароля
            sendEmail(user.getEmail(), "To reset your password, click the link: https://localhost:8443/reset-password?token=" + resetToken);

            // Log statements
            System.out.println("Generated reset token: " + resetToken);
            System.out.println("Token expiry: " + user.getTokenExpiry());
        } else {
            // Если срок годности токена истек, не отправляем письмо
            System.out.println("Token has expired or sendEmail is set to false. Not sending the password reset email.");
        }
    }

    public User findUserByResetToken(String resetToken) {
        return userRepository.findByResetToken(resetToken);
    }
    private void sendEmail(String to, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Password Reset");
        message.setText(body);
        emailSender.send(message);
    }
    public String getUserRole(String username) {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            String role = user.getRole();
            System.out.println("Retrieved role for " + username + ": " + role);
            return role;
        }
        return null;
    }
    // Метод для поиска пользователя по имени пользователя (логину)
    public User findByUsername(String username) {

        return userRepository.findByUsername(username);
    }
    public User getUserById(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }
    public User findByRole(String role) {
        // Implement logic to retrieve a user by their role
        return userRepository.findByRole(role); // Example implementation, adjust as needed
    }
    public void save(User user) {
        userRepository.save(user);
    }
    public void delete(Long userId) {
        userRepository.deleteById(userId);
    }


    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    public void blockUser(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            user.setBlocked(true);
            userRepository.save(user);
        }
    }

    public void unblockUser(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            user.setBlocked(false);
            userRepository.save(user);
        }
    }
    public boolean isUserBlocked(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        return user != null && user.isBlocked();
    }
    public String getUserEmail(Long userId) {
        // Здесь вы можете реализовать логику для получения адреса электронной почты пользователя по его идентификатору
        // Например, вы можете получить пользователя из базы данных и вернуть его адрес электронной почты
        User user = userRepository.findById(userId).orElse(null);
        return (user != null) ? user.getEmail() : null;
    }

}