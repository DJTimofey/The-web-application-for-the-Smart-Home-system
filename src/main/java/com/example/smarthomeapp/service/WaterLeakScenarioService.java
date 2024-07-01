package com.example.smarthomeapp.service;

import com.example.smarthomeapp.model.WaterLeakScenario;
import com.example.smarthomeapp.repository.WaterLeakScenarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WaterLeakScenarioService {
    @Autowired
    private WaterLeakScenarioRepository waterLeakScenarioRepository;

    public void addScenario(WaterLeakScenario scenario) {

        waterLeakScenarioRepository.save(scenario);
    }
    public List<WaterLeakScenario> getWaterLeakScenariosByUserId(Long userId) {
        return waterLeakScenarioRepository.findByUserId(userId);
    }
    public WaterLeakScenario findScenarioById(Long id) {
        return waterLeakScenarioRepository.findById(id).orElse(null);
    }

    public void deleteScenarioById(Long id) {
        waterLeakScenarioRepository.deleteById(id);
    }
}
