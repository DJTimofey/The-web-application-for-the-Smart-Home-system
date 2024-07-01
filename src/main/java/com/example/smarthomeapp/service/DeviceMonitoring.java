package com.example.smarthomeapp.service;

import com.example.smarthomeapp.model.Device;
import com.example.smarthomeapp.model.DeviceStatus;
import com.example.smarthomeapp.model.TemperatureAndHumidityScenario;
import com.example.smarthomeapp.model.User;
import com.example.smarthomeapp.repository.DeviceRepository;
import com.example.smarthomeapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Component
public class DeviceMonitoring {
    @Autowired
    private MqttService mqttService; // Ваш сервис MQTT

    @Autowired
    private DeviceService deviceService;
    @Autowired
    private UserRepository userRepository;
    private final JavaMailSender javaMailSender;

    @Autowired
    public DeviceMonitoring(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Autowired
    private TemperatureAndHumidityScenarioService temperatureAndHumidityScenarioService;

    @Autowired
    private DeviceRepository deviceRepository;

    @Scheduled(fixedRate = 3600000)
    public void checkAndControlDevices() {
        try {
            // Получаем список всех устройств из базы данных
            List<Device> devices = deviceRepository.findAll();
            // Перебираем все устройства
            // Перебираем все устройства
            for (Device device : devices) {
                Integer temperature = device.getTemperature();
                Double humidity = device.getHumidity(); // Получаем также данные о влажности

                // Проверяем, есть ли у данного устройства сценарий для температуры
                List<TemperatureAndHumidityScenario> temperatureScenarios = temperatureAndHumidityScenarioService.getScenariosByDeviceId(device.getId());
                for (TemperatureAndHumidityScenario temperatureScenario : temperatureScenarios) {
                    Double minTemp = temperatureScenario.getMinTemperature();
                    Double maxTemp = temperatureScenario.getMaxTemperature();
                    if (temperature != null && (temperature < minTemp || temperature > maxTemp)) {
                        // Определяем тип сообщения в зависимости от того, превышает ли температура максимальное или минимальное значение
                        String temperatureMessage;
                        if (temperature < minTemp) {
                            temperatureMessage = "Температура ниже минимальной для устройства " + device.getName() + ". Текущая температура: " + temperature;
                        } else {
                            temperatureMessage = "Температура выше максимальной для устройства " + device.getName() + ". Текущая температура: " + temperature;
                        }
                        // Получаем user_id устройства
                        Long userId = device.getUserId();
                        if (userId != null) {
                            // Ищем пользователя по user_id
                            Optional<User> userOptional = userRepository.findById(userId);
                            userOptional.ifPresent(user -> {
                                // Отправляем уведомление на почту пользователя о состоянии температуры
                                sendEmailNotification(user.getEmail(), "Предупреждение о температуре", temperatureMessage);                            });
                        }
                    }
                }

                // Проверяем, есть ли у данного устройства сценарий для влажности
                List<TemperatureAndHumidityScenario> humidityScenarios = temperatureAndHumidityScenarioService.getScenariosByDeviceId(device.getId());
                for (TemperatureAndHumidityScenario humidityScenario : humidityScenarios) {
                    Double minHumidity = humidityScenario.getMinHumidity();
                    Double maxHumidity = humidityScenario.getMaxHumidity();
                    if (humidity != null && (humidity < minHumidity || humidity > maxHumidity)) {
                        // Определяем тип сообщения в зависимости от того, превышает ли влажность максимальное или минимальное значение
                        String humidityMessage;
                        if (humidity < minHumidity) {
                            humidityMessage = "Влажность ниже минимальной для устройства " + device.getName() + ". Текущая влажность: " + humidity;
                        } else {
                            humidityMessage = "Влажность выше максимальной для устройства " + device.getName() + ". Текущая влажность: " + humidity;
                        }
// Получаем user_id устройства
                        Long userId = device.getUserId();
                        if (userId != null) {
                            // Ищем пользователя по user_id
                            Optional<User> userOptional = userRepository.findById(userId);
                            userOptional.ifPresent(user -> {
                                // Отправляем уведомление на почту пользователю о состоянии влажности
                                sendEmailNotification(user.getEmail(), "Предупреждение о влажности", humidityMessage);
                            });
                        }
                    }
                }
            }
            // Фильтруем устройства по типу sensorTaH
            List<Device> sensorTaHDevices = devices.stream()
                    .filter(device -> device.getType().equals("sensorTaH"))
                    .collect(Collectors.toList());

            // Получаем текущие значения температуры и влажности для устройств типа sensorTaH
            List<Device> devicesWithTemperature = sensorTaHDevices.stream()
                    .filter(device -> device.getTemperature() != null)
                    .toList();
            List<Device> devicesWithHumidity = sensorTaHDevices.stream()
                    .filter(device -> device.getHumidity() != null)
                    .toList();

            // Выполняем расчеты только для устройств типа sensorTaH
            double averageTemperature = devicesWithTemperature.isEmpty() ? 0.0 :
                    devicesWithTemperature.stream()
                            .mapToDouble(Device::getTemperature)
                            .average().orElse(0.0);
            double averageHumidity = devicesWithHumidity.isEmpty() ? 0.0 :
                    devicesWithHumidity.stream()
                            .mapToDouble(Device::getHumidity)
                            .average().orElse(0.0);

            // Получаем минимальное и максимальное значение средней температуры только для устройств типа sensorTaH
            double minAverageTemperature = temperatureAndHumidityScenarioService.getAllScenarios().stream()
                    .filter(scenario -> scenario.getDeviceType().equals("sensorTaH"))
                    .mapToDouble(TemperatureAndHumidityScenario::getMinTemperature)
                    .min().orElse(0.0);
            double maxAverageTemperature = temperatureAndHumidityScenarioService.getAllScenarios().stream()
                    .filter(scenario -> scenario.getDeviceType().equals("sensorTaH"))
                    .mapToDouble(TemperatureAndHumidityScenario::getMaxTemperature)
                    .max().orElse(0.0);

            // Получаем максимальное значение средней влажности из сценариев только для устройств типа sensorTaH
            double maxAverageHumidity = temperatureAndHumidityScenarioService.getAllScenarios().stream()
                    .filter(scenario -> scenario.getDeviceType().equals("sensorTaH"))
                    .mapToDouble(TemperatureAndHumidityScenario::getMaxHumidity)
                    .max().orElse(0.0);

            // Отправляем сообщение на брокер MQTT с данными о средних значениях температуры и влажности,
            // а также минимальных и максимальных значениях средней температуры и влажности для устройств типа sensorTaH
            String topic = "topic/device_monitoring";
            for (Device device : sensorTaHDevices) {
                String message = "Название устройства: " + device.getName() +
                        "\nИмя пользователя: " + device.getUser().getUsername() +
                        "\nСредняя температура: " + averageTemperature +
                        "\nСредняя влажность: " + averageHumidity +
                        "\nМинимальная средняя температура: " + minAverageTemperature +
                        "\nМаксимальная средняя температура: " + maxAverageTemperature +
                        "\nМаксимальная средняя влажность: " + maxAverageHumidity;
                mqttService.sendMessage(topic, message);
            }

            // Проверяем устройства на соответствие сценариям и управляем air condition и heater только для устройств типа sensorTaH
            manageAirConditionerAndHeater(sensorTaHDevices, averageTemperature, averageHumidity, minAverageTemperature, maxAverageTemperature, maxAverageHumidity);

        } catch (Exception e) {
            System.out.println("Произошла ошибка во время мониторинга устройств: " + e.getMessage());

        }
    }
    private void manageAirConditionerAndHeater(List<Device> devices, double averageTemperature, double averageHumidity, double minAverageTemperature, double maxAverageTemperature, double maxMinAverageHumidity) {
        // Переменные для отслеживания, нужно ли включить обогреватель или кондиционер
        boolean turnOnHeater = false;
        boolean turnOnAirConditioner = false;

        // Перебираем все устройства
        for (Device device : devices) {
            // Получаем список всех сценариев для данного устройства из базы данных
            List<TemperatureAndHumidityScenario> scenariosForDevice = temperatureAndHumidityScenarioService.getScenariosByDeviceId(device.getId());

            // Перебираем сценарии для данного устройства
            for (TemperatureAndHumidityScenario scenario : scenariosForDevice) {
                // Получаем минимальные и максимальные значения температуры и влажности из сценария
                Double minTemp = scenario.getMinTemperature();
                Double maxTemp = scenario.getMaxTemperature();
                Double minHumidity = scenario.getMinHumidity();
                Double maxHumidity = scenario.getMaxHumidity();

                // Проверяем условия для включения обогревателя
                if (minAverageTemperature != 0.0 && averageTemperature < minTemp) {
                    turnOnHeater = true;
                }

                // Проверяем условия для включения кондиционера
                if (maxAverageTemperature != 0.0 && averageTemperature > maxTemp) {
                    turnOnAirConditioner = true;
                }

                if (maxMinAverageHumidity != 0.0 && averageHumidity > maxHumidity) {
                    turnOnAirConditioner = true;
                }

                // Если температура и влажность соответствуют максимальным и минимальным значениям сценария,
                // отключаем обогреватель и кондиционер
                if (minAverageTemperature != 0.0 && maxAverageTemperature != 0.0 && minHumidity != null && maxHumidity != null) {
                    if (averageTemperature >= minTemp && averageTemperature <= maxTemp && averageHumidity >= minHumidity && averageHumidity <= maxHumidity) {
                        turnOnHeater = false;
                        turnOnAirConditioner = false;
                    }
                }
            }

            // Включаем или выключаем обогреватель для данного устройства
            if (turnOnHeater) {
                turnOnDevice("heater", device.getName(), device.getUser().getUsername(), devices);
            } else {
                turnOffDevice("heater", device.getName(), device.getUser().getUsername(), devices);
            }

            // Включаем или выключаем кондиционер для данного устройства
            if (turnOnAirConditioner) {
                turnOnDevice("air condition", device.getName(), device.getUser().getUsername(), devices);
            } else {
                turnOffDevice("air condition", device.getName(), device.getUser().getUsername(), devices);
            }
        }
    }

    private void turnOnDevice(String deviceType, String deviceName, String userName, List<Device> devices) {
        // Находим устройство нужного типа и имени
        Optional<Device> deviceOptional = devices.stream()
                .filter(device -> device.getType().equals(deviceType) && device.getName().equals(deviceName))
                .findFirst();

        // Если устройство найдено
        deviceOptional.ifPresent(device -> {
            // Проверяем текущее состояние устройства
            if (device.getStatus() == DeviceStatus.On) {
                // Устройство уже включено, ничего не делаем
                return;
            }

            // Получаем сценарии пользователя
            List<TemperatureAndHumidityScenario> scenarios = temperatureAndHumidityScenarioService.getScenariosByUserId(Long.valueOf(userName));
            for (TemperatureAndHumidityScenario scenario : scenarios) {
                // Проверяем, соответствует ли сценарий устройству
                if (scenario.getDeviceId().equals(device.getId())) {
                    // Включаем устройство
                    device.setStatus(DeviceStatus.On);
                    deviceService.updateDevice(device);
                    System.out.println(deviceType + " (" + deviceName + ") включено пользователем: " + userName);
                    // Отправляем сообщение на брокер MQTT о включении устройства
                    String message = deviceType + " (" + deviceName + ") включено пользователем: " + userName;
                    mqttService.sendMessage("topic/" + deviceType + "_control", message);
                    // Отправляем уведомление пользователю
                    sendEmailNotification(userName, "Активация устройства", deviceType + " (" + deviceName + ") было включено.");
                    return; // Выходим из цикла, так как уже выполнено действие для устройства
                }
            }
// Если не найден сценарий для устройства
            sendEmailNotification(userName, "Не удалось активировать устройство", "У вас нет сценария для включения " + deviceType + " (" + deviceName + "). Пожалуйста, создайте сценарий для управления этим устройством.");
        });
    }

    private void turnOffDevice(String deviceType, String deviceName, String userName, List<Device> devices) {
        // Находим устройство нужного типа и имени
        Optional<Device> deviceOptional = devices.stream()
                .filter(device -> device.getType().equals(deviceType) && device.getName().equals(deviceName))
                .findFirst();

        // Если устройство найдено
        deviceOptional.ifPresent(device -> {
            // Проверяем текущее состояние устройства
            if (device.getStatus() == DeviceStatus.Off) {
                // Устройство уже выключено, ничего не делаем
                return;
            }

            // Получаем сценарии пользователя
            List<TemperatureAndHumidityScenario> scenarios = temperatureAndHumidityScenarioService.getScenariosByUserId(Long.valueOf(userName));
            for (TemperatureAndHumidityScenario scenario : scenarios) {
                // Проверяем, соответствует ли сценарий устройству
                if (scenario.getDeviceId().equals(device.getId())) {
                    // Выключаем устройство
                    device.setStatus(DeviceStatus.Off);
                    deviceService.updateDevice(device);
                    System.out.println(deviceType + " (" + deviceName + ") выключено пользователем: " + userName);
                    // Отправляем сообщение на брокер MQTT о выключении устройства
                    String message = deviceType + " (" + deviceName + ") выключено пользователем: " + userName;
                    mqttService.sendMessage("topic/" + deviceType + "_control", message);
                    // Отправляем уведомление пользователю
                    sendEmailNotification(userName, "Деактивация устройства", deviceType + " (" + deviceName + ") было выключено.");
                    return; // Выходим из цикла, так как уже выполнено действие для устройства
                }
            }
// Если не найден сценарий для устройства
            sendEmailNotification(userName, "Не удалось деактивировать устройство", "У вас нет сценария для выключения " + deviceType + " (" + deviceName + "). Пожалуйста, создайте сценарий для управления этим устройством.");
        });
    }
    public void sendEmailNotification(String email, String subject, String text) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email);
        mailMessage.setSubject(subject);
        mailMessage.setText(text);
        javaMailSender.send(mailMessage);
        System.out.println("Уведомление по электронной почте отправлено.");

    }


}