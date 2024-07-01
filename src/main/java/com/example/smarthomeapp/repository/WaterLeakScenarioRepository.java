package com.example.smarthomeapp.repository;

import com.example.smarthomeapp.model.WaterLeakScenario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WaterLeakScenarioRepository extends JpaRepository<WaterLeakScenario, Long> {
    WaterLeakScenario findByDeviceId(Long id);

    List<WaterLeakScenario> findByUserId(Long id);
    // Можно добавить дополнительные методы, если необходимо
}