package com.tsystems.logistics.service;

import com.tsystems.logistics.entities.Truck;
import com.tsystems.logistics.entities.Order;
import com.tsystems.logistics.repository.TruckRepository;
import com.tsystems.logistics.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TruckService {

    private final TruckRepository truckRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public Truck addTruck(Truck truck) {

        if (truckRepository.findByNumber(truck.getNumber()) != null) {
            throw new RuntimeException("Truck number already in use.");
        }

        return truckRepository.save(truck);
    }

    @Transactional
    public Truck updateTruck(Truck truck) {

        Truck existingTruck = truckRepository.findById(truck.getId())
                .orElseThrow(() -> new RuntimeException("Truck not found with id: " + truck.getId()));

        // Unique truck number
        if (!existingTruck.getNumber().equals(truck.getNumber()) && truckRepository.findByNumber(truck.getNumber()) != null) {
            throw new RuntimeException("Truck number is already in use by another truck.");
        }

        existingTruck.setNumber(truck.getNumber());
        existingTruck.setCapacity(truck.getCapacity());
        existingTruck.setStatus(truck.getStatus());
        existingTruck.setCurrentCity(truck.getCurrentCity());

        return truckRepository.save(existingTruck);
    }

    @Transactional
    public void deleteTruck(Integer id) {

        Truck existingTruck = truckRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Truck not found with id: " + id));

        if (!existingTruck.getOrders().isEmpty() || !existingTruck.getDrivers().isEmpty()) {
            throw new RuntimeException("Truck is currently in use and cannot be deleted.");
        }

        truckRepository.deleteById(id);
    }

    public List<Truck> getAllTrucks() {
        return truckRepository.findAll();
    }

    public Truck getTruckById(Integer id) {
        return truckRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Truck not found with id: " + id));
    }

    @Transactional
    public void assignTruckToOrder(Integer truckId, Integer orderId) {

        Truck truck = truckRepository.findById(truckId)
                .orElseThrow(() -> new RuntimeException("Truck not found with id: " + truckId));
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        // Check that cargo and order are in the same city
        if (!truck.getCurrentCity().equals(order.getTruck().getCurrentCity())) {
            throw new RuntimeException("Truck and order are not in the same city.");
        }

        // Check that the size of the truck is correct for the order
        if (truck.getCapacity() < order.getTruck().getCapacity()) {
            throw new RuntimeException("Truck does not have enough capacity for the order.");
        }

        // Check if truck is available and it's available for the order.
        if (!"AVAILABLE".equals(truck.getStatus())) {
            throw new RuntimeException("Truck is currently unavailable.");
        }

        // If the truck its already assign to the order
        if (truck.getOrders().contains(order)) {
            throw new RuntimeException("Truck is already assigned to this order.");
        }

        // Set the truck for the order, and update the status.
        truck.getOrders().add(order);
        truck.setStatus("ASSIGNED");
        truckRepository.save(truck);
    }
}
