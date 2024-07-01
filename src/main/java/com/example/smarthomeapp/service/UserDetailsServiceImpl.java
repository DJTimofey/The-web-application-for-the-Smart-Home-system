package com.example.smarthomeapp.service;

import com.example.smarthomeapp.model.User;
import com.example.smarthomeapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        // Получаем роль пользователя из базы данных или другого источника
        String role = user.getRole(); // Предположим, что у пользователя есть поле "role"

        // Создаем объект GrantedAuthority для роли пользователя
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);

        // Создаем список ролей для данного пользователя и добавляем роль пользователя
        List<GrantedAuthority> authorities = Collections.singletonList(authority);

        // Возвращаем объект UserDetails, учитывая роль пользователя
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(), // Убедитесь, что это зашифрованный пароль из базы данных
                authorities
        );
    }



}