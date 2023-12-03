package com.tsystems.logistics.service;

import com.tsystems.logistics.entities.Driver;
import com.tsystems.logistics.entities.Truck;
import com.tsystems.logistics.repository.DriverRepository;
import com.tsystems.logistics.repository.OrderRepository;
import com.tsystems.logistics.repository.WaypointRepository;
import com.tsystems.logistics.repository.TruckRepository;
import com.tsystems.logistics.dto.DriverDTO;
import com.tsystems.logistics.dto.WaypointDTO;
import java.util.stream.Collectors;
import java.util.Collections;
import java.time.Instant;
import java.time.Duration;

import com.tsystems.logistics.dto.OrderDTO;
import com.tsystems.logistics.entities.Order;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Arrays;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DriverService {

    private final DriverRepository driverRepository;
    private final TruckRepository truckRepository;
    private final WaypointRepository waypointRepository;
    private final OrderRepository orderRepository;

    private final TruckService truckService;

    private final int MAX_WORKING_HOURS = 176;
    private final List<String> validStatuses = Arrays.asList("REST", "DRIVING", "SECOND_DRIVER", "LOADING_UNLOADING" );

    @Transactional
    public Driver addDriver(Driver driver) {
        driverRepository.findByPersonalNumber(driver.getPersonalNumber())
                .ifPresent(s -> {
                    throw new RuntimeException("Personal number already in use.");
                });

        if (!validStatuses.contains(driver.getStatus())) {
            throw new RuntimeException("Invalid status, must be a valid type.");
        }

        if (!driver.getPersonalNumber().matches("\\d+")) {
            throw new RuntimeException("Personal number should only contain numbers.");
        }

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

        if (!driver.getPersonalNumber().matches("\\d+")) {
            throw new RuntimeException("Personal number should only contain numbers.");
        }

        if (driver.getWorkingHours() > MAX_WORKING_HOURS) {
            throw new RuntimeException("Working hours exceed the maximum limit.");
        }

        if (!validStatuses.contains(driver.getStatus())) {
            throw new RuntimeException("Invalid status. Status must be either 'rest' or 'driving'.");
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

        System.out.print(status);
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

    public int getDriverswithMaximumWorkHours() {
        return driverRepository.countByWorkingHours(MAX_WORKING_HOURS);
    }

    public Driver getDriverByUsername(String username) {
        return driverRepository.findByPersonalNumber(username)
                .orElseThrow(() -> new RuntimeException("Driver not found with username: " + username));
    }

    public List<Driver> getCoDrivers(Integer truckId) {
        return driverRepository.findByCurrentTruckId(truckId);
    }

    public DriverDTO convertToDTO(Driver driver) {
        DriverDTO dto = new DriverDTO();
        dto.setId(driver.getId());
        dto.setName(driver.getName());
        dto.setSurname(driver.getSurname());
        dto.setPersonalNumber(driver.getPersonalNumber());
        dto.setWorkingHours(driver.getWorkingHours());
        dto.setStatus(driver.getStatus());
        dto.setCurrentCity(driver.getCurrentCity());

        if (driver.getCurrentTruck() != null) {
            dto.setCurrentTruck(truckService.convertToDTO(driver.getCurrentTruck()));
            dto.setTruckNumber(driver.getCurrentTruck().getNumber());

            Integer truckId = driver.getCurrentTruck().getId();
            dto.setCoDrivers(getCoDriverNamesForTruck(truckId, driver.getPersonalNumber()));
            dto.setOrderNumber(getOrderNumberForDriver(driver));
        }

        if (driver.getOrders() != null) {
            dto.setOrders(driver.getOrders().stream().map(this::convertOrderToDTO).collect(Collectors.toSet()));
        }

        return dto;
    }

    public Driver convertToEntity(DriverDTO dto) {
        Driver driver = new Driver();
        driver.setId(dto.getId());
        driver.setName(dto.getName());
        driver.setSurname(dto.getSurname());
        driver.setPersonalNumber(dto.getPersonalNumber());
        driver.setWorkingHours(dto.getWorkingHours());
        driver.setStatus(dto.getStatus());
        driver.setCurrentCity(dto.getCurrentCity());
        if (dto.getCurrentTruck() != null) {
            driver.setCurrentTruck(truckService.convertToEntity(dto.getCurrentTruck()));
        } else {
            driver.setCurrentTruck(null);
        }
        return driver;
    }

    private OrderDTO convertOrderToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setCompleted(order.getCompleted());

        dto.setTruck(truckService.convertToDTO(order.getTruck()));

        dto.setDrivers(order.getDrivers().stream().map(driver -> {
            DriverDTO driverDTO = new DriverDTO();
            driverDTO.setId(driver.getId());
            return driverDTO;
        }).collect(Collectors.toSet()));
        return dto;
    }

    private String getTruckNumberForDriver(Driver driver) {
        if (driver.getCurrentTruck() != null) {
            return driver.getCurrentTruck().getNumber();
        }
        return null;
    }

    public List<String> getCoDriverNamesForTruck(Integer truckId, String excludePersonalNumber) {
        return driverRepository.findByCurrentTruckId(truckId).stream()
                .filter(driver -> !driver.getPersonalNumber().equals(excludePersonalNumber))
                .map(driver -> driver.getPersonalNumber())
                .collect(Collectors.toList());
    }


    private String getOrderNumberForDriver(Driver driver) {
        if (driver.getCurrentTruck() != null) {
            return orderRepository.findByTruckId(driver.getCurrentTruck().getId())
                    .map(Order::getId)
                    .toString();
        }
        return null;
    }

    private List<WaypointDTO> getWaypointsForOrder(Integer orderId) {
        if (orderId == null) {
            return Collections.emptyList();
        }
        return waypointRepository.findByOrderId(orderId).stream()
                .map(waypoint -> {
                    WaypointDTO dto = new WaypointDTO();
                    dto.setId(waypoint.getId());
                    dto.setOrderId(waypoint.getOrder().getId());
                    dto.setCityId(waypoint.getCity().getId());
                    dto.setCityName(waypoint.getCity().getName());
                    dto.setCargoId(waypoint.getCargo().getId());
                    dto.setType(waypoint.getType());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public DriverDTO getDriverDashboardData(String username) {
        Driver driver = driverRepository.findByPersonalNumber(username)
                .orElseThrow(() -> new RuntimeException("Driver not found with username: " + username));

        DriverDTO driverDTO = convertToDTO(driver);

        if (driver.getCurrentTruck() != null) {
            Integer truckId = driver.getCurrentTruck().getId();
            driverDTO.setTruckNumber(driver.getCurrentTruck().getNumber());
            driverDTO.setCoDrivers(getCoDriverNamesForTruck(truckId, driver.getPersonalNumber()));
            driverDTO.setOrderNumber(getOrderNumberForTruck(truckId));

            Order currentOrder = orderRepository.findByTruckId(truckId)
                    .orElseThrow(() -> new RuntimeException("Order not found for truck id: " + truckId));
            driverDTO.setWaypoints(getWaypointsForOrder(currentOrder.getId()));
        }

        return driverDTO;
    }

    public String getOrderNumberForTruck(Integer truckId) {
        return orderRepository.findTopByTruckIdOrderByIdDesc(truckId)
                .map(order -> order.getId().toString())
                .orElse(null);
    }

    public void startShift(String username) {
        Optional<Driver> optionalDriver = driverRepository.findByPersonalNumber(username);
        if (optionalDriver.isPresent()) {
            Driver driver = optionalDriver.get();
            driver.setShiftStartTime(Instant.now());
            driverRepository.save(driver);
        }
    }

    public void endShift(String username) {
        Optional<Driver> optionalDriver = driverRepository.findByPersonalNumber(username);
        if (optionalDriver.isPresent()) {
            Driver driver = optionalDriver.get();

            if (driver.getShiftStartTime() == null) {
                throw new IllegalStateException("Shift has not been started.");
            }

            Instant shiftStart = driver.getShiftStartTime();
            Instant shiftEnd = Instant.now();

            if (shiftStart != null) {
                long minutesWorked = Duration.between(shiftStart, shiftEnd).toMinutes();
                long totalHours = driver.getWorkingHours();
                long totalMinutesAccumulated = driver.getAccumulatedMinutes() + minutesWorked;

                long additionalHours = totalMinutesAccumulated / 60;
                long remainingMinutes = totalMinutesAccumulated % 60;

                totalHours += additionalHours;
                if (totalHours > 176) {
                    throw new IllegalStateException("Exceeded maximum working hours limit.");
                } else {
                    driver.setWorkingHours((int) totalHours);
                    driver.setAccumulatedMinutes((int) remainingMinutes);
                    driver.setShiftStartTime(null);
                }
            }

            driver.setShiftEndTime(shiftEnd);
            driverRepository.save(driver);
        } else {
            throw new IllegalStateException("Driver not found.");
        }
    }




}

