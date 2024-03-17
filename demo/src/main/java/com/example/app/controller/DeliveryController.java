package com.example.app.controller;

import com.example.app.model.Delivery;
import com.example.app.model.DeliveryException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;

@CrossOrigin(origins = "http://localhost:8080")
@RestController
@RequestMapping("/api")
public class DeliveryController {

    // Cities where the delivery can be done
    private final ArrayList<String> AVAILABLE_CITIES = new ArrayList<>(Arrays.asList("Tallinn", "Tartu", "Pärnu"));
    // Types of vehicles which are allowed.
    private final ArrayList<String> AVAILABLE_VEHICLE_TYPES = new ArrayList<>(Arrays.asList("Car", "Scooter", "Bike"));

    /**
     * Get the cost for the delivery. If delivery can not be done, explanatory message is given.
     * @param city
     * @param vehicle
     * @return cost of the delivery of reason why delivery can not be done
     */
    @GetMapping("/delivery-cost")
    public ResponseEntity<?> getDeliveryCost(@RequestParam String city, @RequestParam String vehicle) {
        if (!AVAILABLE_CITIES.contains(city)) {
            return new ResponseEntity<>("Delivery can not be done in given city.", HttpStatus.BAD_REQUEST);
        }
        if (!AVAILABLE_VEHICLE_TYPES.contains(vehicle)) {
            return new ResponseEntity<>("Delivery can not be done with given vehicle.", HttpStatus.BAD_REQUEST);
        }
        Delivery delivery = new Delivery(city, vehicle);
        try {
            double cost = delivery.getCost();
            return new ResponseEntity<>(cost, HttpStatus.OK);
        } catch (DeliveryException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.OK);
        }
    }
}
