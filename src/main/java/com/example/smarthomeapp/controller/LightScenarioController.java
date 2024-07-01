package com.example.smarthomeapp.controller;
import com.example.smarthomeapp.model.Device;
import com.example.smarthomeapp.model.DeviceStatus;
import com.example.smarthomeapp.model.LightScenario;
import com.example.smarthomeapp.model.User;
import com.example.smarthomeapp.repository.UserRepository;
import com.example.smarthomeapp.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/light_scenarios")
public class LightScenarioController {
    @Autowired
    private MqttService mqttService;
    private final JavaMailSender javaMailSender;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private LightScenarioService scenarioLightService;

    @Autowired
    private DeviceService deviceService;

    public LightScenarioController(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @PostMapping("/add")
    public String addScenario(@RequestParam("scenarioName") String scenarioName,
                              @RequestParam("selectedDevices") List<Long> selectedDevices,
                              @RequestParam(value = "lightSwitch", required = false) String lightSwitch,
                              @RequestParam(value = "lightBrightness", required = false) Integer lightBrightness,
                              @RequestParam(value = "lightTime") String lightTime) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Long userId = userService.findByUsername(userDetails.getUsername()).getId();

            for (Long deviceId : selectedDevices) {
                Device device = deviceService.getDeviceById(deviceId);

                if (device != null) {
                    LightScenario scenario = new LightScenario();
                    scenario.setUserId(userId);
                    scenario.setScenarioName(scenarioName);
                    scenario.setDeviceId(deviceId);
                    scenario.setDeviceName(device.getName());
                    scenario.setDeviceType(device.getType());
                    scenario.setCreatedAt(new Timestamp(System.currentTimeMillis())); // Устанавливаем текущую дату и время

                    // Устанавливаем время света
                    scenario.setLightTime(Time.valueOf(LocalTime.parse(lightTime))); // Парсим строку времени в формат LocalTime
                    scenarioLightService.addScenario(scenario);

                    // Установка пользовательских значений, если они были предоставлены
                    if (lightSwitch != null) {
                        scenario.setLightSwitch(lightSwitch);
                    }
                    if (lightBrightness != null) {
                        scenario.setLightBrightness(lightBrightness);
                    }

                } else {
                    return "error";
                }
            }

            String topic = "topic/add_light_scenario";
            String message = "New light scenario added: " + scenarioName;
            mqttService.sendMessage(topic, message);

            return "devices";
        } catch (Exception e) {
            return "error";
        }
    }

    // Добавьте методы для отображения и удаления сценариев аналогично примеру для датчиков температуры и влажности
// Метод для автоматического выполнения сценариев освещения
    @Scheduled(fixedRate = 60000) // Проверять каждую минуту
    public void executeLightScenarios() {
        try {
            // Получаем текущее время
            LocalTime currentTime = LocalTime.now().truncatedTo(ChronoUnit.MINUTES);

            // Получаем список активных сценариев на основе текущего времени
            List<LightScenario> activeScenarios = scenarioLightService.getScenariosByTime(currentTime);

            // Обрабатываем каждый активный сценарий
            for (LightScenario scenario : activeScenarios) {
                // Получаем устройство, к которому относится сценарий
                Device device = deviceService.getDeviceById(scenario.getDeviceId());

                if (device != null) {
                    // Проверяем состояние света и выполнение условий сценария
                    if ("On".equals(scenario.getLightSwitch())) {
                        // Если включение света
                        device.setStatus(DeviceStatus.On);
                        device.setBrightness(scenario.getLightBrightness());

                        // Отправляем уведомление на почту
                        Long userId = device.getUserId();
                        if (userId != null) {
                            Optional<User> userOptional = userRepository.findById(userId);
                            userOptional.ifPresent(user -> {
                                String subject = "Light Scenario Executed";
                                String message = "Light scenario has been executed for device " + device.getName() + ". Current status: On. Light brightness: " + scenario.getLightBrightness();
                                sendEmailNotification(user.getEmail(), subject, message);
                            });
                        }
                    } else if ("Off".equals(scenario.getLightSwitch())) {
                        // Если выключение света
                        device.setStatus(DeviceStatus.Off);
                        device.setBrightness(scenario.getLightBrightness());

                        // Отправляем уведомление на почту
                        Long userId = device.getUserId();
                        if (userId != null) {
                            Optional<User> userOptional = userRepository.findById(userId);
                            userOptional.ifPresent(user -> {
                                String subject = "Light Scenario Executed";
                                String message = "Light scenario has been executed for device " + device.getName() + ". Current status: Off. Light brightness: " + scenario.getLightBrightness();
                                sendEmailNotification(user.getEmail(), subject, message);
                            });
                        }
                    }

                    // Сохраняем обновленное состояние устройства в базе данных
                    deviceService.updateDevice(device);
                }
            }
        } catch (Exception e) {
            // Обработка ошибок
            e.printStackTrace();
        }
    }
    @GetMapping("/light_scenarios")
    public String getLightScenarios(Model model) {
        List<LightScenario> lightScenarios = scenarioLightService.getLightScenarios();
        model.addAttribute("lightScenarios", lightScenarios);
        return "myscenarios"; // имя вашего шаблона Thymeleaf
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