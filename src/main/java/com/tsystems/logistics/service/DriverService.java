package com.tsystems.logistics.service;

import com.tsystems.logistics.entities.Driver;
import com.tsystems.logistics.entities.Truck;
import com.tsystems.logistics.repository.DriverRepository;
import com.tsystems.logistics.repository.TruckRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class DriverService {

    private final DriverRepository driverRepository;
    private final TruckRepository truckRepository;
    private final int MAX_WORKING_HOURS = 60;

    @Transactional
    public Driver addDriver(Driver driver) {
        driverRepository.findByPersonalNumber(driver.getPersonalNumber())
                .ifPresent(s -> {
                    throw new RuntimeException("Personal number already in use.");
                });

        return driverRepository.save(driver);
    }

    @Transactional
    public Driver updateDriver(Driver driver) {
        Driver existingDriver = driverRepository.findById(driver.getId())
                .orElseThrow(() -> new RuntimeException("Driver not found with id: " + driver.getId()));

        if (!existingDriver.getPersonalNumber().equals(driver.getPersonalNumber())) {
            driverRepository.findByPersonalNumber(driver.getPersonalNumber())
                    .ifPresent(s -> {
                        throw new RuntimeException("New personal number is already in use.");
                    });
        }

        if (driver.getWorkingHours() > MAX_WORKING_HOURS) {
            throw new RuntimeException("Working hours exceed the maximum limit.");
        }

        existingDriver.setName(driver.getName());
        existingDriver.setSurname(driver.getSurname());
        existingDriver.setPersonalNumber(driver.getPersonalNumber());
        existingDriver.setStatus(driver.getStatus());
        existingDriver.setCurrentCity(driver.getCurrentCity());
        existingDriver.setWorkingHours(driver.getWorkingHours());

        return driverRepository.save(existingDriver);
    }

    @Transactional
    public void deleteDriver(Integer id) {
        Driver existingDriver = driverRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Driver not found with id: " + id));

        if (existingDriver.getCurrentTruck() != null || !existingDriver.getOrders().isEmpty()) {
            throw new RuntimeException("Driver is currently assigned and cannot be deleted.");
        }

        driverRepository.deleteById(id);
    }

    @Transactional
    public void assignDriverToTruck(Integer driverId, Integer truckId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found with id: " + driverId));

        if (driver.getCurrentTruck() != null && !driver.getCurrentTruck().getId().equals(truckId)) {
            throw new RuntimeException("Driver is already assigned to a different truck.");
        }

        Truck truck = truckRepository.findById(truckId)
                .orElseThrow(() -> new RuntimeException("Truck not found with id: " + truckId));

        if (truck.getDrivers().contains(driver)) {
            throw new RuntimeException("Truck is already assigned to the specified driver.");
        }

        if (!driver.getCurrentCity().equals(truck.getCurrentCity())) {
            throw new RuntimeException("Driver and truck are not in the same city.");
        }

        driver.setCurrentTruck(truck);
        driverRepository.save(driver);
    }

    @Transactional
    public void updateDriverStatus(Integer driverId, String status) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found with id: " + driverId));

        // Check if status is valid.
        List<String> validStatuses = Arrays.asList("REST", "DRIVING" );

        if (!validStatuses.contains(status)) {
            throw new RuntimeException("Invalid status provided.");
        }

        driver.setStatus(status);
        driverRepository.save(driver);
    }

    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }

    public Driver getDriverById(Integer id) {
        return driverRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Driver not found with id: " + id));
    }

}

