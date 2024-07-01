package com.example.smarthomeapp.model;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "TemperatureAndHumidityScenarios")
public class TemperatureAndHumidityScenario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "userId")
    private Long userId;

    @Column(name = "deviceId")
    private Long deviceId;

    @Column(name = "scenarioName")
    private String scenarioName;

    @Column(name = "deviceName")
    private String deviceName;

    @Column(name = "deviceType")
    private String deviceType;

    @Column(name = "minTemperature")
    private Double minTemperature; // Используем класс-оболочку Double

    @Column(name = "maxTemperature")
    private Double maxTemperature; // Используем класс-оболочку Double

    @Column(name = "minHumidity")
    private Double minHumidity; // Используем класс-оболочку Double

    @Column(name = "maxHumidity")
    private Double maxHumidity; // Используем класс-оболочку Double

    @Column(name = "createdDate")
    private Date createdDate;

    // Геттеры и сеттеры для всех полей

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public String getScenarioName() {
        return scenarioName;
    }

    public void setScenarioName(String scenarioName) {
        this.scenarioName = scenarioName;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public Double getMinTemperature() {
        return minTemperature;
    }

    public void setMinTemperature(Double minTemperature) {
        this.minTemperature = minTemperature;
    }

    public Double getMaxTemperature() {
        return maxTemperature;
    }

    public void setMaxTemperature(Double maxTemperature) {
        this.maxTemperature = maxTemperature;
    }

    public Double getMinHumidity() {
        return minHumidity;
    }

    public void setMinHumidity(Double minHumidity) {
        this.minHumidity = minHumidity;
    }

    public Double getMaxHumidity() {
        return maxHumidity;
    }

    public void setMaxHumidity(Double maxHumidity) {
        this.maxHumidity = maxHumidity;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    @Transient // Поле не будет сохраняться в базе данных
    private List<Long> selectedDevices; // Поле для хранения выбранных устройств

    // Геттер и сеттер для поля selectedDevices
    public List<Long> getSelectedDevices() {
        return selectedDevices;
    }

    public void setSelectedDevices(List<Long> selectedDevices) {
        this.selectedDevices = selectedDevices;
    }
}