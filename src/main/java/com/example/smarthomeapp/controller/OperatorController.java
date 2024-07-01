package com.example.smarthomeapp.controller;

import com.example.smarthomeapp.model.Operator;
import com.example.smarthomeapp.repository.OperatorRepository;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
public class OperatorController {

    @Autowired
    private OperatorRepository operatorRepository;
    @Autowired
    private UserDetailsService userDetailsService;
    @GetMapping("/operator")
    public String operatorDashboard(Model model, Principal principal) {
        String operatorName = principal.getName();
        Operator operator = operatorRepository.findByUsername(operatorName);
        model.addAttribute("operator", operator);
        return "operator";
    }
    @PostMapping("/operator-edit")
    public String editOperatorProfile(@RequestParam("username") String newUsername,
                                      @RequestParam(value = "password", required = false) String password,
                                      @RequestParam("email") String email,
                                      @RequestParam("phone") String phone,
                                      @RequestParam("address") String address,
                                      Principal principal) {

        // Update operator's information
        Operator operator = operatorRepository.findByUsername(principal.getName());
        operator.setUsername(newUsername);
        if (password != null && !password.isEmpty()) {
            if (!isPasswordBCryptEncoded(password)) {
                password = BCrypt.hashpw(password, BCrypt.gensalt());
            }
            operator.setPassword(password);
        }
        operator.setEmail(email);
        operator.setPhone(phone);
        operator.setAddress(address);

        // Save the updated operator
        operatorRepository.save(operator);

        // Load UserDetails using UserDetailsService
        UserDetails userDetails = userDetailsService.loadUserByUsername(newUsername);

        // Create a new authentication object with updated UserDetails
        Authentication newAuthentication = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());

        // Set the new authentication object in the security context
        SecurityContextHolder.getContext().setAuthentication(newAuthentication);

        return "redirect:/operator";
    }
    private boolean isPasswordBCryptEncoded(String password) {
        // BCrypt passwords start with "$2a$"
        return password.startsWith("$2a$");
    }
}