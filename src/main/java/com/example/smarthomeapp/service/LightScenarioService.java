package com.example.smarthomeapp.service;

import com.example.smarthomeapp.model.LightScenario;
import com.example.smarthomeapp.repository.LightScenarioRepository;
import com.example.smarthomeapp.repository.TemperatureAndHumidityScenarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Time;
import java.time.LocalTime;
import java.util.List;

@Service
public class LightScenarioService {
    @Autowired
    private TemperatureAndHumidityScenarioRepository scenarioRepository;
    private final LightScenarioRepository lightScenarioRepository;

    @Autowired
    public LightScenarioService(LightScenarioRepository lightScenarioRepository) {
        this.lightScenarioRepository = lightScenarioRepository;
    }

    @Transactional // Добавляем аннотацию @Transactional
    public void addScenario(LightScenario scenario) {
        lightScenarioRepository.save(scenario);
    }

    public List<LightScenario> getAllScenarios() {
        return lightScenarioRepository.findAll(); // заменено с scenarioRepository на lightScenarioRepository
    }

    public void saveScenario(LightScenario scenario) {
        lightScenarioRepository.save(scenario);
    }
    public List<LightScenario> getScenariosByTime(LocalTime currentTime) {
        return lightScenarioRepository.findByLightTime(Time.valueOf(currentTime));
    }
    // Метод для получения списка сценариев включения и выключения света
    public List<LightScenario> getLightScenarios() {
        return lightScenarioRepository.findAll(); // Пример, как получить все сценарии из репозитория
    }

    public LightScenario findScenarioById(Long id) {
        return lightScenarioRepository.findById(id).orElse(null);
    }

    public void deleteScenarioById(Long id) {
        lightScenarioRepository.deleteById(id);
    }

    // LightScenarioService
    public List<LightScenario> getScenariosByUserId(Long userId) {
        return lightScenarioRepository.findByUserId(userId);
    }
    public void deleteLightScenarioById(Long scenarioId) {
        scenarioRepository.deleteById(scenarioId);
    }
}