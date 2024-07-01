package com.example.smarthomeapp.service;

import com.example.smarthomeapp.model.TemperatureAndHumidityScenario;
import com.example.smarthomeapp.model.WaterLeakScenario;
import com.example.smarthomeapp.repository.TemperatureAndHumidityScenarioRepository;
import com.example.smarthomeapp.repository.WaterLeakScenarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TemperatureAndHumidityScenarioService {

    @Autowired
    private TemperatureAndHumidityScenarioRepository scenarioRepository;
    @Autowired
    private WaterLeakScenarioRepository waterLeakScenarioRepository;

    public void addScenario(TemperatureAndHumidityScenario scenario) {
        scenarioRepository.save(scenario);
    }
    // Метод для получения всех сценариев
    public List<TemperatureAndHumidityScenario> getAllScenarios() {
        return scenarioRepository.findAll();
    }
    // Метод для получения сценариев для определенного устройства по его идентификатору
    public List<TemperatureAndHumidityScenario> getScenariosByDeviceId(Long deviceId) {
        return scenarioRepository.findByDeviceId(deviceId);
    }
    public TemperatureAndHumidityScenario findScenarioById(Long scenarioId) {
        return scenarioRepository.findById(scenarioId).orElse(null);
    }
    public List<TemperatureAndHumidityScenario> getScenariosByUserId(Long userId) {
        return scenarioRepository.findByUserId(userId);
    }
    public void deleteScenarioById(Long scenarioId) {
        scenarioRepository.deleteById(scenarioId);
    }
    public List<WaterLeakScenario> getAllWaterLeakScenarios() {
        return waterLeakScenarioRepository.findAll();
    }

    public List<WaterLeakScenario> getWaterLeakScenariosByUserId(Long userId) {
        return waterLeakScenarioRepository.findByUserId(userId);
    }
    public void deleteTemperatureAndHumidityScenarioById(Long scenarioId) {
        scenarioRepository.deleteById(scenarioId);
    }
    // Добавим метод для удаления сценариев обнаружения протечки воды
    public void deleteWaterLeakScenarioById(Long scenarioId) {
        scenarioRepository.deleteById(scenarioId);    }
}