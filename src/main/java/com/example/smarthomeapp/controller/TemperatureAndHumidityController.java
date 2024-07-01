package com.example.smarthomeapp.controller;

import com.example.smarthomeapp.model.Device;
import com.example.smarthomeapp.model.DeviceStatus;
import com.example.smarthomeapp.model.TemperatureAndHumidityScenario;
import com.example.smarthomeapp.service.DeviceService;
import com.example.smarthomeapp.service.MqttService;
import com.example.smarthomeapp.service.TemperatureAndHumidityScenarioService;
import com.example.smarthomeapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/temperatureandhumidityscenarios")
public class TemperatureAndHumidityController {
    @Autowired
    private MqttService mqttService; // Ваш сервис MQTT

    @Autowired
    private UserService userService;

    @Autowired
    private TemperatureAndHumidityScenarioService scenarioService;

    @Autowired
    private DeviceService deviceService; // Ваш сервис для работы с устройствами

    @PostMapping("/add")
    public String addScenario(@RequestParam("scenarioName") String scenarioName,
                              @RequestParam("minTemperature") double minTemperature,
                              @RequestParam("maxTemperature") double maxTemperature,
                              @RequestParam("minHumidity") double minHumidity,
                              @RequestParam("maxHumidity") double maxHumidity,
                              @RequestParam("selectedDevices") List<Long> selectedDevices) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Long userId = userService.findByUsername(userDetails.getUsername()).getId();

            for (Long deviceId : selectedDevices) {
                // Получаем данные об устройстве из базы данных
                Device device = deviceService.getDeviceById(deviceId);

                if (device != null) {
                    // Создаем новый сценарий для каждого выбранного устройства
                    TemperatureAndHumidityScenario scenario = new TemperatureAndHumidityScenario();
                    scenario.setUserId(userId);
                    scenario.setScenarioName(scenarioName);
                    scenario.setMinTemperature(minTemperature);
                    scenario.setMaxTemperature(maxTemperature);
                    scenario.setMinHumidity(minHumidity);
                    scenario.setMaxHumidity(maxHumidity);
                    scenario.setDeviceId(deviceId); // Устанавливаем идентификатор устройства
                    scenario.setDeviceName(device.getName()); // Устанавливаем имя устройства
                    scenario.setDeviceType(device.getType()); // Устанавливаем тип устройства
                    scenario.setCreatedDate(new Date());

                    // Сохраняем сценарий для текущего устройства
                    scenarioService.addScenario(scenario);
                } else {
                    // Обработка случая, если устройство не найдено в базе данных
                    return "error"; // Например, перенаправление на страницу с сообщением об ошибке
                }
            }
            // Send a message to the MQTT broker
            String topic = "topic/add_scenario";
            String message = "New scenario added: " + scenarioName;
            mqttService.sendMessage(topic, message);
            // После успешного добавления всех сценариев выполняем редирект на определенную страницу
            return "redirect:/device"; // Замените "/dashboard" на ваш URL для успешного добавления сценариев
        } catch (Exception e) {
            // Обработка ошибки, если не удалось добавить сценарии
            return "error"; // Например, перенаправление на страницу с сообщением об ошибке
        }
    }
    // Метод для получения всех сценариев в формате JSON
    @GetMapping("/all")
    public String showAllScenarios(Model model) {
        List<TemperatureAndHumidityScenario> scenarios = scenarioService.getAllScenarios();
        model.addAttribute("scenarios", scenarios);
        return "myscenarios"; // Имя вашего Thymeleaf шаблона
    }
    @GetMapping("/myscenarios")
    public String showMyScenariosPage(Model model) {
        List<TemperatureAndHumidityScenario> scenarios = scenarioService.getAllScenarios();
        model.addAttribute("scenarios", scenarios);
        return "myscenarios";
    }

}