package com.tsystems.logistics.service;


import com.tsystems.logistics.entities.Order;
import com.tsystems.logistics.entities.Truck;
import com.tsystems.logistics.entities.Cargo;
import com.tsystems.logistics.entities.Waypoint;

import com.tsystems.logistics.repository.TruckRepository;
import com.tsystems.logistics.repository.OrderRepository;
import com.tsystems.logistics.repository.CargoRepository;

import com.tsystems.logistics.dto.TruckDTO;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.HashSet;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TruckService {

    private final TruckRepository truckRepository;
    private final OrderRepository orderRepository;
    private final CargoRepository cargoRepository;

    @Transactional
    public Truck addTruck(Truck truck) {

        if (truckRepository.findByNumber(truck.getNumber()) != null) {
            throw new RuntimeException("Truck number already in use.");
        }

        String status = truck.getStatus();
        if (!(status.equals("OK") || status.equals("NOK"))) {
            throw new RuntimeException("Invalid truck status. Status must be 'OK' or 'NOK'.");
        }

        return truckRepository.save(truck);
    }

    @Transactional
    public Truck updateTruck(Truck truck) {

        Truck existingTruck = truckRepository.findById(truck.getId())
                .orElseThrow(() -> new RuntimeException("Truck not found with id: " + truck.getId()));

        Truck truckWithSameNumber = truckRepository.findByNumber(truck.getNumber());
        if (truckWithSameNumber != null && !truckWithSameNumber.getId().equals(truck.getId())) {
            throw new RuntimeException("Truck number is already in use by another truck.");
        }

        String status = truck.getStatus();
        if (!(status.equals("OK") || status.equals("NOK"))) {
            throw new RuntimeException("Invalid truck status. Status must be 'OK' or 'NOK'.");
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

        if (!"OK".equals(truck.getStatus())) {
            throw new RuntimeException("Truck is currently unavailable.");
        }

        if (truck.getOrders() != null && truck.getOrders().contains(order)) {
            throw new RuntimeException("Truck is already assigned to this order.");
        }

        if (truck.getOrders() == null) {
            truck.setOrders(new HashSet<>());
        }
        truck.getOrders().add(order);
        truck.setStatus("NOK");

        truckRepository.save(truck);
    }

    public int getNOKStatus() {
        return truckRepository.countByStatus("NOK");
    }

    public TruckDTO convertToDTO(Truck truck) {
        TruckDTO dto = new TruckDTO();

        if (truck != null) {
            dto.setId(truck.getId());
            dto.setNumber(truck.getNumber());
            dto.setCapacity(truck.getCapacity());
            dto.setStatus(truck.getStatus());
            dto.setCurrentCity(truck.getCurrentCity());

            return dto;
        }
        return null;
    }

    public Truck convertToEntity(TruckDTO truckDTO) {
        Truck truck;
        if (truckDTO.getId() != null) {
            truck = getTruckById(truckDTO.getId());
        } else {
            truck = new Truck();
        }
        truck.setNumber(truckDTO.getNumber());
        truck.setCapacity(truckDTO.getCapacity());
        truck.setStatus(truckDTO.getStatus());
        truck.setCurrentCity(truckDTO.getCurrentCity());

        return truck;
    }

    @Transactional
    public List<Truck> getAvailableTrucksForOrder(Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        return truckRepository.findAll().stream()
                .filter(truck -> "OK".equals(truck.getStatus()))
                .filter(truck -> canTruckHandleOrder(truck, order))
                .filter(truck -> isTruckAvailable(truck))
                .collect(Collectors.toList());
    }
    private boolean canTruckHandleOrder(Truck truck, Order order) {
        int totalWeight = 0;
        for (Waypoint waypoint : order.getWaypoints()) {
            Cargo cargo = cargoRepository.findById(waypoint.getCargo().getId())
                    .orElseThrow(() -> new RuntimeException("Cargo not found with id: " + waypoint.getCargo().getId()));

            if ("loading".equals(waypoint.getType())) {
                totalWeight += cargo.getWeight();
            } else if ("unloading".equals(waypoint.getType())) {
                totalWeight -= cargo.getWeight();
            }

            if (totalWeight > truck.getCapacity()) {
                return false;
            }
        }
        return true;
    }

    private boolean isTruckAvailable(Truck truck) {
        return truck.getOrders() == null || truck.getOrders().isEmpty();
    }


}
