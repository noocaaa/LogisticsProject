package com.tsystems.logistics.service;

import com.tsystems.logistics.entities.Order;
import com.tsystems.logistics.entities.Truck;
import com.tsystems.logistics.entities.Driver;
import com.tsystems.logistics.entities.Waypoint;
import com.tsystems.logistics.entities.Cargo;
import com.tsystems.logistics.dto.OrderDTO;
import com.tsystems.logistics.dto.DriverDTO;
import com.tsystems.logistics.dto.WaypointDTO;

import org.hibernate.Hibernate;

import java.util.stream.Collectors;
import java.util.Set;

import com.tsystems.logistics.repository.OrderRepository;
import com.tsystems.logistics.repository.TruckRepository;
import com.tsystems.logistics.repository.DriverRepository;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final TruckRepository truckRepository;
    private final DriverRepository driverRepository;

    private final TruckService truckService;
    private final DriverService driverService;
    private final WaypointService waypointService;


    private Set<Waypoint> waypoints;

    private final int MAX_DRIVER_HOURS_PER_MONTH = 176;

    @Transactional
    public Order createOrder(Order order) {

        if (order.getTruck() != null) {
            validateTruckForOrder(order.getTruck());
        } else if (order.getDrivers() != null) {
            validateDriversForOrder(order.getDrivers());
        } else if (order.getWaypoints() != null) {
            validateWaypointsForOrder(order.getWaypoints());
        }

        return orderRepository.save(order);
    }

    @Transactional
    public Order updateOrder(Order order, boolean isUpdatingStatusOnly) {
        if (!isUpdatingStatusOnly) {
            validateTruckForOrder(order.getTruck());
            validateDriversForOrder(order.getDrivers());
            validateWaypointsForOrder(order.getWaypoints());
        }

        Order existingOrder = orderRepository.findById(order.getId())
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + order.getId()));

        if (!isUpdatingStatusOnly) {
            existingOrder.setTruck(order.getTruck());
            existingOrder.setDrivers(order.getDrivers());
            existingOrder.setWaypoints(order.getWaypoints());
        }

        existingOrder.setCompleted(order.getCompleted());

        return orderRepository.save(existingOrder);
    }

    @Transactional
    public void cancelOrder(Integer id) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));

        if (Boolean.TRUE.equals(order.getCompleted())) {
            throw new RuntimeException("Completed orders cannot be cancelled.");
        }

        order.setCompleted(true);
        orderRepository.save(order);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Transactional
    public Order getOrderById(Integer id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
        Hibernate.initialize(order.getTruck());
        Hibernate.initialize(order.getDrivers());
        Hibernate.initialize(order.getWaypoints());
        return order;
    }

    @Transactional
    public void changeOrderStatus(Integer orderId, boolean completed) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        order.setCompleted(completed);
        orderRepository.save(order);
    }


    private void validateTruckForOrder(Truck truck) {
        if (truck != null) {
            Truck foundTruck = truckRepository.findById(truck.getId())
                    .orElseThrow(() -> new RuntimeException("Truck not found with id: " + truck.getId()));
            if (!"OK".equals(foundTruck.getStatus()) || !foundTruck.getOrders().isEmpty()) {
                throw new RuntimeException("Truck is not suitable for the order.");
            }
        }
    }

    private void validateDriversForOrder(Set<Driver> drivers) {
        for (Driver driver : drivers) {
            Driver foundDriver = driverRepository.findById(driver.getId())
                    .orElseThrow(() -> new RuntimeException("Driver not found with id: " + driver.getId()));
            if (foundDriver.getWorkingHours() > MAX_DRIVER_HOURS_PER_MONTH || !foundDriver.getOrders().isEmpty()) {
                throw new RuntimeException("Driver is not suitable for the order.");
            }
        }
    }

    private void validateWaypointsForOrder(Set<Waypoint> waypoints) {
        if (waypoints == null || waypoints.isEmpty()) {
            throw new RuntimeException("Order must have at least one waypoint.");
        }

        Map<Cargo, Long> cargoLoadingCount = waypoints.stream()
                .filter(w -> "loading".equals(w.getType()))
                .map(Waypoint::getCargo)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        Map<Cargo, Long> cargoUnloadingCount = waypoints.stream()
                .filter(w -> "unloading".equals(w.getType()))
                .map(Waypoint::getCargo)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        for (Cargo cargo : cargoLoadingCount.keySet()) {
            long loadingCount = cargoLoadingCount.getOrDefault(cargo, 0L);
            long unloadingCount = cargoUnloadingCount.getOrDefault(cargo, 0L);

            if (loadingCount != unloadingCount) {
                throw new RuntimeException("Mismatch in loading and unloading counts for cargo: " + cargo.getId());
            }
        }

        for (Cargo cargo : cargoUnloadingCount.keySet()) {
            if (!cargoLoadingCount.containsKey(cargo)) {
                throw new RuntimeException("Cargo unloaded without being loaded: " + cargo.getId());
            }
        }
    }


    public int getTotalNumberOfOrders() {
        return (int) orderRepository.count();
    }

    public int getNumberOfCompletedOrders() {
        return orderRepository.countByCompleted(true);
    }

    public int getNumberOfInProgressOrders() {
        return orderRepository.countByCompleted(false);
    }

    public Order convertToEntity(OrderDTO dto) {
        Order order = new Order();
        order.setId(dto.getId());
        order.setCompleted(dto.getCompleted());
        if (dto.getTruck() != null) {
            order.setTruck(truckService.convertToEntity(dto.getTruck()));
        }
        return order;
    }

    public OrderDTO convertOrderToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setCompleted(order.getCompleted());
        dto.setTruck(truckService.convertToDTO(order.getTruck()));
        dto.setDrivers(order.getDrivers().stream().map(driverService::convertToDTO).collect(Collectors.toSet()));
        dto.setWaypoints(order.getWaypoints().stream()
                .map(waypointService::convertToDTO)
                .collect(Collectors.toList()));
        dto.setWaypointsCount(order.getWaypoints().size());
        return dto;
    }

    public WaypointDTO convertWaypointToDTO(Waypoint waypoint) {
        WaypointDTO dto = new WaypointDTO();
        dto.setId(waypoint.getId());
        dto.setCityId(waypoint.getCity().getId());
        dto.setCargoId(waypoint.getCargo().getId());
        dto.setType(waypoint.getType());
        return dto;
    }

    private Driver convertDriverDTOtoEntity(DriverDTO driverDTO) {
        Driver driver = new Driver();
        driver.setId(driverDTO.getId());
        driver.setName(driverDTO.getName());
        driver.setSurname(driverDTO.getSurname());
        driver.setPersonalNumber(driverDTO.getPersonalNumber());
        driver.setWorkingHours(driverDTO.getWorkingHours());
        driver.setStatus(driverDTO.getStatus());
        driver.setCurrentCity(driverDTO.getCurrentCity());
        driver.setCurrentTruck(truckService.convertToEntity(driverDTO.getCurrentTruck()));
        return driver;
    }

    @Transactional
    public List<OrderDTO> getAllOrderDTOs() {
        List<Order> orders = orderRepository.findAll();
        orders.forEach(order -> {
            Hibernate.initialize(order.getTruck());
            Hibernate.initialize(order.getDrivers());
            Hibernate.initialize(order.getWaypoints());
        });
        return orders.stream().map(this::convertOrderToDTO).collect(Collectors.toList());
    }

    @Transactional
    public OrderDTO getOrderDTOById(Integer id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
        Hibernate.initialize(order.getTruck());
        Hibernate.initialize(order.getWaypoints());

        OrderDTO orderDTO = convertOrderToDTO(order);

        if (order.getTruck() != null ) {
            List<Driver> driversForTruck = driverRepository.findByCurrentTruckId(order.getTruck().getId());
            Set<DriverDTO> driverDTOs = driversForTruck.stream().map(driverService::convertToDTO).collect(Collectors.toSet());
            orderDTO.setDrivers(driverDTOs);
        }

        return orderDTO;
    }


    @Transactional
    public boolean checkAndUpdateOrderStatus(Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        boolean allCargosDelivered = order.getWaypoints().stream()
                .map(Waypoint::getCargo)
                .allMatch(cargo -> "delivered".equals(cargo.getStatus()));

        if (allCargosDelivered) {
            order.setCompleted(true);
            orderRepository.save(order);
        }

        return allCargosDelivered;
    }

    @Transactional
    public void assignTruckToOrder(Integer orderId, Integer truckId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
        Truck truck = truckRepository.findById(truckId)
                .orElseThrow(() -> new RuntimeException("Truck not found with id: " + truckId));

        order.setTruck(truck);
        orderRepository.save(order);
    }

    @Transactional
    public void assignDriversToOrder(Integer orderId, List<Integer> driverIds) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        Set<Driver> drivers = driverIds.stream()
                .map(driverId -> driverRepository.findById(driverId)
                        .orElseThrow(() -> new RuntimeException("Driver not found with id: " + driverId)))
                .collect(Collectors.toSet());

        order.setDrivers(drivers);
        orderRepository.save(order);
    }

}
