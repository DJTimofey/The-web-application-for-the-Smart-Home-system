package com.example.smarthomeapp.controller;

import com.example.smarthomeapp.model.Device;
import com.example.smarthomeapp.model.DeviceStatus;
import com.example.smarthomeapp.model.PowerSwitchScenario;
import com.example.smarthomeapp.model.User;
import com.example.smarthomeapp.repository.UserRepository;
import com.example.smarthomeapp.service.DeviceService;
import com.example.smarthomeapp.service.MqttService;
import com.example.smarthomeapp.service.PowerSwitchScenarioService;
import com.example.smarthomeapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/power_switch_scenarios")
public class PowerSwitchScenarioController {

    @Autowired
    private PowerSwitchScenarioService powerSwitchScenarioService;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private MqttService mqttService;
    @Autowired
    private UserService userService;
    private final JavaMailSender javaMailSender;
    @Autowired
    private UserRepository userRepository;

    public PowerSwitchScenarioController(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @PostMapping("/add")
    public String addScenario(@RequestParam("scenarioName") String scenarioName,
                              @RequestParam("selectedDevices") List<Long> selectedDevices,
                              @RequestParam("action") String action,
                              @RequestParam("time") String time) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Long userId = userService.findByUsername(userDetails.getUsername()).getId();
            for (Long deviceId : selectedDevices) {
                Device device = deviceService.getDeviceById(deviceId);

                if (device != null) {
                    PowerSwitchScenario scenario = new PowerSwitchScenario();
                    scenario.setScenarioName(scenarioName);
                    scenario.setActionSwitch(String.valueOf(action));
                    scenario.setSwitchTime(Time.valueOf(LocalTime.parse(time)));
                    scenario.setCreatedAt(new Timestamp(System.currentTimeMillis()));
                    scenario.setUserId(userId);
                    scenario.setDeviceId(deviceId);
                    scenario.setDeviceName(device.getName());
                    scenario.setDeviceType(device.getType());
                    powerSwitchScenarioService.addScenario(scenario);
                } else {
                    return "error";
                }
            }

            String topic = "topic/add_power_switch_scenario";
            String message = "New power switch scenario added: " + scenarioName;
            mqttService.sendMessage(topic, message);

            return "success";
        } catch (Exception e) {
            return "error";
        }
    }

    @Scheduled(fixedRate = 60000) // Проверять каждую минуту
    public void executePowerSwitchScenarios() {
        try {
            // Получаем текущее время
            LocalTime currentTime = LocalTime.now().truncatedTo(ChronoUnit.MINUTES);

            // Получаем список активных сценариев включения/выключения устройств на основе текущего времени
            List<PowerSwitchScenario> activeScenarios = powerSwitchScenarioService.getScenariosByTime(currentTime);

            // Обрабатываем каждый активный сценарий
            for (PowerSwitchScenario scenario : activeScenarios) {
                // Проверяем, достигнуто ли время включения/выключения для данного сценария
                Time switchTime = scenario.getSwitchTime();
                LocalTime localSwitchTime = switchTime.toLocalTime();
                if (currentTime.equals(localSwitchTime)) {
                    // Выполняем действие, определенное в сценарии
                    String action = scenario.getActionSwitch();
                    Long deviceId = scenario.getDeviceId();

                    // Получаем устройство по его идентификатору
                    Device device = deviceService.getDeviceById(deviceId);

                    // Выполняем действие в зависимости от сценария
                    if (device != null) {
                        // Проверяем, соответствует ли текущее состояние устройства действию, определенному в сценарии
                        if (("On".equals(action) && device.getStatus() != DeviceStatus.On) ||
                                ("Off".equals(action) && device.getStatus() != DeviceStatus.Off)) {
                            // Обновляем состояние устройства
                            if ("On".equals(action)) {
                                device.setStatus(DeviceStatus.On);
                            } else {
                                device.setStatus(DeviceStatus.Off);
                            }
                            // Сохраняем обновленное состояние устройства
                            deviceService.updateDevice(device);

                            // Отправляем уведомление на почту
                            Long userId = device.getUserId();
                            if (userId != null) {
                                Optional<User> userOptional = userRepository.findById(userId);
                                userOptional.ifPresent(user -> {
                                    String subject = "Power Switch Scenario Executed";
                                    String message = "Power switch scenario has been executed for device " + device.getName() + ". Current status: " + action;
                                    sendEmailNotification(user.getEmail(), subject, message);
                                });
                            }
                        } else {
                            // Логируем сообщение, указывающее, что текущее состояние устройства уже соответствует действию, определенному в сценарии
                            System.out.println("Current device status is already consistent with the action defined in the scenario");
                        }
                    } else {
                        // Логируем сообщение, указывающее на то, что устройство с указанным идентификатором не найдено
                        System.out.println("Device with ID " + deviceId + " not found");
                    }
                }
            }
        } catch (Exception e) {
            // Обработка ошибок
            e.printStackTrace();
        }
    }
    public void sendEmailNotification(String email, String subject, String text) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email);
        mailMessage.setSubject(subject);
        mailMessage.setText(text);
        javaMailSender.send(mailMessage);
        System.out.println("Email notification sent.");
    }
}