package com.example.smarthomeapp;

import com.example.smarthomeapp.model.User;
import com.example.smarthomeapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class TokenUpdateRunner implements CommandLineRunner {

    private final UserService userService;

    @Autowired
    public TokenUpdateRunner(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void run(String... args) throws Exception {
        // Run the password update once when the application starts
        userService.updateExistingPasswords();

        // Generate tokens if they are not present or if they are expired (without sending reset emails)
        List<User> usersWithoutValidTokens = userService.findUsersWithoutValidTokens();

        for (User user : usersWithoutValidTokens) {
            userService.sendPasswordResetEmail(user, false);
        }
    }
}