package com.example.smarthomeapp.model;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "sensor_data")
@Getter
@Setter
@Builder
@AllArgsConstructor // Добавлено здесь
public class WeatherData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "location")
    private String location;

    @Column(name = "temperature")
    private Integer temperature;

    @Column(name = "humidity")
    private Double humidity;

    @Column(name = "wind_meters_ph")
    private Double windMetersPerHour;

    @Column(name = "pressure_mb")
    private Double pressureMb;

    @Column(name = "weather_condition")
    private String weatherCondition;

    @Column(name = "timestamp")
    private Timestamp timestamp;

    @Column(name = "device_id")
    private Long deviceId;

    @Column(name = "room_id")
    private Long roomId;

    @Column(name = "device_name")
    private String deviceName;

    // Добавляем поля для пользователя
    private String userName;
    private String userPhone;
    private String userAddress;

    // Пустой конструктор по умолчанию
    public WeatherData() {
    }

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getTemperature() {
        return temperature;
    }

    public void setTemperature(Integer temperature) {
        this.temperature = temperature;
    }

    public Double getHumidity() {
        return humidity;
    }

    public void setHumidity(Double humidity) {
        this.humidity = humidity;
    }

    public Double getWindMetersPerHour() {
        return windMetersPerHour;
    }

    public void setWindMetersPerHour(Double windMetersPerHour) {
        this.windMetersPerHour = windMetersPerHour;
    }

    public Double getPressureMb() {
        return pressureMb;
    }

    public void setPressureMb(Double pressureMb) {
        this.pressureMb = pressureMb;
    }

    public String getWeatherCondition() {
        return weatherCondition;
    }

    public void setWeatherCondition(String weatherCondition) {
        this.weatherCondition = weatherCondition;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }


    public Double getPressureMB() {
        return pressureMb;
    }
    public WeatherData clone() {
        WeatherData clonedWeatherData = new WeatherData();
        clonedWeatherData.setId(this.id);
        clonedWeatherData.setLocation(this.location);
        // Копируйте остальные поля, которые вы хотите скопировать

        return clonedWeatherData;
    }
    // Добавляем методы установки для полей пользователя
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }
    @Column(name = "user_id")
    private Long userId; // Добавляем поле для userId

    // Геттер и сеттер для userId
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}