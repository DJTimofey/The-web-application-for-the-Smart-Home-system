package com.example.smarthomeapp.repository;

import com.example.smarthomeapp.model.TemperatureAndHumidityScenario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TemperatureAndHumidityScenarioRepository extends JpaRepository<TemperatureAndHumidityScenario, Long> {

    List<TemperatureAndHumidityScenario> findByDeviceId(Long deviceId);

    List<TemperatureAndHumidityScenario> findByUserId(Long id);
}