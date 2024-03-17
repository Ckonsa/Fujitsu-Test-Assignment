package com.example.app.controller;

import com.example.app.model.Weather;
import com.example.app.repository.WeatherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:8080")
@RestController
@RequestMapping("/api")
public class WeatherController {

    @Autowired
    WeatherRepository weatherRepository;

    /**
     * Get all data about weathers from he database.
     * @return list of Weather objects
     */
    @GetMapping("/weathers")
    public ResponseEntity<List<Weather>> getAllEntries() {
        try {
            List<Weather> allEntries = weatherRepository.findAll();
            if (allEntries.size() > 0) {
                return new ResponseEntity<>(allEntries, HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get the latest weather in the desired city.
     * @param city
     * @return Weather object of the latest weather in given city
     */
    @GetMapping("/latest-weather")
    public ResponseEntity<Weather> getLatestWeather(@RequestParam String city) {
        try {
            // Find all the database objects that contain the name of the city in their station name
            List<Weather> weatherDataByCity = weatherRepository.findByStationNameContaining(city);
            if (weatherDataByCity.size() > 0) {
                // Only keep the latest one.
                return new ResponseEntity<>(weatherDataByCity.get(weatherDataByCity.size() - 1), HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Adds Weather data into the database.
     * @param weather
     * @return the added Weather object
     */
    @PostMapping("/weather")
    public ResponseEntity<Weather> addWeatherData(@RequestBody Weather weather) {
        try {
            Weather addedWeather = weatherRepository.save(weather);
            return new ResponseEntity<>(addedWeather, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
