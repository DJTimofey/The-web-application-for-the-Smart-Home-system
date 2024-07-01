package com.example.smarthomeapp.controller;

import com.example.smarthomeapp.model.Admin;
import com.example.smarthomeapp.model.Device;
import com.example.smarthomeapp.model.User;
import com.example.smarthomeapp.repository.AdminRepository;
import com.example.smarthomeapp.service.DeviceService;
import com.example.smarthomeapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import java.security.Principal;
import java.util.List;

@Controller
public class AdminController {

    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/admin")
    public String adminDashboard(Model model, Principal principal) {
        String adminName = principal.getName();
        Admin admin = adminRepository.findByUsername(adminName);
        model.addAttribute("admin", admin != null ? admin : new Admin()); // Добавляем администратора в модель, даже если он null
        return "admin";
    }
    @PostMapping("/admin/add-user")
    public String addUser(@RequestParam("username") String username,
                          @RequestParam("email") String email,
                          @RequestParam("password") String password,
                          @RequestParam("role") String role,
                          @RequestParam("phone") String phone,
                          @RequestParam("address") String address) {
        // Создание нового пользователя
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password)); // Хеширование пароля
        user.setRole(role);
        user.setPhone(phone);
        user.setAddress(address);

        // Сохранение пользователя в базе данных
        userService.save(user);

        return "redirect:/admin/users";
    }
    @PostMapping("/admin/delete-user")
    public String deleteUser(@RequestParam("userId") Long userId) {
        // Удаление пользователя по его ID
        userService.delete(userId);
        return "redirect:/admin/users";
    }
    @GetMapping("/admin/users")
    public String getUsers(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        List<Device> devices = deviceService.getAllDevices();
        model.addAttribute("devices", devices);
        return "users"; // Assuming you have an HTML file named "admin-users.html" to display the users
    }
    @PostMapping("/admin/block-user")
    public String blockUser(@RequestParam("userId") Long userId) {
        userService.blockUser(userId);
        return "redirect:/admin/users";
    }

    @PostMapping("/admin/unblock-user")
    public String unblockUser(@RequestParam("userId") Long userId) {
        userService.unblockUser(userId);
        return "redirect:/admin/users";
    }

    @GetMapping("/admin/users/devices")
    public String getDevices(Model model) {
        List<Device> devices = deviceService.getAllDevices();
        model.addAttribute("devices", devices);
        return "users"; // Assuming you have an HTML file named "users.html" to display the devices
    }
    @PostMapping("/admin-edit")
    public String editAdminProfile(@RequestParam("username") String newUsername,
                                   @RequestParam(value = "password", required = false) String password,
                                   @RequestParam("email") String email,
                                   @RequestParam("phone") String phone,
                                   @RequestParam("address") String address,
                                   Principal principal) {

        // Update admin's information
        Admin admin = adminRepository.findByUsername(principal.getName());
        admin.setUsername(newUsername);
        if (password != null && !password.isEmpty()) {
            if (!isPasswordBCryptEncoded(password)) {
                password = BCrypt.hashpw(password, BCrypt.gensalt());
            }
            admin.setPassword(password);
        }
        admin.setEmail(email);
        admin.setPhone(phone);
        admin.setAddress(address);

        // Save the updated admin
        adminRepository.save(admin);

        // Load UserDetails using UserDetailsService
        UserDetails userDetails = userDetailsService.loadUserByUsername(newUsername);

        // Create a new authentication object with updated UserDetails
        Authentication newAuthentication = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());

        // Set the new authentication object in the security context
        SecurityContextHolder.getContext().setAuthentication(newAuthentication);

        return "redirect:/admin";
    }
    private boolean isPasswordBCryptEncoded(String password) {
        // BCrypt passwords start with "$2a$"
        return password.startsWith("$2a$");
    }


}