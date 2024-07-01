package com.example.smarthomeapp.repository;

import com.example.smarthomeapp.model.CurtainScenario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Time;
import java.util.List;

@Repository
public interface CurtainScenarioRepository extends JpaRepository<CurtainScenario, Long> {
    List<CurtainScenario> findByOpenTime(Time valueOf);

    List<CurtainScenario> findByOpenTimeOrCloseTime(Time valueOf, Time valueOf1);

    List<CurtainScenario> findByUserId(Long id);
}