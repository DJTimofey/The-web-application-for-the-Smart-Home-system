package com.example.smarthomeapp.service;

import com.example.smarthomeapp.model.AverageDailyTemperatureModul;
import com.example.smarthomeapp.model.DateFormat;
import com.example.smarthomeapp.model.Weather;

public interface WeatherService {

    Weather getCurrentWeather();

    AverageDailyTemperatureModul getAverageDailyTemperature();
    void saveWeatherHourly();


    AverageDailyTemperatureModul getAverageDailyTemperatureBetweenDates(DateFormat dateIntervalDto);
}
