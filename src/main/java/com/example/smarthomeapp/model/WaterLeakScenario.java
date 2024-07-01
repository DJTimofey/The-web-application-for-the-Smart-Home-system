package com.example.smarthomeapp.model;
import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "water_leak_scenarios")
public class WaterLeakScenario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_date")
    private Date createdDate;

    @Column(name = "scenario_name")
    private String scenarioName;

    @Column(name = "water_leak_detection")
    private String waterLeakDetection;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "device_id")
    private Long deviceId;

    @Column(name = "device_name")
    private String deviceName;

    @Column(name = "device_type")
    private String deviceType;

    // Геттеры и сеттеры для всех полей

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getScenarioName() {
        return scenarioName;
    }

    public void setScenarioName(String scenarioName) {
        this.scenarioName = scenarioName;
    }

    public String getWaterLeakDetection() {
        return waterLeakDetection;
    }

    public void setWaterLeakDetection(String waterLeakDetection) {
        this.waterLeakDetection = waterLeakDetection;
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