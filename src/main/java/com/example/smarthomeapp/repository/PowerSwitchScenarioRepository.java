package com.example.smarthomeapp.repository;

import com.example.smarthomeapp.model.PowerSwitchScenario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Time;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface PowerSwitchScenarioRepository extends JpaRepository<PowerSwitchScenario, Long> {
    List<PowerSwitchScenario> findBySwitchTime(Time switchTime);

    List<PowerSwitchScenario> findByUserId(Long id);
    // Добавьте дополнительные методы, если это необходимо

}