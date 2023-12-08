package com.tsystems.logistics.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.tsystems.logistics.repository.DriverRepository;
import com.tsystems.logistics.repository.TruckRepository;

import com.tsystems.logistics.entities.Driver;
import com.tsystems.logistics.entities.Truck;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;

class DriverServiceTest {

    @Mock
    private DriverRepository driverRepository;

    @Mock
    private TruckRepository truckRepository;

    @InjectMocks
    private DriverService driverService;

    private Driver driver;
    private Truck truck;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        driver = new Driver();
        driver.setId(1);
        driver.setPersonalNumber("12345");
        driver.setName("John");
        driver.setSurname("Doe");
        driver.setWorkingHours(40);
        driver.setCurrentCity("Granada");

        truck = new Truck();
        truck.setId(1);
        truck.setCurrentCity("Granada");
    }

    @Test
    void addDriver_Success() {
        when(driverRepository.findByPersonalNumber("12345")).thenReturn(Optional.empty());
        when(driverRepository.save(any(Driver.class))).thenReturn(driver);

        driver.setStatus("REST");

        Driver savedDriver = driverService.addDriver(driver);

        assertNotNull(savedDriver);
        assertEquals(driver.getPersonalNumber(), savedDriver.getPersonalNumber());
    }

    @Test
    void addDriver_ThrowsException_IfPersonalNumberInUse() {
        when(driverRepository.findByPersonalNumber("12345")).thenReturn(Optional.of(driver));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            driverService.addDriver(driver);
        });

        assertEquals("Personal number already in use.", exception.getMessage());
    }

    @Test
    void updateDriver_Success() {
        when(driverRepository.findById(1)).thenReturn(Optional.of(driver));
        when(driverRepository.findByPersonalNumber("12345")).thenReturn(Optional.empty());
        when(driverRepository.save(any(Driver.class))).thenReturn(driver);

        driver.setStatus("SECOND_DRIVER");

        Driver updatedDriver = driverService.updateDriver(driver);

        assertNotNull(updatedDriver);
        assertEquals(driver.getPersonalNumber(), updatedDriver.getPersonalNumber());
    }

    @Test
    void updateDriver_ThrowsException_IfDriverNotFound() {
        when(driverRepository.findById(1)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> driverService.updateDriver(driver));

        assertEquals("Driver not found with id: " + 1, exception.getMessage());
    }

    @Test
    void updateDriver_ThrowsException_IfPersonalNumberInUse() {
        Driver existingDriver = new Driver();
        existingDriver.setId(1);
        existingDriver.setPersonalNumber("12345");
        existingDriver.setName("John");
        existingDriver.setSurname("Doe");

        Driver driverToUpdate = new Driver();
        driverToUpdate.setId(1);
        driverToUpdate.setPersonalNumber("67890");

        Driver anotherDriver = new Driver();
        anotherDriver.setId(2);
        anotherDriver.setPersonalNumber("67890");

        when(driverRepository.findById(1)).thenReturn(Optional.of(existingDriver));
        when(driverRepository.findByPersonalNumber("67890")).thenReturn(Optional.of(anotherDriver));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> driverService.updateDriver(driverToUpdate));
        assertEquals("New personal number is already in use.", exception.getMessage());
    }


    @Test
    void updateDriver_ThrowsException_IfWorkingHoursExceedLimit() {
        driver.setWorkingHours(180);
        driver.setStatus("REST");

        when(driverRepository.findById(1)).thenReturn(Optional.of(driver));


        RuntimeException exception = assertThrows(RuntimeException.class, () -> driverService.updateDriver(driver));

        assertEquals("Working hours exceed the maximum limit.", exception.getMessage());
    }

    @Test
    void deleteDriver_Success() {
        when(driverRepository.findById(1)).thenReturn(Optional.of(driver));
        doNothing().when(driverRepository).deleteById(1);

        assertDoesNotThrow(() -> driverService.deleteDriver(1));
    }

    @Test
    void deleteDriver_ThrowsException_IfDriverNotFound() {
        when(driverRepository.findById(1)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> driverService.deleteDriver(1));

        assertEquals("Driver not found with id: " + 1, exception.getMessage());
    }

    @Test
    void deleteDriver_ThrowsException_IfDriverAssigned() {
        driver.setCurrentTruck(truck); // Simula que el conductor está asignado a un camión

        when(driverRepository.findById(1)).thenReturn(Optional.of(driver));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> driverService.deleteDriver(1));

        assertEquals("Driver is currently assigned and cannot be deleted.", exception.getMessage());
    }

    @Test
    void assignDriverToTruck_Success() {
        when(driverRepository.findById(1)).thenReturn(Optional.of(driver));
        when(truckRepository.findById(1)).thenReturn(Optional.of(truck));

        assertDoesNotThrow(() -> driverService.assignDriverToTruck(1, 1));
    }

    @Test
    void assignDriverToTruck_ThrowsException_IfDriverNotFound() {
        when(driverRepository.findById(1)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> driverService.assignDriverToTruck(1, 1));

        assertEquals("Driver not found with id: " + 1, exception.getMessage());
    }

    @Test
    void assignDriverToTruck_ThrowsException_IfTruckNotFound() {
        when(driverRepository.findById(1)).thenReturn(Optional.of(driver));
        when(truckRepository.findById(1)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> driverService.assignDriverToTruck(1, 1));
        assertEquals("Truck not found with id: " + 1, exception.getMessage());
    }

    @Test
    void assignDriverToTruck_ThrowsException_IfDriverAssignedToDifferentTruck() {
        Truck anotherTruck = new Truck();
        anotherTruck.setId(2);
        driver.setCurrentTruck(anotherTruck);

        when(driverRepository.findById(1)).thenReturn(Optional.of(driver));
        when(truckRepository.findById(1)).thenReturn(Optional.of(truck));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> driverService.assignDriverToTruck(1, 1));
        assertEquals("Driver is already assigned to a different truck.", exception.getMessage());
    }

    @Test
    void assignDriverToTruck_ThrowsException_IfDriverAndTruckNotInSameCity() {
        truck.setCurrentCity("Sevilla");
        when(driverRepository.findById(1)).thenReturn(Optional.of(driver));
        when(truckRepository.findById(1)).thenReturn(Optional.of(truck));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> driverService.assignDriverToTruck(1, 1));
        assertEquals("Driver and truck are not in the same city.", exception.getMessage());
    }

    @Test
    void assignDriverToTruck_ThrowsException_IfTruckAlreadyAssignedToAnotherDriver() {
        driver.setCurrentTruck(truck); // El conductor actual ya está asignado a este camión

        Set<Driver> driversAssignedToTruck = new HashSet<>();
        driversAssignedToTruck.add(driver); // Añadir el conductor actual al conjunto de conductores asignados al camión
        truck.setDrivers(driversAssignedToTruck);

        when(driverRepository.findById(1)).thenReturn(Optional.of(driver));
        when(truckRepository.findById(1)).thenReturn(Optional.of(truck));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> driverService.assignDriverToTruck(1, 1));
        assertEquals("Truck is already assigned to the specified driver.", exception.getMessage());
    }

    @Test
    void updateDriverStatus_Success() {
        when(driverRepository.findById(1)).thenReturn(Optional.of(driver));

        assertDoesNotThrow(() -> driverService.updateDriverStatus(1, "REST"));
    }

    @Test
    void updateDriverStatus_ThrowsException_IfDriverNotFound() {
        when(driverRepository.findById(1)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> driverService.updateDriverStatus(1, "AVAILABLE"));
        assertEquals("Driver not found with id: " + 1, exception.getMessage());
    }

    @Test
    void updateDriverStatus_ThrowsException_IfInvalidStatus() {
        when(driverRepository.findById(1)).thenReturn(Optional.of(driver));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> driverService.updateDriverStatus(1, "INVALID_STATUS"));
        assertEquals("Invalid status provided.", exception.getMessage());
    }



}
