package com.tsystems.logistics.controller;

import com.tsystems.logistics.entities.Truck;
import com.tsystems.logistics.service.TruckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/trucks")
public class TruckController {

    @Autowired
    private TruckService truckService;

    @GetMapping
    public ResponseEntity<List<Truck>> getAllTrucks() {
        return ResponseEntity.ok(truckService.getAllTrucks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Truck> getTruckById(@PathVariable Integer id) {
        return ResponseEntity.ok(truckService.getTruckById(id));
    }

    @PostMapping
    public ResponseEntity<Truck> addTruck(@RequestBody Truck truck) {
        return ResponseEntity.ok(truckService.addTruck(truck));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Truck> updateTruck(@PathVariable Integer id, @RequestBody Truck truck) {
        truck.setId(id);
        return ResponseEntity.ok(truckService.updateTruck(truck));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTruck(@PathVariable Integer id) {
        truckService.deleteTruck(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{truckId}/assign/{orderId}")
    public ResponseEntity<Void> assignTruckToOrder(
            @PathVariable Integer truckId,
            @PathVariable Integer orderId
    ) {
        truckService.assignTruckToOrder(truckId, orderId);
        return ResponseEntity.ok().build();
    }
}
