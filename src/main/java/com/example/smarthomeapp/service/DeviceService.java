package com.example.smarthomeapp.service;

import com.example.smarthomeapp.model.Device;
import com.example.smarthomeapp.model.Room;
import com.example.smarthomeapp.model.WeatherData;
import com.example.smarthomeapp.repository.DeviceRepository;
import com.example.smarthomeapp.repository.WeatherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DeviceService {
    private final DeviceRepository deviceRepository;
    private final ApplicationEventPublisher eventPublisher;
    @Autowired
    private WeatherRepository weatherRepository; // Autowire WeatherRepository
    @Autowired
    public DeviceService(DeviceRepository deviceRepository, ApplicationEventPublisher eventPublisher) {
        this.deviceRepository = deviceRepository;
        this.eventPublisher = eventPublisher;

    }

    public List<Device> getAllDevices() {
        return deviceRepository.findAll();
    }
    public void deleteDeviceById(Long deviceId) {

        deviceRepository.deleteById(deviceId);
    }
    public List<Device> findDevicesByRoomId(Long roomId) {
        return deviceRepository.findByRoomId(roomId); // Замените на ваш метод репозитория для поиска устройств по комнате
    }
    public Device findDeviceById(Long deviceId) {
        Optional<Device> deviceOptional = deviceRepository.findById(deviceId);
        return deviceOptional.orElse(null);
    }


    // Метод для установки температуры и влажности в объекты Device
    public void setTemperatureAndHumidityForDevices(List<Device> devices) {
        for (Device device : devices) {
            // Получаем данные о температуре и влажности для данного устройства из таблицы sensor_data
            // Здесь предполагается, что устройство связано с таблицей sensor_data через deviceId
            WeatherData weatherData = weatherRepository.findByDeviceId(device.getId());

            // Устанавливаем температуру и влажность в объект Device
            if (weatherData != null) {
                device.setTemperature(weatherData.getTemperature());
                device.setHumidity(weatherData.getHumidity());
            }
        }
    }
    public void saveDevice(Device device) {
        deviceRepository.save(device);
    }

    public Device getDeviceById(Long deviceId) {
        Optional<Device> deviceOptional = deviceRepository.findById(deviceId);
        return deviceOptional.orElse(null);
    }
    // Метод для обновления устройства
    public void updateDevice(Device device) {
        deviceRepository.save(device);
    }

}
