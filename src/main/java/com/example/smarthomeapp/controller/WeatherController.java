package com.example.smarthomeapp.controller;





import com.example.smarthomeapp.model.AverageDailyTemperatureModul;
import com.example.smarthomeapp.model.DateFormat;
import com.example.smarthomeapp.model.Weather;
import com.example.smarthomeapp.model.WeatherData;
import com.example.smarthomeapp.repository.SensorDataRepository;
import com.example.smarthomeapp.repository.WeatherRepository;
import com.example.smarthomeapp.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("api/v1/weather")
public class WeatherController {
    @Autowired
    private SensorDataRepository sensorDataRepository;

    private final WeatherService weatherService;
    private final WeatherRepository weatherRepository;
    private LocalDateTime lastWeatherSaveTime; // Время последнего сохранения данных о погоде

    @Autowired
    public WeatherController(WeatherService weatherService, WeatherRepository weatherRepository) {
        this.weatherService = weatherService;
        this.weatherRepository = weatherRepository;
        this.lastWeatherSaveTime = LocalDateTime.now(); // Инициализируем время при запуске контроллера
    }

    @GetMapping("/saveWeatherHourly")
    public ResponseEntity<String> saveWeatherHourly() {
        LocalDateTime currentTime = LocalDateTime.now();

        // Проверяем, прошел ли уже час с момента последнего сохранения данных о погоде
        if (currentTime.minusHours(1).isAfter(lastWeatherSaveTime)) {
            // Сохраняем данные о погоде
            weatherService.saveWeatherHourly();
            lastWeatherSaveTime = currentTime; // Обновляем время последнего сохранения
            return new ResponseEntity<>("Weather data saved successfully!", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Weather data already saved within the last hour!", HttpStatus.OK);
        }
    }


}
