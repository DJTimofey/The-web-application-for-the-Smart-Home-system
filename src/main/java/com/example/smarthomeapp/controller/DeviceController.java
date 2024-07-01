package com.example.smarthomeapp.controller;

import com.example.smarthomeapp.model.*;
import com.example.smarthomeapp.repository.*;
import com.example.smarthomeapp.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpSession;
import java.net.URI;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/devices")
public class DeviceController {
    @Autowired
    private SensorDataRepository sensorDataRepository;
    @Autowired
    private TemperatureAndHumidityScenarioRepository scenarioRepository;

    @Autowired
    private TemperatureAndHumidityScenarioService temperatureAndHumidityScenarioService;
    @Autowired
    private WaterLeakScenarioRepository waterLeakScenarioRepository;

    @Autowired
    private WaterLeakScenarioService waterLeakScenarioService;
    private final WeatherRepository weatherRepository;
    @Autowired
    private PowerSwitchScenarioService powerSwitchScenarioService;
    @Autowired
    private PowerSwitchScenarioRepository powerSwitchScenarioRepository;
    @Autowired
    private DeviceErrorSimulator deviceErrorSimulator;
    private final LightScenarioRepository lightScenarioRepository;
    @Autowired
    private DeviceService deviceService;
    private final DeviceRepository deviceRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private LightScenarioService scenarioLightService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SensorDataService sensorDataService;
    @Autowired
    private UserService userService;
    @Autowired
    private CurtainScenarioService curtainScenarioService;
    private final CurtainScenarioRepository curtainScenarioRepository;



    @Autowired
    public DeviceController(DeviceRepository deviceRepository, WeatherRepository weatherRepository, LightScenarioRepository lightScenarioRepository, CurtainScenarioRepository curtainScenarioRepository) {

        this.deviceRepository = deviceRepository;

        this.weatherRepository = weatherRepository;
        this.lightScenarioRepository = lightScenarioRepository;
        this.curtainScenarioRepository = curtainScenarioRepository;
    }
    @Autowired
    private MqttService mqttService; // Ваш сервис MQTT



    // Получить все устройства
    @GetMapping
    public String getAllDevices(Model model, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername());
        List<Device> devices = deviceRepository.findByUser(user);
        model.addAttribute("devices", devices);
        return "device";
    }

    // Получить устройство по его ID
    @GetMapping("/{id}")
    public ResponseEntity<Device> getDeviceById(@PathVariable Long id) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found with id: " + id));
        return new ResponseEntity<>(device, HttpStatus.OK);
    }

    // Создать новое устройство
    @PostMapping
    public ResponseEntity<Device> createDevice(@RequestBody Device device) {
        Device createdDevice = deviceRepository.save(device);
        return new ResponseEntity<>(createdDevice, HttpStatus.CREATED);
    }

    // Обновить информацию об устройстве
    @PutMapping("/{id}")
    public ResponseEntity<Device> updateDevice(@PathVariable Long id, @RequestBody Device deviceDetails) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found with id: " + id));

        device.setName(deviceDetails.getName());
        device.setType(deviceDetails.getType());
        device.setStatus(deviceDetails.getStatus());
        device.setUser(deviceDetails.getUser());

        Device updatedDevice = deviceRepository.save(device);
        return new ResponseEntity<>(updatedDevice, HttpStatus.OK);
    }


    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String message) {
            super(message);
        }
    }
    @GetMapping("/device")
    public String devicePage(Model model) {
        List<Device> devices = deviceRepository.findAll();
        List<Room> rooms = roomRepository.findAll();

        model.addAttribute("devices", devices);
        model.addAttribute("rooms", rooms);

        return "device";
    }

    @ModelAttribute("sensorDataList")
    public List<WeatherData> populateSensorDataList() {
        return sensorDataService.getAllSensorData();
    }
    @PostMapping("/add")
    public String addDevice(@RequestParam("name") String name,
                            @RequestParam("type") String type,
                            @RequestParam("roomId") Long roomId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Long userId = userService.findByUsername(userDetails.getUsername()).getId();

            Device device = new Device();
            device.setName(name);
            device.setType(type);

            // Установка комнаты для устройства
            Room room = roomRepository.findById(roomId)
                    .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + roomId));
            device.setRoom(room);

            // Установка пользователя для устройства
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
            device.setUser(user);

            deviceRepository.save(device);

            // Отправка сообщения на брокер MQTT
            String topic = "topic/add_device"; // Задайте нужный топик
            String message = "New device added: " + name;
            mqttService.sendMessage(topic, message); // Вызов метода для отправки сообщения

            // После успешного добавления устройства выполняем редирект на страницу с устройствами
            return "redirect:/devices/search?roomId=";
        } catch (Exception e) {
            // Обработка ошибки, если не удалось добавить устройство
            return "error"; // Например, перенаправление на страницу с сообщением об ошибке
        }
    }
    // Метод для получения всех устройств из базы данных
    @GetMapping("/all")
    public String getAllDevices(Model model) {
        List<Device> devices = deviceRepository.findAll();
        model.addAttribute("devices", devices);
        return "device"; // Имя представления для отображения устройств
    }
    // Метод для переключения статуса устройства
    @PostMapping("/{id}/toggle")
    public String toggleDevice(@PathVariable Long id) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found with id: " + id));

        // Изменяем статус устройства на противоположный
        device.setStatus(device.getStatus() == DeviceStatus.On ? DeviceStatus.Off : DeviceStatus.On);
        deviceRepository.save(device);

        // Определяем topic для управления устройством
        String topic = "devices/" + id + "/command";

        // Определяем message в зависимости от статуса устройства
        String message = device.getStatus() == DeviceStatus.On ? "On" : "Off";

        // Отправляем сообщение на брокер MQTT
        mqttService.sendMessage(topic, message);

        // Возвращаем строку с URL перенаправления на страницу устройств
        return "redirect:/devices/search?roomId=";
    }
    @PostMapping("/{id}/simulate-error")
    public String simulateDeviceErrorAndTurnOff(@PathVariable("id") Long deviceId) {
        deviceErrorSimulator.simulateDeviceErrorAndTurnOff(deviceId);
        return "redirect:/devices/device";
    }

    @PostMapping("/{id}/delete")
    public String deleteDevice(@PathVariable("id") Long deviceId) {
        Device deviceToDelete = deviceService.findDeviceById(deviceId); // Находим устройство по его идентификатору
        if (deviceToDelete != null) {
            deviceService.deleteDeviceById(deviceId); // Удаляем устройство из базы данных
            // Устройство успешно удалено, отправляем сообщение на брокер MQTT
            String topic = "topic/delete_device"; // Задайте нужный топик
            String message = "Device deleted: " + deviceToDelete.getName();
            mqttService.sendMessage(topic, message); // Вызов метода для отправки сообщения
        } else {
            // Обработка случая, если устройство не найдено
            // Можно вывести сообщение об ошибке или выполнить другие действия по вашему усмотрению
        }

        return "redirect:/devices"; // Можете указать любой URL для перенаправления после удаления
    }
    @GetMapping("/filter")
    public ResponseEntity<List<Device>> filterDevicesByRoom(@RequestParam("roomId") Long roomId) {
        List<Device> devicesInRoom = deviceService.findDevicesByRoomId(roomId); // Замените на ваш метод сервиса для поиска устройств по комнате
        return new ResponseEntity<>(devicesInRoom, HttpStatus.OK);
    }
    @GetMapping("/search")
    public String searchDevicesByRoom(@RequestParam(value = "roomId", required = false) Long roomId, Model model, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername());
        List<Device> devicesInRoom;
        List<Room> rooms = roomRepository.findAll(); // Получаем все комнаты пользователя

        if (roomId != null) {
            // Проверяем, принадлежит ли комната текущему пользователю
            Room room = roomRepository.findById(roomId)
                    .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + roomId));

            // Получаем устройства в этой комнате
            devicesInRoom = deviceRepository.findByRoomId(roomId);

            // Фильтруем устройства, чтобы оставить только те, которые принадлежат текущему пользователю
            devicesInRoom = devicesInRoom.stream()
                    .filter(device -> device.getUser().equals(user))
                    .collect(Collectors.toList());
        } else {
            // Если комната не выбрана, получаем все устройства текущего пользователя
            devicesInRoom = deviceRepository.findByUser(user);
        }

        model.addAttribute("devices", devicesInRoom);
        model.addAttribute("rooms", rooms);
        return "device";
    }

    @GetMapping("/sensorTaH")
    @ResponseBody
    public List<Device> getSensorTaHDevices(@AuthenticationPrincipal UserDetails userDetails) {
        User user = getUserFromUserDetails(userDetails);
        if (user != null) {
            return deviceRepository.findByUserAndType(user, "sensorTaH");
        } else {
            // Обработка ситуации, если не удалось получить пользователя
            return Collections.emptyList(); // Например, возвращаем пустой список
        }
    }
    private User getUserFromUserDetails(UserDetails userDetails) {
        if (userDetails instanceof User) {
            return (User) userDetails;
        } else {
            String username = userDetails.getUsername();
            return userRepository.findByUsername(username);
        }
    }

    @GetMapping("/water leak sensor")
    @ResponseBody
    public List<Device> getWaterLeakSensorDevices(@AuthenticationPrincipal UserDetails userDetails) {
        User user = getUserFromUserDetails(userDetails);
        if (user != null) {
            return deviceRepository.findByUserAndType(user, "water leak sensor");
        } else {
            return Collections.emptyList(); // Обработка ситуации, если не удалось получить пользователя
        }
    }

    @GetMapping("/light")
    @ResponseBody
    public List<Device> getLightDevices(@AuthenticationPrincipal UserDetails userDetails) {
        User user = getUserFromUserDetails(userDetails);
        if (user != null) {
            return deviceRepository.findByUserAndType(user, "light");
        } else {
            return Collections.emptyList(); // Обработка ситуации, если не удалось получить пользователя
        }
    }

    @GetMapping("/power switch")
    @ResponseBody
    public List<Device> getPowerSwitchDevices(@AuthenticationPrincipal UserDetails userDetails) {
        User user = getUserFromUserDetails(userDetails);
        if (user != null) {
            return deviceRepository.findByUserAndType(user, "power switch");
        } else {
            return Collections.emptyList(); // Обработка ситуации, если не удалось получить пользователя
        }
    }

    @GetMapping("/curtains")
    @ResponseBody
    public List<Device> getCurtainsDevices(@AuthenticationPrincipal UserDetails userDetails) {
        User user = getUserFromUserDetails(userDetails);
        if (user != null) {
            return deviceRepository.findByUserAndType(user, "curtains");
        } else {
            return Collections.emptyList(); // Обработка ситуации, если не удалось получить пользователя
        }
    }
    @PostMapping("temp/{id}/delete")
    public String deleteTemperatureAndHumidityScenario(@PathVariable("id") Long scenarioId) {
        TemperatureAndHumidityScenario scenarioToDelete = temperatureAndHumidityScenarioService.findScenarioById(scenarioId);
        if (scenarioToDelete != null) {
            temperatureAndHumidityScenarioService.deleteScenarioById(scenarioId);
            // Send a message to the MQTT broker
            String topic = "topic/delete_scenario";
            String message = "Scenario deleted: " + scenarioId;
            mqttService.sendMessage(topic, message);
        }
        return "redirect:/devices/myscenarios"; // Перенаправление на страницу сценариев устройства
    }

    @PostMapping("waterleakscenarios/{id}/delete")
    public String deleteWaterLeakScenario(@PathVariable("id") Long scenarioId, HttpSession session) {
        WaterLeakScenario scenarioToDelete = waterLeakScenarioService.findScenarioById(scenarioId);
        if (scenarioToDelete != null) {
            waterLeakScenarioService.deleteScenarioById(scenarioId);
            // Отправляем сообщение на брокер MQTT
            String topic = "topic/delete_scenario";
            String message = "Water leak scenario deleted: " + scenarioId;
            mqttService.sendMessage(topic, message);
        }

        // Получаем сохраненный URL из сессии
        String previousPage = (String) session.getAttribute("previousPage");
        if (previousPage != null) {
            return "redirect:" + previousPage;
        } else {
            // Если URL не был сохранен в сессии, перенаправляем на страницу по умолчанию
            return "redirect:/devices/myscenarios";
        }
    }
    // Метод удаления сценария включения и выключения света
    @PostMapping("lightscenarios/{id}/delete")
    public String deleteLightScenario(@PathVariable("id") Long scenarioId, HttpSession session) {
        LightScenario scenarioToDelete = scenarioLightService.findScenarioById(scenarioId);
        if (scenarioToDelete != null) {
            scenarioLightService.deleteScenarioById(scenarioId);
            // Отправляем сообщение на брокер MQTT
            String topic = "topic/delete_scenario";
            String message = "Light scenario deleted: " + scenarioId;
            mqttService.sendMessage(topic, message);
        }

        return "redirect:/devices/myscenarios";
    }

    // Метод удаления сценария управления шторами
    @PostMapping("curtainscenarios/delete/{id}")
    public String deleteCurtainScenario(@PathVariable("id") Long scenarioId, HttpSession session) {
        CurtainScenario scenarioToDelete = curtainScenarioService.findScenarioById(scenarioId);
        if (scenarioToDelete != null) {
            curtainScenarioService.deleteScenarioById(scenarioId);
            // Отправляем сообщение на брокер MQTT
            String topic = "topic/delete_scenario";
            String message = "Curtain scenario deleted: " + scenarioId;
            mqttService.sendMessage(topic, message);
        }

        // Получаем сохраненный URL из сессии
        String previousPage = (String) session.getAttribute("previousPage");
        if (previousPage != null) {
            return "redirect:" + previousPage;
        } else {
            // Если URL не был сохранен в сессии, перенаправляем на страницу по умолчанию
            return "redirect:/devices/myscenarios";
        }
    }
    @PostMapping("power_switch_scenarios/{id}/delete")
    public String deletePowerSwitchScenario(@PathVariable("id") Long scenarioId) {
        PowerSwitchScenario scenarioToDelete = powerSwitchScenarioService.findScenarioById(scenarioId);
        if (scenarioToDelete != null) {
            powerSwitchScenarioService.deleteScenarioById(scenarioId);
            // Send a message to the MQTT broker
            String topic = "topic/delete_scenario";
            String message = "Power switch scenario deleted: " + scenarioId;
            mqttService.sendMessage(topic, message);
        }
        return "redirect:/devices/myscenarios"; // Перенаправление на страницу сценариев устройства
    }


    @GetMapping("/myscenarios")
    public String showMyScenariosPage(Model model, Authentication authentication) {
        User currentUser = getCurrentUser(authentication);
        if (currentUser == null) {
            return "/login"; // Если пользователь не авторизован, перенаправляем на страницу логина
        }

        // Получение сценариев для температуры и влажности, относящихся только к текущему пользователю
        List<TemperatureAndHumidityScenario> scenarios = temperatureAndHumidityScenarioService.getScenariosByUserId(currentUser.getId());
        model.addAttribute("scenarios", scenarios);

        // Получение сценариев для штор, относящихся только к текущему пользователю
        List<CurtainScenario> curtainScenarios = curtainScenarioService.getScenariosByUserId(currentUser.getId());
        model.addAttribute("curtainScenarios", curtainScenarios);

        // Получение сценариев для обнаружения протечки воды, относящихся только к текущему пользователю
        List<WaterLeakScenario> waterLeakScenarios = waterLeakScenarioService.getWaterLeakScenariosByUserId(currentUser.getId());
        model.addAttribute("waterLeakScenarios", waterLeakScenarios);

        // Получение сценариев для управления освещением, относящихся только к текущему пользователю
        List<LightScenario> lightScenarios = scenarioLightService.getScenariosByUserId(currentUser.getId());
        model.addAttribute("lightScenarios", lightScenarios);

        // Получение сценариев для управления выключателями, относящихся только к текущему пользователю
        List<PowerSwitchScenario> powerSwitchScenarios = powerSwitchScenarioService.getScenariosByUserId(currentUser.getId());
        model.addAttribute("powerSwitchScenarios", powerSwitchScenarios);

        return "myscenarios"; // возвращаем имя шаблона (view), который нужно отобразить
    }
    @PostMapping("/{id}/setBrightness")
    public String setBrightness(@PathVariable Long id, @RequestParam Integer brightness) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found with id: " + id));

        if (device.getType().equals("light")) {
            device.setBrightness(brightness);
            deviceRepository.save(device);
        }

        return "redirect:/devices/device"; // Redirect to the device page
    }
    @GetMapping("/sensor_data_history")
    public String showSensorDataHistory(Model model, Authentication authentication) {
        User currentUser = getCurrentUser(authentication);

        if (currentUser == null) {
            return "/login"; // Если пользователь не авторизован, перенаправляем на страницу логина
        }

        List<WeatherData> sensorDataList = sensorDataRepository.findSensorDataForLastMonth();

        // Для каждой записи данных о погоде получаем информацию о соответствующем пользователе
        for (WeatherData data : sensorDataList) {
            // Получаем информацию о пользователе по user_id из таблицы devices
            Map<String, Object> userData = sensorDataRepository.findUserDataByDeviceId(data.getDeviceId());
            if (userData != null) {
                data.setUserName((String) userData.get("userName"));
                data.setUserPhone((String) userData.get("userPhone"));
                data.setUserAddress((String) userData.get("userAddress"));
            }
        }

        // Фильтруем список sensorDataList, оставляя только записи для устройств текущего пользователя
        sensorDataList = sensorDataList.stream()
                .filter(data -> {
                    Long userId = data.getUserId(); // Получаем userId из данных погоды
                    return userId != null && userId.equals(currentUser.getId()); // Оставляем только записи для текущего пользователя
                })
                .collect(Collectors.toList());

        model.addAttribute("sensorDataList", sensorDataList);

        if ("USER".equals(currentUser.getRole())) {
            // Если пользователь - администратор, отображаем данные для администратора
            List<Device> waterLeakSensorDataList = deviceRepository.findByType("water leak sensor");

            // Фильтруем список waterLeakSensorDataList, оставляя только записи для устройств текущего пользователя
            waterLeakSensorDataList = waterLeakSensorDataList.stream()
                    .filter(device -> {
                        Long userId = device.getUserId(); // Получаем userId из данных устройства
                        return userId != null && userId.equals(currentUser.getId()); // Оставляем только записи для текущего пользователя
                    })
                    .collect(Collectors.toList());

            for (Device device : waterLeakSensorDataList) {
                Map<String, String> userData = deviceRepository.findUserDataByDeviceId(device.getId());
                if (userData != null) {
                    device.setUserAddress(userData.get("address"));
                    device.setUserPhone(userData.get("phone"));
                    device.setUserName(userData.get("username"));
                }
            }

            model.addAttribute("waterLeakSensorData", waterLeakSensorDataList);
        } else if ("OPERATOR".equals(currentUser.getRole())) {
            // Если пользователь - оператор, отображаем все записи для устройств всех пользователей
            List<Device> allWaterLeakSensorDataList = deviceRepository.findByType("water leak sensor");

            for (Device device : allWaterLeakSensorDataList) {
                Map<String, String> userData = deviceRepository.findUserDataByDeviceId(device.getId());
                if (userData != null) {
                    device.setUserAddress(userData.get("address"));
                    device.setUserPhone(userData.get("phone"));
                    device.setUserName(userData.get("username"));
                }
            }

            model.addAttribute("waterLeakSensorData", allWaterLeakSensorDataList);
            List<WeatherData> allSensorDataList = sensorDataRepository.findSensorDataForLastMonth();

            // Для каждой записи данных о погоде получаем информацию о соответствующем пользователе
            for (WeatherData data : allSensorDataList) {
                // Получаем информацию о пользователе по user_id из таблицы devices
                Map<String, Object> userData = sensorDataRepository.findUserDataByDeviceId(data.getDeviceId());
                if (userData != null) {
                    data.setUserName((String) userData.get("userName"));
                    data.setUserPhone((String) userData.get("userPhone"));
                    data.setUserAddress((String) userData.get("userAddress"));
                }
            }

            model.addAttribute("sensorDataList", allSensorDataList);
        }


        return "sensor_data_history";
    }
    private User getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userRepository.findByUsername(userDetails.getUsername());
    }
    @GetMapping("/sensor-data/{deviceId}")
    public ResponseEntity<List<WeatherData>> getSensorDataByDeviceId(@PathVariable Long deviceId) {
        List<WeatherData> sensorDataList = sensorDataRepository.findByDeviceId(deviceId);
        return ResponseEntity.ok(sensorDataList);
    }


    @PostMapping("/temp/{id}/edit")
    public RedirectView editScenario(@PathVariable Long id, @RequestParam Map<String,String> updatedScenarioData) {
        Optional<TemperatureAndHumidityScenario> scenarioOptional = scenarioRepository.findById(id);
        if (scenarioOptional.isPresent()) {
            TemperatureAndHumidityScenario existingScenario = scenarioOptional.get();
            // Обновляем только те поля, которые были переданы в запросе
            if (updatedScenarioData.containsKey("scenarioName")) {
                existingScenario.setScenarioName(updatedScenarioData.get("scenarioName"));
            }
            if (updatedScenarioData.containsKey("minTemperature")) {
                existingScenario.setMinTemperature(Double.parseDouble(updatedScenarioData.get("minTemperature")));
            }
            if (updatedScenarioData.containsKey("maxTemperature")) {
                existingScenario.setMaxTemperature(Double.parseDouble(updatedScenarioData.get("maxTemperature")));
            }
            if (updatedScenarioData.containsKey("minHumidity")) {
                existingScenario.setMinHumidity(Double.parseDouble(updatedScenarioData.get("minHumidity")));
            }
            if (updatedScenarioData.containsKey("maxHumidity")) {
                existingScenario.setMaxHumidity(Double.parseDouble(updatedScenarioData.get("maxHumidity")));
            }
            if (updatedScenarioData.containsKey("deviceName")) {
                existingScenario.setDeviceName(updatedScenarioData.get("deviceName"));
            }
            // Сохраняем обновленный сценарий в базе данных
            scenarioRepository.save(existingScenario);
            // Создаем объект RedirectView для перенаправления на нужный URL
            return new RedirectView("/devices/myscenarios");
        } else {
            // Возвращаем ошибку 404, если сценарий не найден
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Сценарий с id " + id + " не найден");
        }
    }
    @PostMapping("/power_switch_scenarios/{id}/edit")
    public ResponseEntity<?> editPowerSwitchScenario(@PathVariable Long id, @RequestParam Map<String, String> updatedScenarioData) throws ParseException {
        Optional<PowerSwitchScenario> scenarioOptional = powerSwitchScenarioRepository.findById(id);
        if (scenarioOptional.isPresent()) {
            PowerSwitchScenario existingScenario = scenarioOptional.get();
            // Обновляем только те поля, которые были переданы в запросе
            if (updatedScenarioData.containsKey("scenarioName")) {
                existingScenario.setScenarioName(updatedScenarioData.get("scenarioName"));
            }
            if (updatedScenarioData.containsKey("actionSwitch")) {
                existingScenario.setActionSwitch(updatedScenarioData.get("actionSwitch"));
            }
            if (updatedScenarioData.containsKey("switchTime") && updatedScenarioData.get("switchTime") != null) {
                // Преобразование строки времени в объект Time
                String switchTimeString = updatedScenarioData.get("switchTime") + ":00"; // Add seconds to the time string
                Time switchTime = Time.valueOf(switchTimeString);
                existingScenario.setSwitchTime(switchTime);
            }
            if (updatedScenarioData.containsKey("deviceName")) {
                existingScenario.setDeviceName(updatedScenarioData.get("deviceName"));
            }
            // Сохраняем обновленный сценарий в базе данных
            powerSwitchScenarioRepository.save(existingScenario);

            // Redirect back to the page devices/myscenarios
            URI location = URI.create("/devices/myscenarios");
            return ResponseEntity
                    .status(HttpStatus.FOUND)
                    .location(location)
                    .build();
        } else {
            // Возвращаем ошибку 404, если сценарий не найден
            return ResponseEntity.notFound().build();
        }
    }
    @PostMapping("/curtainscenarios/{id}/edit")
    public ResponseEntity<?> editCurtainScenario(@PathVariable Long id, @RequestParam Map<String, String> updatedScenarioData) throws ParseException {
        Optional<CurtainScenario> scenarioOptional = curtainScenarioRepository.findById(id);
        if (scenarioOptional.isPresent()) {
            CurtainScenario existingScenario = scenarioOptional.get();
            // Обновляем только те поля, которые были переданы в запросе
            if (updatedScenarioData.containsKey("scenarioName")) {
                existingScenario.setScenarioName(updatedScenarioData.get("scenarioName"));
            }

            if (updatedScenarioData.containsKey("openTime") && updatedScenarioData.get("openTime") != null) {
                String openTimeString = updatedScenarioData.get("openTime");
                // Разбиваем строку времени на часы и минуты
                String[] openTimeParts = openTimeString.split(":");
                int openHours = Integer.parseInt(openTimeParts[0]);
                int openMinutes = Integer.parseInt(openTimeParts[1]);
                // Создаем объект Time
                Time openTime = new Time(openHours, openMinutes, 0); // Секунды устанавливаем в 0
                existingScenario.setOpenTime(openTime);
            }

            if (updatedScenarioData.containsKey("closeTime") && updatedScenarioData.get("closeTime") != null) {
                String closeTimeString = updatedScenarioData.get("closeTime");
                // Разбиваем строку времени на часы и минуты
                String[] closeTimeParts = closeTimeString.split(":");
                int closeHours = Integer.parseInt(closeTimeParts[0]);
                int closeMinutes = Integer.parseInt(closeTimeParts[1]);
                // Создаем объект Time
                Time closeTime = new Time(closeHours, closeMinutes, 0); // Секунды устанавливаем в 0
                existingScenario.setCloseTime(closeTime);
            }

            if (updatedScenarioData.containsKey("deviceName")) {
                existingScenario.setDeviceName(updatedScenarioData.get("deviceName"));
            }
            // Сохраняем обновленный сценарий в базе данных
            curtainScenarioRepository.save(existingScenario);
            // Redirect back to the page devices/myscenarios
            return ResponseEntity
                    .status(HttpStatus.FOUND)
                    .location(URI.create("/devices/myscenarios"))
                    .build();
        } else {
            // Возвращаем ошибку 404, если сценарий не найден
            return ResponseEntity.notFound().build();
        }
    }
    @PostMapping("/lightscenarios/{id}/edit")
    public ResponseEntity<?> editLightScenario(@PathVariable Long id, @RequestParam Map<String, String> updatedScenarioData) throws ParseException {
        Optional<LightScenario> scenarioOptional = lightScenarioRepository.findById(id);
        if (scenarioOptional.isPresent()) {
            LightScenario existingScenario = scenarioOptional.get();
            // Обновляем только те поля, которые были переданы в запросе
            if (updatedScenarioData.containsKey("scenarioName")) {
                existingScenario.setScenarioName(updatedScenarioData.get("scenarioName"));
            }

            if (updatedScenarioData.containsKey("lightSwitch")) {
                existingScenario.setLightSwitch(updatedScenarioData.get("lightSwitch"));
            }

            if (updatedScenarioData.containsKey("lightTime")) {
                String lightTimeString = updatedScenarioData.get("lightTime");
                // Преобразуем строку времени в объект Time
                SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
                Date parsedDate = dateFormat.parse(lightTimeString);
                Time lightTime = new Time(parsedDate.getTime());
                existingScenario.setLightTime(lightTime);
            }

            if (updatedScenarioData.containsKey("lightBrightness")) {
                existingScenario.setLightBrightness(Integer.parseInt(updatedScenarioData.get("lightBrightness")));
            }

            if (updatedScenarioData.containsKey("deviceName")) {
                existingScenario.setDeviceName(updatedScenarioData.get("deviceName"));
            }

            // Сохраняем обновленный сценарий в базе данных
            lightScenarioRepository.save(existingScenario);

            // Redirect back to the page devices/myscenarios
            return ResponseEntity
                    .status(HttpStatus.FOUND)
                    .location(URI.create("/devices/myscenarios"))
                    .build();
        } else {
            // Возвращаем ошибку 404, если сценарий не найден
            return ResponseEntity.notFound().build();
        }
    }
    @PostMapping("/waterleakscenarios/{id}/edit")
    public ResponseEntity<?> editWaterLeakScenario(@PathVariable Long id, @RequestParam Map<String, String> updatedScenarioData) throws ParseException {
        Optional<WaterLeakScenario> scenarioOptional = waterLeakScenarioRepository.findById(id);
        if (scenarioOptional.isPresent()) {
            WaterLeakScenario existingScenario = scenarioOptional.get();
            // Обновляем только те поля, которые были переданы в запросе
            if (updatedScenarioData.containsKey("scenarioName")) {
                existingScenario.setScenarioName(updatedScenarioData.get("scenarioName"));
            }

            if (updatedScenarioData.containsKey("deviceName")) {
                existingScenario.setDeviceName(updatedScenarioData.get("deviceName"));
            }

            // Сохраняем обновленный сценарий в базе данных
            waterLeakScenarioRepository.save(existingScenario);

            // Redirect back to the page devices/myscenarios
            return ResponseEntity
                    .status(HttpStatus.FOUND)
                    .location(URI.create("/devices/myscenarios"))
                    .build();
        } else {
            // Возвращаем ошибку 404, если сценарий не найден
            return ResponseEntity.notFound().build();
        }
    }

}






