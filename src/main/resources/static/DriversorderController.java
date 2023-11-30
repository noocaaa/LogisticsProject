package com.tsystems.logistics.controller;

import com.tsystems.logistics.entities.Driversorder;
import com.tsystems.logistics.service.DriversorderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/driversorders")
public class DriversorderController {

    @Autowired
    private DriversorderService driversorderService;

    @GetMapping
    public ResponseEntity<List<Driversorder>> getAllDriversorders() {
        return ResponseEntity.ok(driversorderService.getAllDriversorders());
    }

    @GetMapping("/driver/{driverId}")
    public ResponseEntity<List<Driversorder>> getDriversorderByDriverId(@PathVariable Integer driverId) {
        return ResponseEntity.ok(driversorderService.getDriversorderByDriverId(driverId));
    }

    @PostMapping
    public ResponseEntity<Driversorder> addDriversorder(@RequestBody Driversorder driversorder) {
        return ResponseEntity.ok(driversorderService.addDriversorder(driversorder));
    }

    @DeleteMapping("/{driverId}/{orderId}")
    public ResponseEntity<Void> deleteDriversorder(@PathVariable Integer driverId, @PathVariable Integer orderId) {
        driversorderService.deleteDriversorder(driverId, orderId);
        return ResponseEntity.ok().build();
    }
}
