package com.tsystems.logistics.service;

import com.tsystems.logistics.entities.Order;
import com.tsystems.logistics.entities.Truck;
import com.tsystems.logistics.entities.Driver;
import com.tsystems.logistics.entities.Waypoint;
import com.tsystems.logistics.entities.Cargo;
import com.tsystems.logistics.entities.City;

import com.tsystems.logistics.repository.OrderRepository;
import com.tsystems.logistics.repository.TruckRepository;
import com.tsystems.logistics.repository.DriverRepository;
import com.tsystems.logistics.repository.WaypointRepository;
import com.tsystems.logistics.repository.CargoRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final TruckRepository truckRepository;
    private final DriverRepository driverRepository;

    private Set<Waypoint> waypoints;

    private final int MAX_DRIVER_HOURS_PER_MONTH = 176;

    @Transactional
    public Order createOrder(Order order) {

        validateTruckForOrder(order.getTruck());
        validateDriversForOrder(order.getDrivers());

        // Check Waypoints
        validateWaypointsForOrder(order.getWaypoints());

        return orderRepository.save(order);
    }

    @Transactional
    public Order updateOrder(Order order) {
        Order existingOrder = orderRepository.findById(order.getId())
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + order.getId()));

        validateTruckForOrder(order.getTruck());
        validateDriversForOrder(order.getDrivers());
        validateWaypointsForOrder(order.getWaypoints());

        existingOrder.setCompleted(order.getCompleted());
        existingOrder.setTruck(order.getTruck());
        existingOrder.setDrivers(order.getDrivers());
        existingOrder.setWaypoints(order.getWaypoints());

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

    public Order getOrderById(Integer id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
    }

    @Transactional
    public void changeOrderStatus(Integer orderId, boolean completed) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        if (order.getCompleted() != null && order.getCompleted().equals(completed)) {
            throw new RuntimeException("Order is already in the specified state.");
        }

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

        // Check that each order that has a  'loading' waypoint, has also an 'unloading' waypoint
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
    }

    // NEW FUNCTIONS

    public int getTotalNumberOfOrders() {
        return (int) orderRepository.count();
    }

    public int getNumberOfCompletedOrders() {
        return orderRepository.countByCompleted(true);
    }

    public int getNumberOfInProgressOrders() {
        return orderRepository.countByCompleted(false);
    }

}
