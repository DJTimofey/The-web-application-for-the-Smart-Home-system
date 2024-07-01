package com.example.smarthomeapp.service;

import com.example.smarthomeapp.model.PowerSwitchScenario;
import com.example.smarthomeapp.repository.PowerSwitchScenarioRepository;
import com.example.smarthomeapp.repository.TemperatureAndHumidityScenarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class PowerSwitchScenarioService {
    @Autowired
    private TemperatureAndHumidityScenarioRepository scenarioRepository;
    @Autowired
    private PowerSwitchScenarioRepository powerSwitchScenarioRepository;

    public void addScenario(PowerSwitchScenario scenario) {
        powerSwitchScenarioRepository.save(scenario);
    }

    public List<PowerSwitchScenario> getAllScenarios() {
        return powerSwitchScenarioRepository.findAll();
    }
    public List<PowerSwitchScenario> getScenariosByTime(LocalTime time) {
        // Implement your logic to query the database and retrieve scenarios based on the given time
        // Example: You may use a repository method like findBySwitchTime to retrieve scenarios for a specific time
        return powerSwitchScenarioRepository.findBySwitchTime(Time.valueOf(time));
    }

    // PowerSwitchScenarioService
    public List<PowerSwitchScenario> getScenariosByUserId(Long userId) {
        return powerSwitchScenarioRepository.findByUserId(userId);
    }
    public PowerSwitchScenario findPowerSwitchScenarioById(Long id) {
        Optional<PowerSwitchScenario> scenarioOptional = powerSwitchScenarioRepository.findById(id);
        return scenarioOptional.orElse(null);
    }
    public void deletePowerSwitchScenarioById(Long id) {
        powerSwitchScenarioRepository.deleteById(id);
    }
    // метод для поиска сценария по его ID
    public PowerSwitchScenario findScenarioById(Long id) {
        return powerSwitchScenarioRepository.findById(id)
                .orElse(null); // можно изменить на выбрасывание исключения, если сценарий не найден
    }

    // метод для удаления сценария по его ID
    public void deleteScenarioById(Long id) {
        powerSwitchScenarioRepository.deleteById(id);
    }
}