package com.example.smarthomeapp;

import com.example.smarthomeapp.model.WeatherData;
import com.example.smarthomeapp.repository.SensorDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SensorDataInitializer implements CommandLineRunner {

    @Autowired
    private SensorDataRepository sensorDataRepository;

    @Override
    public void run(String... args) throws Exception {
        List<WeatherData> sensorDataList = sensorDataRepository.findSensorDataForLastMonth();
        sensorDataList.forEach(System.out::println);
    }
}