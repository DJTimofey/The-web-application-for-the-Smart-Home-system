package com.example.smarthomeapp.service;

import com.example.smarthomeapp.model.CurtainScenario;
import com.example.smarthomeapp.repository.CurtainScenarioRepository;
import com.example.smarthomeapp.repository.TemperatureAndHumidityScenarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.time.LocalTime;
import java.util.List;

@Service
public class CurtainScenarioService {
    @Autowired
    private TemperatureAndHumidityScenarioRepository scenarioRepository;
    @Autowired
    private CurtainScenarioRepository curtainScenarioRepository;

    public void addScenario(CurtainScenario scenario) {
        curtainScenarioRepository.save(scenario);
    }
    public List<CurtainScenario> getAllScenarios() {
        return curtainScenarioRepository.findAll();
    }
    public List<CurtainScenario> getScenariosByTime(LocalTime time) {
        return curtainScenarioRepository.findByOpenTimeOrCloseTime(Time.valueOf(time), Time.valueOf(time));
    }

    public CurtainScenario findScenarioById(Long id) {
        return curtainScenarioRepository.findById(id).orElse(null);
    }

    public void deleteScenarioById(Long id) {
        curtainScenarioRepository.deleteById(id);
    }
    public List<CurtainScenario> getScenariosByUserId(Long userId) {
        return curtainScenarioRepository.findByUserId(userId);
    }
    public void deleteCurtainScenarioById(Long scenarioId) {
        scenarioRepository.deleteById(scenarioId);
    }
}