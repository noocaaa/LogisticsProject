package com.tsystems.logistics.controller;

import com.tsystems.logistics.entities.City;
import com.tsystems.logistics.entities.Distance;
import com.tsystems.logistics.service.CityService;
import com.tsystems.logistics.service.DistanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/map")
public class MapController {

    @Autowired
    private CityService cityService;

    @Autowired
    private DistanceService distanceService;

    // Cities endpoints
    @GetMapping("/cities")
    public ResponseEntity<List<City>> getAllCities() {
        return ResponseEntity.ok(cityService.getAllCities());
    }

    @PostMapping("/cities")
    public ResponseEntity<City> createCity(@RequestBody City city) {
        return ResponseEntity.ok(cityService.addCity(city));
    }

    @PutMapping("/cities/{id}")
    public ResponseEntity<City> updateCity(@PathVariable Integer id, @RequestBody City city) {
        city.setId(id);
        return ResponseEntity.ok(cityService.updateCity(city));
    }

    @DeleteMapping("/cities/{id}")
    public ResponseEntity<Void> deleteCity(@PathVariable Integer id) {
        cityService.deleteCity(id);
        return ResponseEntity.ok().build();
    }

    // Distances endpoints
    @GetMapping("/distances")
    public ResponseEntity<List<Distance>> getAllDistances() {
        return ResponseEntity.ok(distanceService.getAllDistances());
    }

    @PostMapping("/distances")
    public ResponseEntity<Distance> createDistance(@RequestBody Distance distance) {
        return ResponseEntity.ok(distanceService.addDistance(distance));
    }

    @PutMapping("/distances/{id}")
    public ResponseEntity<Distance> updateDistance(@PathVariable Integer id, @RequestBody Distance distance) {
        distance.setId(id);
        return ResponseEntity.ok(distanceService.updateDistance(distance));
    }

    @DeleteMapping("/distances/{id}")
    public ResponseEntity<Void> deleteDistance(@PathVariable Integer id) {
        distanceService.deleteDistance(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/distances/cities")
    public ResponseEntity<Distance> getDistanceBetweenCities(@RequestParam String city1Name, @RequestParam String city2Name) {
        return ResponseEntity.ok(distanceService.getDistanceByCities(city1Name, city2Name));
    }
}
