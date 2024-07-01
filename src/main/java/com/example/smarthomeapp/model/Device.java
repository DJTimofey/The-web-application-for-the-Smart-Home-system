package com.example.smarthomeapp.model;

import javax.persistence.*;

@Entity
@Table(name = "devices")
public class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "device_id")
    private Long id;

    @Column(name = "device_name", nullable = false)
    private String name;

    @Column(name = "device_type", nullable = false)
    private String type;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private DeviceStatus status = DeviceStatus.Off; // По умолчанию OFF

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;


    private Integer temperature;


    private Double humidity;

    @Column(name = "brightness")
    private Integer brightness;
    private String userName;
    private String userPhone;
    private String userAddress;

    // Геттеры и сеттеры
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

    // Конструкторы
    public Device() {
    }

    public Device(String name, String type, User user) {
        this.name = name;
        this.type = type;
        this.user = user;
    }

    public Integer getBrightness() {
        return brightness;
    }

    public void setBrightness(Integer brightness) {
        this.brightness = brightness;
    }


    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public DeviceStatus getStatus() {
        return status;
    }

    public void setStatus(DeviceStatus status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public Room getRoom() {
        return room;
    }

    public Long getUserId() {
        return user != null ? user.getId() : null;
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
}