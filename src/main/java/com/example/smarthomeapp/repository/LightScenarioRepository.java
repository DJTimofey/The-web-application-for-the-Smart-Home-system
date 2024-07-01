package com.example.smarthomeapp.repository;

import com.example.smarthomeapp.model.LightScenario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Time;
import java.time.LocalTime;
import java.util.List;

public interface LightScenarioRepository extends JpaRepository<LightScenario, Long> {
    List<LightScenario> findByLightTime(Time lightTime);

    List<LightScenario> findByUserId(Long id);
}