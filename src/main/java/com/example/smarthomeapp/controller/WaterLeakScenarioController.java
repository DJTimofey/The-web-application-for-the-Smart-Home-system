package com.example.smarthomeapp.controller;

import com.example.smarthomeapp.model.Device;
import com.example.smarthomeapp.model.WaterLeakScenario;
import com.example.smarthomeapp.service.DeviceService;
import com.example.smarthomeapp.service.MqttService;
import com.example.smarthomeapp.service.UserService;
import com.example.smarthomeapp.service.WaterLeakScenarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/water_leak_scenarios")
public class WaterLeakScenarioController {
    @Autowired
    private MqttService mqttService;

    @Autowired
    private UserService userService;

    @Autowired
    private WaterLeakScenarioService scenarioService;

    @Autowired
    private DeviceService deviceService;

    @PostMapping("/add")
    public String addScenario(@RequestParam("scenarioName") String scenarioName,
                              @RequestParam("selectedDevices") List<Long> selectedDevices) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Long userId = userService.findByUsername(userDetails.getUsername()).getId();

            for (Long deviceId : selectedDevices) {
                Device device = deviceService.getDeviceById(deviceId);

                if (device != null) {
                    WaterLeakScenario scenario = new WaterLeakScenario();
                    scenario.setUserId(userId);
                    scenario.setScenarioName(scenarioName);
                    scenario.setWaterLeakDetection("Сухой");
                    scenario.setDeviceId(deviceId);
                    scenario.setDeviceName(device.getName());
                    scenario.setDeviceType(device.getType());
                    scenario.setCreatedDate(new Date());

                    scenarioService.addScenario(scenario);
                } else {
                    return "error";
                }
            }

            String topic = "topic/add_scenario";
            String message = "New scenario added: " + scenarioName;
            mqttService.sendMessage(topic, message);

            return "redirect:/device";
        } catch (Exception e) {
            return "error";
        }
    }

    // Добавьте методы для отображения и удаления сценариев аналогично примеру для датчиков температуры и влажности
}