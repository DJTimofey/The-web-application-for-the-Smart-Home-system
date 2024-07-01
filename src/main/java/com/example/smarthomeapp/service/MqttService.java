package com.example.smarthomeapp.service;

import com.example.smarthomeapp.mqtt.MqttClientComponent;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class MqttService {

    private final MqttClientComponent mqttClientComponent;
    private final Logger logger = LoggerFactory.getLogger(MqttService.class);

    @Autowired
    public MqttService(MqttClientComponent mqttClientComponent) {
        this.mqttClientComponent = mqttClientComponent;
    }

    @PostConstruct
    public void initialize() {
        mqttClientComponent.connect();
    }

    public void sendMessage(String topic, String message) {
        mqttClientComponent.sendMessage(topic, message);
        logger.info("Sent message to topic '{}': {}", topic, message);
    }
}