package com.tsystems.logistics.controller;

import com.tsystems.logistics.entities.Cargo;
import com.tsystems.logistics.service.CargoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cargos")
public class CargoController {

    @Autowired
    private CargoService cargoService;

    @GetMapping
    public ResponseEntity<List<Cargo>> getAllCargos() {
        return ResponseEntity.ok(cargoService.getAllCargos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cargo> getCargoById(@PathVariable Integer id) {
        return ResponseEntity.ok(cargoService.getCargoById(id));
    }

    @PostMapping
    public ResponseEntity<Cargo> addCargo(@RequestBody Cargo cargo) {
        return ResponseEntity.ok(cargoService.addCargo(cargo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cargo> updateCargo(@PathVariable Integer id, @RequestBody Cargo cargo) {
        cargo.setId(id);
        return ResponseEntity.ok(cargoService.updateCargo(cargo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCargo(@PathVariable Integer id) {
        cargoService.deleteCargo(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/assign")
    public ResponseEntity<Void> assignCargoToOrder(@RequestParam Integer cargoId, @RequestParam Integer orderId) {
        cargoService.assignCargoToOrder(cargoId, orderId);
        return ResponseEntity.ok().build();
    }
}
