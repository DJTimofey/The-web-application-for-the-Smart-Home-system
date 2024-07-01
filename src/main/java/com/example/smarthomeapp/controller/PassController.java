package com.example.smarthomeapp.controller;

import com.example.smarthomeapp.model.Order;
import com.example.smarthomeapp.repository.OrderRepository;
import com.example.smarthomeapp.service.QRCodeService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
public class PassController {

    private final QRCodeService qrCodeService;
    private final OrderRepository orderRepository;

    public PassController(QRCodeService qrCodeService, OrderRepository orderRepository) {
        this.qrCodeService = qrCodeService;
        this.orderRepository = orderRepository;
    }
    @GetMapping("/order-passes")
    public String orderPassesPage(Model model) {
        // You may want to add any necessary logic for this page
        return "order-passes"; // The name of your HTML file for the order-passes page
    }

    @PostMapping("/generate-pass")
    public String generatePass(@ModelAttribute("order") Order order, Model model) {
        try {
            // Convert String to Date
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
            Date expirationDate = formatter.parse(order.getExpirationDateStr());

            // Ваша логика для генерации QR-кода и отправки на почту
            qrCodeService.generateAndSendQRCode(order.getFriendEmail(), order.getFriendName(), expirationDate);

            // Сохранение данных в базе данных
            order.setExpirationDate(expirationDate);
            orderRepository.save(order);

            // Можете добавить атрибуты в модель, если хотите отобразить какую-то информацию на странице
            model.addAttribute("message", "Pass generated and sent successfully!");

            // Возвращаем имя представления или URL для перенаправления
            return "redirect:/order-passes"; // Redirect to the order-passes page

        } catch (ParseException e) {
            // Обработка ошибок, если необходимо
            e.printStackTrace();
            model.addAttribute("error", "Error generating and sending pass.");
            return "redirect:/order-passes"; // Redirect to the order-passes page
        }
    }
}