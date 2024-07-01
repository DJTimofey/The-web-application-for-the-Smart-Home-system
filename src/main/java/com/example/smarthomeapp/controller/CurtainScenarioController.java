package com.example.smarthomeapp.controller;

import com.example.smarthomeapp.model.CurtainScenario;
import com.example.smarthomeapp.model.Device;
import com.example.smarthomeapp.model.DeviceStatus;
import com.example.smarthomeapp.model.User;
import com.example.smarthomeapp.repository.UserRepository;
import com.example.smarthomeapp.service.CurtainScenarioService;
import com.example.smarthomeapp.service.DeviceService;
import com.example.smarthomeapp.service.MqttService;
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
@RequestMapping("/curtain_scenarios")
public class CurtainScenarioController {

    @Autowired
    private CurtainScenarioService curtainScenarioService;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private UserService userService;
    private final JavaMailSender javaMailSender;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MqttService mqttService;

    public CurtainScenarioController(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @PostMapping("/add")
    public String addScenario(@RequestParam("scenarioName") String scenarioName,
                              @RequestParam("selectedDevices") List<Long> selectedDevices,
                              @RequestParam("openTime") String openTime,
                              @RequestParam("closeTime") String closeTime) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Long userId = userService.findByUsername(userDetails.getUsername()).getId();

            for (Long deviceId : selectedDevices) {
                Device device = deviceService.getDeviceById(deviceId);

                if (device != null) {
                    CurtainScenario scenario = new CurtainScenario();
                    scenario.setScenarioName(scenarioName);
                    scenario.setUserId(userId);
                    scenario.setOpenTime(Time.valueOf(LocalTime.parse(openTime)));
                    scenario.setCloseTime(Time.valueOf(LocalTime.parse(closeTime)));
                    scenario.setCreatedAt(new Timestamp(System.currentTimeMillis()));
                    scenario.setDeviceId(deviceId);
                    scenario.setDeviceName(device.getName());
                    scenario.setDeviceType(device.getType());
                    curtainScenarioService.addScenario(scenario);
                } else {
                    return "error";
                }
            }

            String topic = "topic/add_curtain_scenario";
            String message = "New curtain scenario added: " + scenarioName;
            mqttService.sendMessage(topic, message);

            return "success";
        } catch (Exception e) {
            return "error";
        }
    }
    @Scheduled(fixedRate = 60000) // Check every minute
    public void executeCurtainScenarios() {
        try {
            LocalTime currentTime = LocalTime.now().truncatedTo(ChronoUnit.MINUTES);
            List<CurtainScenario> activeScenarios = curtainScenarioService.getScenariosByTime(currentTime);

            for (CurtainScenario scenario : activeScenarios) {
                LocalTime openTime = scenario.getOpenTime().toLocalTime();

                Device device = deviceService.getDeviceById(scenario.getDeviceId());

                if (device != null) {
                    DeviceStatus desiredStatus = currentTime.equals(openTime) ? DeviceStatus.On : DeviceStatus.Off;

                    if (device.getStatus() != desiredStatus) {
                        device.setStatus(desiredStatus);
                        deviceService.updateDevice(device);

                        // Отправляем уведомление на почту
                        Long userId = device.getUserId();
                        if (userId != null) {
                            Optional<User> userOptional = userRepository.findById(userId);
                            userOptional.ifPresent(user -> {
                                String subject = "Curtain Scenario Executed";
                                String message = "Curtain scenario has been executed for device " + device.getName() + ". Current status: " + desiredStatus;
                                sendEmailNotification(user.getEmail(), subject, message);
                            });
                        }
                    }
                }
            }
        } catch (Exception e) {
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