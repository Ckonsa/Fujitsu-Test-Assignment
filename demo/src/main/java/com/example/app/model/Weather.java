package com.example.app.model;

import jakarta.persistence.*;


@Entity
@Table(name = "weather")
public class Weather {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "station_name")
    private String stationName;

    @Column(name = "wmo_code")
    private String wmoCode;

    @Column(name = "air_temperature")
    private double airTemperature;

    @Column(name = "wind_speed")
    private double windSpeed;

    @Column(name = "weather_phenomenon")
    private String weatherPhenomenon;

    @Column(name = "timestamp")
    private long timestamp;

    public Weather() {
    }

    /**
     * Constructor for Weather.
     * @param stationName
     * @param wmoCode
     * @param airTemperature
     * @param windSpeed
     * @param weatherPhenomenon
     * @param timestamp
     */
    public Weather(String stationName, String wmoCode, double airTemperature, double windSpeed, String weatherPhenomenon, Long timestamp) {
        this.stationName = stationName;
        this.wmoCode = wmoCode;
        this.airTemperature = airTemperature;
        this.windSpeed = windSpeed;
        this.weatherPhenomenon = weatherPhenomenon;
        this.timestamp = timestamp;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getWmoCode() {
        return wmoCode;
    }

    public void setWmoCode(String wmoCode) {
        this.wmoCode = wmoCode;
    }

    public double getAirTemperature() {
        return airTemperature;
    }

    public void setAirTemperature(double airTemperature) {
        this.airTemperature = airTemperature;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public String getWeatherPhenomenon() {
        return weatherPhenomenon;
    }

    public void setWeatherPhenomenon(String weatherPhenomenon) {
        this.weatherPhenomenon = weatherPhenomenon;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Weather{" +
                "stationName='" + stationName + '\'' +
                ", wmoCode='" + wmoCode + '\'' +
                ", airTemperature=" + airTemperature +
                ", windSpeed=" + windSpeed +
                ", weatherPhenomenon='" + weatherPhenomenon + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
