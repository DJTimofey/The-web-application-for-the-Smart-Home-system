package com.example.smarthomeapp.model;

import javax.persistence.*;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "power_switch_scenarios")
public class PowerSwitchScenario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "scenario_name", nullable = false)
    private String scenarioName;


    @Column(name = "action_switch", nullable = false)
    private String actionSwitch;

    @Column(name = "switch_time", nullable = false)
    private Time switchTime;

    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;

    @Column(name = "user_id")
    private Long userId; //

    @Column(name = "device_id")
    private Long deviceId;

    @Column(name = "device_name")
    private String deviceName;

    @Column(name = "device_type")
    private String deviceType;

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getScenarioName() {
        return scenarioName;
    }

    public void setScenarioName(String scenarioName) {
        this.scenarioName = scenarioName;
    }

    public String getActionSwitch() {
        return actionSwitch;
    }

    public void setActionSwitch( String actionSwitch) {
        this.actionSwitch = actionSwitch;
    }

    public Time getSwitchTime() {
        return switchTime;
    }

    public void setSwitchTime(Time switchTime) {
        this.switchTime = switchTime;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
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