package com.example.app.repository;

import com.example.app.model.Weather;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WeatherRepository extends JpaRepository<Weather, Long> {
    List<Weather> findByStationNameContaining(String city);

}
