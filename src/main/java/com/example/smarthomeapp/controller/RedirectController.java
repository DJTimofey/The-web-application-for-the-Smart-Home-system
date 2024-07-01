package com.example.smarthomeapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RedirectController {

    @GetMapping("/qr-redirect")
    public String qrRedirectPage() {
        // You may want to add any necessary logic for this page
        return "qr-redirect"; // The name of your HTML file for the QR code redirect page
    }
}