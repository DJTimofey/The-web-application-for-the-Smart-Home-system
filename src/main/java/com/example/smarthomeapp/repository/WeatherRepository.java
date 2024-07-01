package com.example.smarthomeapp.repository;


import java.time.Instant;
import java.util.List;
import java.util.Optional;

import com.example.smarthomeapp.model.WeatherData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface WeatherRepository extends JpaRepository<WeatherData, Long> {

    Optional<WeatherData> findFirstByOrderByTimestampDesc();
    WeatherData findByDeviceId(Long deviceId);

    List<WeatherData> findByTimestampBetween(Instant startDate, Instant endDate);
    @Query(value = "SELECT * FROM sensor_data WHERE device_id = :deviceId ORDER BY timestamp DESC LIMIT 1", nativeQuery = true)
    Optional<WeatherData> findLatestByDeviceId(@Param("deviceId") Long deviceId);

    WeatherData findFirstByDeviceIdOrderByTimestampDesc(Long id);

    List<WeatherData> findTop1ByDeviceIdOrderByTimestampDesc(Long id);


}