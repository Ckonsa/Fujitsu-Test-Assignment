package com.example.app.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Objects;

public class Delivery {
    private String city;
    private String vehicle;
    private Weather weather;
    // URI to fetch latest weather in certain city
    private String LATEST_WEATHER_URI = "http://localhost:8080/api/latest-weather";
    private final HashMap<String, HashMap<String, Double>> CITY_TO_RBF = new HashMap<>() {{
        put("Tallinn", new HashMap<>(){{
            put("Car", 4.0);
            put("Scooter", 3.5);
            put("Bike", 3.0);
        }});
        put("Tartu", new HashMap<>(){{
            put("Car", 3.5);
            put("Scooter", 3.0);
            put("Bike", 2.5);
        }});
        put("Pärnu", new HashMap<>(){{
            put("Car", 3.0);
            put("Scooter", 2.5);
            put("Bike", 2.0);
        }});
    }};

    /**
     * Constructor for a delivery.
     * @param city
     * @param vehicle
     */
    public Delivery(String city, String vehicle) {
        this.city = city;
        this.vehicle = vehicle;
    }

    private Weather getLatestWeather() throws IOException, InterruptedException {
        // Gets the latest weather according to the city. Returns a Weather object of the latest weather.
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(LATEST_WEATHER_URI+"?city="+this.city))
                .GET()
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(response.body(), Weather.class);
    }

    /**
     * Gets the cost of the delivery.
     * @return cost of the delivery according to the latest weather in the city
     * @throws DeliveryException if the delivery cannot be done
     */
    public double getCost() throws DeliveryException {
        try {
            this.weather = getLatestWeather();
            return calculateCost();
        } catch (IOException | InterruptedException e) {
            System.out.println("Something went wrong with fetching the latest weather!");
            throw new RuntimeException(e);
        }
    }
    private double calculateRBF() {
        // Calculates RBF
        return CITY_TO_RBF.get(this.city).get(this.vehicle);
    }
    private double calculateATEF() {
        // Calculates ATEF
        float lowerLimit = -10;
        float upperLimit = 0;

        if (Objects.equals(this.vehicle, "Scooter") ||
                Objects.equals(this.vehicle, "Bike")) {
            if (this.weather.getAirTemperature() < lowerLimit) {
                return 1;
            }
            if (this.weather.getAirTemperature() <= upperLimit) {
                return 0.5;
            }
        }
        return 0;
    }

    private double calculateWSEF() throws DeliveryException {
        // Calculate WSEF
        float lowerLimit = 10;
        float upperLimit = 20;

        if (Objects.equals(this.vehicle, "Bike")) {
            if (this.weather.getWindSpeed() > upperLimit) {
                throw new DeliveryException("Usage of selected vehicle type is forbidden");
            }
            if (this.weather.getWindSpeed() >= lowerLimit) {
                return 0.5;
            }
        }
        return 0;
    }

    private double calculateWPEF() throws DeliveryException {
        // Calculate WPEF
        if (Objects.equals(this.vehicle, "Scooter") ||
                Objects.equals(this.vehicle, "Bike")) {
            String phenomenon = this.weather.getWeatherPhenomenon().toLowerCase();
            if (phenomenon.contains("snow") || phenomenon.contains("sleet")) {
                return 1;
            }
            if (phenomenon.contains("rain")) {
                return 0.5;
            }
            if (phenomenon.contains("glaze") || phenomenon.contains("hail") || phenomenon.contains("thunder")) {
                throw new DeliveryException("Usage of selected vehicle type is forbidden");
            }
        }
        return 0;
    }

    private double calculateCost() throws DeliveryException {
        // Calculates the total cost of a delivery
        return calculateRBF() + calculateATEF() + calculateWSEF() + calculateWPEF();
    }
}
