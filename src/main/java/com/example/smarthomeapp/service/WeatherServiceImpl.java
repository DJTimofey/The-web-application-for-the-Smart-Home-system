package com.example.smarthomeapp.service;

import com.example.smarthomeapp.exceptions.InvalidDateException;
import com.example.smarthomeapp.exceptions.WeatherAPIException;
import com.example.smarthomeapp.exceptions.WeatherDataNotFoundException;
import com.example.smarthomeapp.model.*;
import com.example.smarthomeapp.repository.DeviceRepository;
import com.example.smarthomeapp.repository.WeatherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.json.Json;
import javax.json.JsonObject;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@EnableScheduling
public class WeatherServiceImpl implements WeatherService {
    private final DeviceRepository deviceRepository;

    private final WeatherRepository weatherRepository;

    @Autowired
    public WeatherServiceImpl(DeviceRepository deviceRepository, WeatherRepository weatherRepository) {
        this.deviceRepository = deviceRepository;
        this.weatherRepository = weatherRepository;
    }


    @Override
    public Weather getCurrentWeather() {
        WeatherData weatherData = weatherRepository.findFirstByOrderByTimestampDesc()
                .orElseThrow(() -> new WeatherDataNotFoundException("There are no weather records in the database."));
        return new Weather(weatherData.getLocation(),
                weatherData.getTemperature(),
                weatherData.getWindMetersPerHour(),
                weatherData.getPressureMB(),
                weatherData.getHumidity(),
                weatherData.getWeatherCondition());
    }

    @Override
    public AverageDailyTemperatureModul getAverageDailyTemperature() {

        LocalDate today = LocalDate.now();
        Instant from = today.atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant to = today.atStartOfDay()
                .toInstant(ZoneOffset.UTC).plus(Duration.ofDays(1));

        return getAverageDailyTemperatureModul(from, to);
    }


    @Override
    public AverageDailyTemperatureModul getAverageDailyTemperatureBetweenDates(
            DateFormat dateFormat) {

        String firstDateString = dateFormat.getFrom();
        String secondDateString = dateFormat.getTo();

        Instant from = getLocalDateFromString(firstDateString).atStartOfDay()
                .toInstant(ZoneOffset.UTC);
        Instant to = getLocalDateFromString(secondDateString).atStartOfDay()
                .toInstant(ZoneOffset.UTC).plus(Duration.ofDays(1));

        return getAverageDailyTemperatureModul(from, to);
    }

    private AverageDailyTemperatureModul getAverageDailyTemperatureModul(Instant from, Instant to) {
        List<WeatherData> weatherDataList = weatherRepository.findByTimestampBetween(from, to);

        int averageTemperature = (int) Math.round(
                weatherDataList.stream()
                        .mapToInt(WeatherData::getTemperature)
                        .average()
                        .orElseThrow(() -> new WeatherDataNotFoundException("This time interval is not in the database"))
        );
        return new AverageDailyTemperatureModul(averageTemperature);
    }

    private LocalDate getLocalDateFromString(String dateString) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            LocalDate localDate = LocalDate.parse(dateString, formatter);
            return localDate;
        } catch (Exception e) {
            throw new InvalidDateException("Incorrect data entered");
        }
    }


    @Override
    @Scheduled(fixedRate = 1800000)// Вызывается каждый час
    public void saveWeatherHourly() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://weatherapi-com.p.rapidapi.com/current.json?q=Minsk"))
                    .header("X-RapidAPI-Key", "3822c89bd8msha434f782a2855a8p15e769jsn610c9e2a5979")
                    .header("X-RapidAPI-Host", "weatherapi-com.p.rapidapi.com")
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            JsonObject jsonObject = Json.createReader(new StringReader(response.body()))
                    .readObject();
            saveNewWeatherData(jsonObject);

        } catch (IOException | InterruptedException e) {
            throw new WeatherAPIException(e);
        }
    }

    private void saveNewWeatherData(JsonObject jsonObject) {
        // Define desired temperature and humidity ranges for each room
        // Define desired temperature and humidity ranges for each room
        Map<String, TemperatureRange> temperatureRangesByRoom = new HashMap<>();
        temperatureRangesByRoom.put("Гостиная", new TemperatureRange(18, 25));
        temperatureRangesByRoom.put("Спальня", new TemperatureRange(18, 25));
        temperatureRangesByRoom.put("Кухня", new TemperatureRange(18, 25));
        temperatureRangesByRoom.put("Ванная комната", new TemperatureRange(18, 25));
        temperatureRangesByRoom.put("Туалет", new TemperatureRange(18, 25));
        temperatureRangesByRoom.put("Кабинет", new TemperatureRange(18, 25));
        temperatureRangesByRoom.put("Прихожая", new TemperatureRange(18, 25));
        temperatureRangesByRoom.put("Детская", new TemperatureRange(18, 25));
        temperatureRangesByRoom.put("Комната для игр", new TemperatureRange(18, 25));
        temperatureRangesByRoom.put("Терраса", new TemperatureRange(18, 25));
        temperatureRangesByRoom.put("Балкон", new TemperatureRange(18, 25));
        temperatureRangesByRoom.put("Подвал", new TemperatureRange(18, 25));
        temperatureRangesByRoom.put("Гараж", new TemperatureRange(18, 25));
        temperatureRangesByRoom.put("Коридор", new TemperatureRange(18, 25));
        temperatureRangesByRoom.put("Кладовка", new TemperatureRange(18, 25));
        temperatureRangesByRoom.put("Вторая спальня", new TemperatureRange(18, 25));
        temperatureRangesByRoom.put("Вторая гостиная", new TemperatureRange(18, 25));
        temperatureRangesByRoom.put("Вторая кухня", new TemperatureRange(18, 25));
        temperatureRangesByRoom.put("Третья спальня", new TemperatureRange(18, 25));
        temperatureRangesByRoom.put("Третья гостиная", new TemperatureRange(18, 25));
        temperatureRangesByRoom.put("Вторая ванная комната", new TemperatureRange(18, 25));
        temperatureRangesByRoom.put("Третья ванная комната", new TemperatureRange(18, 25));
        temperatureRangesByRoom.put("Второй туалет", new TemperatureRange(18, 25));
        temperatureRangesByRoom.put("Третий туалет", new TemperatureRange(18, 25));

        Map<String, HumidityRange> humidityRangesByRoom = new HashMap<>();
        humidityRangesByRoom.put("Гостиная", new HumidityRange(45, 60));
        humidityRangesByRoom.put("Спальня", new HumidityRange(45, 60));
        humidityRangesByRoom.put("Кухня", new HumidityRange(45, 60));
        humidityRangesByRoom.put("Ванная комната", new HumidityRange(45, 60));
        humidityRangesByRoom.put("Туалет", new HumidityRange(45, 60));
        humidityRangesByRoom.put("Кабинет", new HumidityRange(45, 60));
        humidityRangesByRoom.put("Прихожая", new HumidityRange(45, 60));
        humidityRangesByRoom.put("Детская", new HumidityRange(45, 60));
        humidityRangesByRoom.put("Комната для игр", new HumidityRange(45, 60));
        humidityRangesByRoom.put("Терраса", new HumidityRange(45, 60));
        humidityRangesByRoom.put("Балкон", new HumidityRange(45, 60));
        humidityRangesByRoom.put("Подвал", new HumidityRange(45, 60));
        humidityRangesByRoom.put("Гараж", new HumidityRange(45, 60));
        humidityRangesByRoom.put("Коридор", new HumidityRange(45, 60));
        humidityRangesByRoom.put("Кладовка", new HumidityRange(45, 60));
        humidityRangesByRoom.put("Вторая спальня", new HumidityRange(45, 60));
        humidityRangesByRoom.put("Вторая гостиная", new HumidityRange(45, 60));
        humidityRangesByRoom.put("Вторая кухня", new HumidityRange(45, 60));
        humidityRangesByRoom.put("Третья спальня", new HumidityRange(45, 60));
        humidityRangesByRoom.put("Третья гостиная", new HumidityRange(45, 60));
        humidityRangesByRoom.put("Вторая ванная комната", new HumidityRange(45, 60));
        humidityRangesByRoom.put("Третья ванная комната", new HumidityRange(45, 60));
        humidityRangesByRoom.put("Второй туалет", new HumidityRange(45, 60));
        humidityRangesByRoom.put("Третий туалет", new HumidityRange(45, 60));

        // Get outdoor weather data
        int outdoorTemperature = jsonObject.getJsonObject("current").getJsonNumber("temp_c").intValue();
        double outdoorHumidity = jsonObject.getJsonObject("current").getJsonNumber("humidity").doubleValue();

        // Iterate over sensorTaH devices
        List<Device> sensorTaHDevices = deviceRepository.findByType("sensorTaH");
        for (Device sensorTaHDevice : sensorTaHDevices) {
            // Пропускаем устройства, которые выключены
            if ("Off".equalsIgnoreCase(String.valueOf(sensorTaHDevice.getStatus()))) {
                continue; // Пропускаем это устройство
            }
            // Determine the room where the device is located
            Room room = sensorTaHDevice.getRoom();
            if (room == null) {
                continue; // Skip devices without a room
            }
            String roomName = room.getName();

            // Simulate indoor temperature and humidity for the room
            TemperatureRange temperatureRange = temperatureRangesByRoom.get(roomName);
            if (temperatureRange == null) {
                continue; // Skip if the room's temperature range is not defined
            }
            HumidityRange humidityRange = humidityRangesByRoom.get(roomName);
            if (humidityRange == null) {
                continue; // Skip if the room's humidity range is not defined
            }

            // Generate random indoor temperature within the desired range
            int indoorTemperature = generateRandomWithinRange(temperatureRange.min(), temperatureRange.max());

            // Generate random indoor humidity within the desired range
            double indoorHumidity = generateRandomWithinRange(humidityRange.getMin(), humidityRange.getMax());

            // Create new WeatherData object with indoor readings for the room
            WeatherData indoorWeatherData = new WeatherData();
            indoorWeatherData.setLocation(roomName);
            indoorWeatherData.setTemperature(indoorTemperature);
            indoorWeatherData.setHumidity(indoorHumidity);
            indoorWeatherData.setWindMetersPerHour((double) 0); // Assuming no wind indoors
            indoorWeatherData.setPressureMb((double) 0); // Assuming no pressure data indoors
            indoorWeatherData.setWeatherCondition("Indoor"); // Custom condition for indoor readings
            indoorWeatherData.setTimestamp(Timestamp.from(Instant.now()));
            indoorWeatherData.setDeviceId(sensorTaHDevice.getId());
            indoorWeatherData.setDeviceName(sensorTaHDevice.getName());

            // Save indoor weather data
            weatherRepository.save(indoorWeatherData);
        }

        // Save outdoor weather data
        WeatherData outdoorWeatherData = new WeatherData();
        outdoorWeatherData.setLocation("Outdoor");
        outdoorWeatherData.setTemperature(outdoorTemperature);
        outdoorWeatherData.setHumidity(outdoorHumidity);
        outdoorWeatherData.setWindMetersPerHour((double) 0); // Assuming no wind outdoors
        outdoorWeatherData.setPressureMb((double) 0); // Assuming no pressure data outdoors
        outdoorWeatherData.setWeatherCondition("Outdoor"); // Custom condition for outdoor readings
        outdoorWeatherData.setTimestamp(Timestamp.from(Instant.now()));
        // Save outdoor weather data
        weatherRepository.save(outdoorWeatherData);
    }

    private int generateRandomWithinRange(int min, int max) {

        return min + (int) (Math.random() * ((max - min) + 1));
    }

    private double generateRandomWithinRange(double min, double max) {
        double randomValue = min + (Math.random() * (max - min));
        return Math.round(randomValue * 10.0) / 10.0; // округляем до одного знака после запятой
    }

    private record TemperatureRange(int min, int max) {}

    private static class HumidityRange {
        private final double min;
        private final double max;

        public HumidityRange(double min, double max) {
            this.min = min;
            this.max = max;
        }

        public double getMin() {
            return min;
        }

        public double getMax() {
            return max;
        }
    }

}
