package com.example.smarthomeapp.service;

import com.example.smarthomeapp.model.Device;
import com.example.smarthomeapp.model.DeviceStatus;
import com.example.smarthomeapp.model.User;
import com.example.smarthomeapp.model.WaterLeakScenario;
import com.example.smarthomeapp.repository.DeviceRepository;

import com.example.smarthomeapp.repository.WaterLeakScenarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Scanner;

@Service
public class DeviceErrorSimulator {

    private final MqttService mqttService;
    private final DeviceRepository deviceRepository;
    private final JavaMailSender javaMailSender;
    public final UserService userService;

    private final WaterLeakScenarioRepository waterLeakScenarioRepository;
    @Autowired
    public DeviceErrorSimulator(MqttService mqttService, DeviceRepository deviceRepository, JavaMailSender javaMailSender, UserService userService, WaterLeakScenarioRepository waterLeakScenarioRepository) {
        this.mqttService = mqttService;
        this.deviceRepository = deviceRepository;
        this.javaMailSender = javaMailSender;
        this.userService = userService;
        this.waterLeakScenarioRepository = waterLeakScenarioRepository;
        startConsoleListener(); // Вызываем метод для начала прослушивания команд из консоли
    }

    public void simulateDeviceErrorAndTurnOff(Long deviceId) {
        // Получаем устройство из базы данных по его ID
        Optional<Device> deviceOptional = deviceRepository.findById(deviceId);
        if (deviceOptional.isPresent()) {
            Device device = deviceOptional.get();

            // Симулируем ошибку
            String errorMessage = "Симуляция ошибки";
            System.out.println("Устройство " + device.getName() + " (" + device.getId() + "): " + errorMessage);

            // Отправляем команду на выключение устройства через MQTT
            String topic = "devices/" + deviceId + "/command";
            String message = "Off";
            mqttService.sendMessage(topic, message);

            // Обновляем статус устройства в базе данных на Off
            device.setStatus(DeviceStatus.Off);
            deviceRepository.save(device);
        } else {
            System.out.println("Устройство с ID " + deviceId + " не найдено.");
        }
    }

    public void simulateWaterLeak(Long deviceId) {
        // Получаем устройство из базы данных по его ID
        Optional<Device> deviceOptional = deviceRepository.findById(deviceId);
        if (deviceOptional.isPresent()) {
            Device device = deviceOptional.get();

            // Симулируем затопление
            System.out.println("Симуляция затопления в комнате " + device.getRoom());

            // Отправляем уведомление на почту пользователя
            User user = userService.getUserById(device.getUserId());
            if (user != null) {
                sendEmailNotification(user.getEmail(), "затопление");
            }

            // Находим сценарий протечки воды для устройства
            WaterLeakScenario scenario = waterLeakScenarioRepository.findByDeviceId(deviceId);
            if (scenario != null) {
                // Устанавливаем статус "Мокрый"
                scenario.setWaterLeakDetection("Мокрый");
                waterLeakScenarioRepository.save(scenario);
            } else {
                System.out.println("Сценарий протечки воды для устройства с ID " + deviceId + " не найден.");
            }
        } else {
            System.out.println("Устройство с ID " + deviceId + " не найдено.");
        }
    }

    public void stopWaterLeak(Long deviceId) {
        // Получаем устройство из базы данных по его ID
        Optional<Device> deviceOptional = deviceRepository.findById(deviceId);
        if (deviceOptional.isPresent()) {
            Device device = deviceOptional.get();

            // Прекращаем затопление
            System.out.println("Прекращение затопления в комнате " + device.getRoom());

            // Отправляем уведомление на почту пользователя
            User user = userService.getUserById(device.getUserId());
            if (user != null) {
                sendEmailNotification(user.getEmail(), "прекращение затопления");
            }

            // Находим сценарий протечки воды для устройства
            WaterLeakScenario scenario = waterLeakScenarioRepository.findByDeviceId(deviceId);
            if (scenario != null) {
                // Устанавливаем статус "Сухой"
                scenario.setWaterLeakDetection("Сухой");
                waterLeakScenarioRepository.save(scenario);
            } else {
                System.out.println("Сценарий протечки воды для устройства с ID " + deviceId + " не найден.");
            }
        } else {
            System.out.println("Устройство с ID " + deviceId + " не найдено.");
        }
    }

    public void startConsoleListener() {
        Thread consoleThread = new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.println("Ожидание команды...");
                String command = scanner.nextLine();
                if ("simulate".equalsIgnoreCase(command.trim())) {
                    // Если введенная команда - simulate, запускаем симуляцию ошибки
                    System.out.println("Введите ID устройства:");
                    Long deviceId = scanner.nextLong(); // Считываем введенный пользователем идентификатор устройства
                    simulateDeviceErrorAndTurnOff(deviceId);
                } else if ("simulate-leak".equalsIgnoreCase(command.trim())) {
                    // Если введенная команда - simulate-leak, запускаем симуляцию затопления
                    System.out.println("Введите ID устройства для симуляции затопления:");
                    Long deviceId = scanner.nextLong(); // Считываем введенный пользователем идентификатор устройства
                    simulateWaterLeak(deviceId);
                } else if ("stop-leak".equalsIgnoreCase(command.trim())) {
                    // Если введенная команда - stop-leak, запускаем симуляцию остановки затопления
                    System.out.println("Введите ID устройства для остановки затопления:");
                    Long deviceId = scanner.nextLong(); // Считываем введенный пользователем идентификатор устройства
                    stopWaterLeak(deviceId);
                } else if ("exit".equalsIgnoreCase(command.trim())) {
                    break;
                } else {
                    System.out.println("Неизвестная команда.");
                }
            }
        });
        consoleThread.start();
    }

    private void sendEmailNotification(String email, String notificationType) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email);
        mailMessage.setSubject("Уведомление о затоплении");

        // Определяем текст сообщения в зависимости от типа уведомления
        if ("затопление".equals(notificationType)) {
            mailMessage.setText("Затопление в вашей комнате!");
        } else if ("прекращение затопления".equals(notificationType)) {
            mailMessage.setText("Затопление в вашей комнате прекращено.");
        } else {
            // Если передан неизвестный тип уведомления, отправляем общее сообщение
            mailMessage.setText("Уведомление от Смарт-дома");
        }

        javaMailSender.send(mailMessage);
        System.out.println("Email notification sent.");
    }
}