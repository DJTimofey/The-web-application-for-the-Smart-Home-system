package com.example.smarthomeapp.controller;

import com.example.smarthomeapp.model.ServiceRequest;
import com.example.smarthomeapp.model.User;
import com.example.smarthomeapp.repository.ServiceRequestRepository;
import com.example.smarthomeapp.service.ServiceRequestService;
import com.example.smarthomeapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
@Controller
public class ServiceRequestController {

    private final ServiceRequestService serviceRequestService;
    private final UserService userService;

    public ServiceRequestController(ServiceRequestService serviceRequestService, UserService userService) {
        this.serviceRequestService = serviceRequestService;
        this.userService = userService;
    }

    @GetMapping("/call-service")
    public String callService(Model model) {
        // Получаем аутентифицированного пользователя
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // Получаем имя текущего аутентифицированного пользователя
        String username = authentication.getName();

        // Получаем пользователя по его имени
        User user = userService.findByUsername(username);

        // Получаем список предыдущих заявок этого пользователя из сервисного службы
        List<ServiceRequest> serviceRequests = serviceRequestService.getAllUserRequestsByUserId(user.getId());

        // Добавляем список заявок в модель для передачи в представление
        model.addAttribute("serviceRequests", serviceRequests);

        return "call-service"; // Возвращаем имя HTML-файла для страницы вызова сервиса
    }

    @PostMapping("/submit-service-request")
    public String submitServiceRequest(@RequestParam("requestDetails") String requestDetails,
                                       @RequestParam("priority") String priority,
                                       @RequestParam("contactInfo") String contactInfo,
                                       @RequestParam("recipient") String recipient) {
        // Получение аутентификационного объекта
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // Получение имени текущего аутентифицированного пользователя
        String username = authentication.getName();
        // Получение пользователя по его имени
        User user = userService.findByUsername(username);

        // Создание нового объекта ServiceRequest
        ServiceRequest request = new ServiceRequest();
        request.setRequestDetails(requestDetails);
        request.setPriority(priority);
        request.setContactInfo(contactInfo);
        request.setDateRequested(LocalDateTime.now()); // Установка текущей даты и времени
        request.setStatus("В ожидании"); // Установка статуса по умолчанию
        request.setUser(user); // Установка пользователя, который отправил заявку
        request.setRole(recipient); // Установка роли получателя

        // Сохранение заявки в базе данных
        serviceRequestService.save(request);

        // Перенаправление пользователя на главную страницу или куда-либо еще
        return "redirect:/call-service";
    }
    @GetMapping("/admin/admin-service")
    public String adminService(Model model) {
        List<ServiceRequest> adminRequests = serviceRequestService.getAllAdminRequests();
        model.addAttribute("adminRequests", adminRequests);
        return "admin-service";
    }

    @GetMapping("/operator/operator-service")
    public String operatorService(Model model) {
        List<ServiceRequest> operatorRequests = serviceRequestService.getAllOperatorRequests();
        model.addAttribute("operatorRequests", operatorRequests);
        return "operator-service";
    }
    @PostMapping("/update-status")
    public String updateStatus(@RequestParam("requestId") Long requestId,
                               @RequestParam("status") String status) {
        // Получаем аутентифицированного пользователя
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // Получаем имя текущего аутентифицированного пользователя
        String username = authentication.getName();
        // Получаем пользователя по его имени
        User user = userService.findByUsername(username);

        // Проверяем роль пользователя
        if (user.getRole().equals("OPERATOR")) {
            // Обновляем статус заявки
            serviceRequestService.updateStatus(requestId, status);
            // Перенаправляем оператора на страницу оператора
            return "redirect:/operator/operator-service";
        } else if (user.getRole().equals("ADMIN")) {
            // Обновляем статус заявки
            serviceRequestService.updateStatus(requestId, status);
            // Перенаправляем админа на страницу админа
            return "redirect:/admin/admin-service";
        } else {
            // В случае, если у пользователя другая роль, перенаправляем его на главную страницу
            return "redirect:/";
        }
    }
}