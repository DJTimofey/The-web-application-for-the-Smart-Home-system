package com.example.smarthomeapp.service;

import com.example.smarthomeapp.model.ServiceRequest;
import com.example.smarthomeapp.repository.ServiceRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ServiceRequestService {

    private final ServiceRequestRepository serviceRequestRepository;

    public ServiceRequestService(ServiceRequestRepository serviceRequestRepository) {
        this.serviceRequestRepository = serviceRequestRepository;
    }




    // Метод для получения всех заявок пользователя
    public List<ServiceRequest> getAllUserRequests() {
        // Реализуйте логику, чтобы получить заявки конкретного пользователя
        // Например, можно использовать сессию или аутентификацию для определения пользователя
        // В данном примере просто возвращается список всех заявок
        return serviceRequestRepository.findAll();
    }

    // Метод для сохранения новой заявки
    public void submitServiceRequest(ServiceRequest request) {
        // Устанавливаем статус по умолчанию (например, "Ожидание")
        request.setStatus("Pending");
        // Сохраняем заявку в базе данных
        serviceRequestRepository.save(request);
    }
    public void save(ServiceRequest serviceRequest) {
        serviceRequestRepository.save(serviceRequest);
    }


    public List<ServiceRequest> getAllUserRequestsByUserId(Long userId) {
        return serviceRequestRepository.findByUserId(userId);
    }

    public void updateStatus(Long requestId, String status) {
        Optional<ServiceRequest> optionalRequest = serviceRequestRepository.findById(requestId);
        if (optionalRequest.isPresent()) {
            ServiceRequest request = optionalRequest.get();
            request.setStatus(status);
            serviceRequestRepository.save(request);
        }
    }
    public List<ServiceRequest> getAllAdminRequests() {
        // Фильтруем заявки по роли ADMIN
        return serviceRequestRepository.findByRole("ADMIN");
    }

    public List<ServiceRequest> getAllOperatorRequests() {
        // Фильтруем заявки по роли OPERATOR
        return serviceRequestRepository.findByRole("OPERATOR");
    }
}