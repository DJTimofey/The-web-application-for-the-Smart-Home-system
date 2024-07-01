package com.example.smarthomeapp.repository;

import com.example.smarthomeapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);

    User findByResetToken(String resetToken);


    User findByEmail(String email);

    List<User> findByResetTokenIsNullOrTokenExpiryBefore(LocalDateTime now);

    User findByRole(String role);
}