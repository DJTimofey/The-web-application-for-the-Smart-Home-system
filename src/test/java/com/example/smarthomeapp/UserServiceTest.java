package com.example.smarthomeapp;

import com.example.smarthomeapp.model.User;
import com.example.smarthomeapp.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    public void testGeneratePasswordResetToken() {
        User user = new User();
        user.setEmail("test@example.com"); // Set a valid email address
        user.setUsername("user"); // Set a valid username
        user.setPassword("3242422"); // Set a valid password

        // Set the reset token and token expiry
        user.setResetToken("testToken");
        user.setTokenExpiry(LocalDateTime.now().plusHours(1));

        // Save the user
        userService.saveUser(user);

        // Log statements for verification
        System.out.println("Generated reset token: " + user.getResetToken());
        System.out.println("Token expiry: " + user.getTokenExpiry());
    }
}