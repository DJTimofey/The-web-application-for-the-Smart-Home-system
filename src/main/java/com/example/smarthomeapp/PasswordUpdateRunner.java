package com.example.smarthomeapp;
import com.example.smarthomeapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class PasswordUpdateRunner implements CommandLineRunner {

    private final UserService userService;

    @Autowired
    public PasswordUpdateRunner(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void run(String... args) throws Exception {
        // Run the password update once when the application starts
        userService.updateExistingPasswords();
    }
}