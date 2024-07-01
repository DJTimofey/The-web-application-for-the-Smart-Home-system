package com.example.smarthomeapp.service;

import com.example.smarthomeapp.model.WeatherData;
import com.example.smarthomeapp.repository.WeatherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SensorDataService {

    @Autowired
    private WeatherRepository weatherRepository;

    public WeatherData getSensorDataForDevice(Long deviceId) {
        return weatherRepository.findByDeviceId(deviceId);
    }

    public List<WeatherData> getAllSensorData() {
        return weatherRepository.findAll();
    }

}