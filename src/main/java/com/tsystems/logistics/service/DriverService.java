package com.tsystems.logistics.service;

import com.tsystems.logistics.entities.Distance;
import com.tsystems.logistics.entities.Driver;
import com.tsystems.logistics.entities.Truck;
import com.tsystems.logistics.exception.DriverAlreadyExistsException;
import com.tsystems.logistics.exception.DriverAssignmentException;
import com.tsystems.logistics.exception.DriverNotFoundException;
import com.tsystems.logistics.exception.InvalidDriverStatusException;
import com.tsystems.logistics.repository.DriverRepository;
import com.tsystems.logistics.repository.OrderRepository;
import com.tsystems.logistics.repository.WaypointRepository;
import com.tsystems.logistics.repository.TruckRepository;
import com.tsystems.logistics.repository.DistanceRepository;

import org.hibernate.Hibernate;

import com.tsystems.logistics.dto.DriverDTO;
import com.tsystems.logistics.dto.WaypointDTO;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.stream.Collectors;
import java.util.Collections;
import java.time.Instant;
import java.time.Duration;

import com.tsystems.logistics.dto.OrderDTO;
import com.tsystems.logistics.entities.Order;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.*;

@Service
@RequiredArgsConstructor
public class DriverService {

    private final DriverRepository driverRepository;
    private final TruckRepository truckRepository;
    private final WaypointRepository waypointRepository;
    private final OrderRepository orderRepository;
    private final DistanceRepository distanceRepository;

    private final TruckService truckService;

    private final int AVERAGE_SPEED = 100;
    private final int MAX_WORKING_HOURS = 176;
    private final List<String> validStatuses = Arrays.asList("REST", "DRIVING", "SECOND_DRIVER", "LOADING_UNLOADING" );

    @Transactional
    public Driver addDriver(Driver driver) {
        driverRepository.findByPersonalNumber(driver.getPersonalNumber())
                .ifPresent(s -> {
                    throw new DriverAlreadyExistsException("Personal number already in use.");
                });

        if (!validStatuses.contains(driver.getStatus())) {
            throw new InvalidDriverStatusException("Invalid status, must be a valid type.");
        }

        if (!driver.getPersonalNumber().matches("\\d+")) {
            throw new InvalidDriverStatusException("Personal number should only contain numbers.");
        }

        return driverRepository.save(driver);
    }

    @Transactional
    public Driver updateDriver(Driver driver) {
        Driver existingDriver = driverRepository.findById(driver.getId())
                .orElseThrow(() -> new DriverNotFoundException("Driver not found with id: " + driver.getId()));

        if (!existingDriver.getPersonalNumber().equals(driver.getPersonalNumber())) {
            driverRepository.findByPersonalNumber(driver.getPersonalNumber())
                    .ifPresent(s -> {
                        throw new DriverAlreadyExistsException("New personal number is already in use.");
                    });
        }

        if (!driver.getPersonalNumber().matches("\\d+")) {
            throw new InvalidDriverStatusException("Personal number should only contain numbers.");
        }

        if (driver.getWorkingHours() > MAX_WORKING_HOURS) {
            throw new DriverAssignmentException("Working hours exceed the maximum limit.");
        }

        if (!validStatuses.contains(driver.getStatus())) {
            throw new InvalidDriverStatusException("Invalid status. Status must be either 'rest' or 'driving'.");
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
                .orElseThrow(() -> new DriverNotFoundException("Driver not found with id: " + id));

        if (existingDriver.getCurrentTruck() != null || !existingDriver.getOrders().isEmpty()) {
            throw new DriverAssignmentException("Driver is currently assigned and cannot be deleted.");
        }

        driverRepository.deleteById(id);
    }

    @Transactional
    public void assignDriverToTruck(Integer driverId, Integer truckId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new DriverNotFoundException("Driver not found with id: " + driverId));

        if (driver.getCurrentTruck() != null && !driver.getCurrentTruck().getId().equals(truckId)) {
            throw new DriverAssignmentException("Driver is already assigned to a different truck.");
        }

        Truck truck = truckRepository.findById(truckId)
                .orElseThrow(() -> new DriverNotFoundException("Truck not found with id: " + truckId));

        if (truck.getDrivers().contains(driver)) {
            throw new DriverAssignmentException("Truck is already assigned to the specified driver.");
        }

        if (!driver.getCurrentCity().equals(truck.getCurrentCity())) {
            throw new DriverAssignmentException("Driver and truck are not in the same city.");
        }

        driver.setCurrentTruck(truck);
        driverRepository.save(driver);
    }

    @Transactional
    public void updateDriverStatus(Integer driverId, String status) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new DriverNotFoundException("Driver not found with id: " + driverId));

        System.out.print(status);
        if (!validStatuses.contains(status)) {
            throw new InvalidDriverStatusException("Invalid status provided.");
        }

        driver.setStatus(status);
        driverRepository.save(driver);
    }

    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }

    public Driver getDriverById(Integer id) {
        return driverRepository.findById(id)
                .orElseThrow(() -> new DriverNotFoundException("Driver not found with id: " + id));
    }

    public int getDriverswithMaximumWorkHours() {
        return driverRepository.countByWorkingHours(MAX_WORKING_HOURS);
    }

    public Driver getDriverByUsername(String username) {
        return driverRepository.findByPersonalNumber(username)
                .orElseThrow(() -> new DriverNotFoundException("Driver not found with username: " + username));
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
                .orElseThrow(() -> new DriverNotFoundException("Driver not found with username: " + username));

        DriverDTO driverDTO = convertToDTO(driver);

        if (driver.getCurrentTruck() != null) {
            Integer truckId = driver.getCurrentTruck().getId();
            driverDTO.setTruckNumber(driver.getCurrentTruck().getNumber());
            driverDTO.setCoDrivers(getCoDriverNamesForTruck(truckId, driver.getPersonalNumber()));
            driverDTO.setOrderNumber(getOrderNumberForTruck(truckId));

            Order currentOrder = orderRepository.findByTruckId(truckId)
                    .orElseThrow(() -> new DriverAssignmentException("Order not found for truck id: " + truckId));
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
                throw new DriverAssignmentException("Shift has not been started.");
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
                    throw new DriverAssignmentException("Exceeded maximum working hours limit.");
                } else {
                    driver.setWorkingHours((int) totalHours);
                    driver.setAccumulatedMinutes((int) remainingMinutes);
                    driver.setShiftStartTime(null);
                }
            }

            driver.setShiftEndTime(shiftEnd);
            driverRepository.save(driver);
        } else {
            throw new DriverNotFoundException("Driver not found.");
        }
    }


    @Transactional
    public List<DriverDTO> getAvailableDriversForOrder(OrderDTO order, Integer truckId) {
        Truck truck = truckService.getTruckById(truckId);
        int travelTime = calculateTravelTime(order);

        LocalDate today = LocalDate.now();
        LocalDate endOfMonth = today.withDayOfMonth(today.lengthOfMonth());
        long remainingDaysInMonth = ChronoUnit.DAYS.between(today, endOfMonth);

        return driverRepository.findAll().stream()
                .filter(driver -> isDriverAvailable(driver, travelTime, remainingDaysInMonth))
                .filter(driver -> "REST".equals(driver.getStatus()))
                .filter(driver -> driver.getCurrentCity().equals(truck.getCurrentCity()))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private boolean isDriverAvailable(Driver driver, int travelTime, long remainingDaysInMonth) {
        int travelHours = travelTime / 60;
        int hoursThisMonth = driver.getWorkingHours();
        int hoursNextMonth = 0;

        if (travelHours > remainingDaysInMonth * 24) {
            hoursThisMonth += remainingDaysInMonth * 24;
            hoursNextMonth += travelHours - remainingDaysInMonth * 24;
        } else {
            hoursThisMonth += travelHours;
        }

        return hoursThisMonth <= 176 && hoursNextMonth <= 176;
    }


    private int calculateTravelTime(OrderDTO order) {
        int totalTime = 0;

        List<WaypointDTO> waypoints = new ArrayList<>(order.getWaypoints());
        waypoints.sort(Comparator.comparing(WaypointDTO::getId));

        for (int i = 0; i < waypoints.size() - 1; i++) {
            WaypointDTO start = waypoints.get(i);
            WaypointDTO end = waypoints.get(i + 1);
            Distance distanceEntity = distanceRepository.findDistanceByCity1_IdAndAndCity2_Id(start.getCityId(), end.getCityId());
            int distance = distanceEntity.getDistance();
            totalTime += calculateTimeFromDistance(distance);
            totalTime += 30;
        }

        return totalTime;
    }

    private int calculateTimeFromDistance(int distance) {
        return (distance / AVERAGE_SPEED) * 60;
    }

    private boolean isDriverAvailable(Driver driver, int travelTime) {
        int totalHours = driver.getWorkingHours() + (travelTime / 60);
        return totalHours <= 176;
    }

}

