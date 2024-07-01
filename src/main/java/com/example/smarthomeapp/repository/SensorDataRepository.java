package com.example.smarthomeapp.repository;

import com.example.smarthomeapp.model.User;
import com.example.smarthomeapp.model.WeatherData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface SensorDataRepository extends JpaRepository<WeatherData, Long> {

    @Query(value = "SELECT * FROM sensor_data WHERE timestamp >= DATE_SUB(CURRENT_DATE, INTERVAL 1 MONTH)", nativeQuery = true)
    List<WeatherData> findSensorDataForLastMonth();

    List<WeatherData> findByDeviceId(Long deviceId);
    @Query(value = "SELECT * FROM sensor_data", nativeQuery = true)
    List<WeatherData> findAllSensorData();
    @Query(value = "SELECT u.username as userName, u.phone as userPhone, u.address as userAddress " +
            "FROM devices d JOIN users u ON d.user_id = u.id " +
            "WHERE d.device_id = :deviceId", nativeQuery = true)
    Map<String, Object> findUserDataByDeviceId(Long deviceId);

    @Query("SELECT wd FROM WeatherData wd WHERE wd.deviceId IN " +
            "(SELECT d.id FROM Device d WHERE d.user.id = :userId) " +
            "AND wd.timestamp >= :lastMonthDate")
    List<WeatherData> findSensorDataForLastMonthByUserId(@Param("userId") Long userId, @Param("lastMonthDate") Date lastMonthDate);
}