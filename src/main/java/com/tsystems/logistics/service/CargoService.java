package com.tsystems.logistics.service;

import com.tsystems.logistics.entities.Cargo;
import com.tsystems.logistics.entities.Order;
import com.tsystems.logistics.entities.Waypoint;

import com.tsystems.logistics.exception.InvalidDriverStatusException;
import com.tsystems.logistics.repository.CargoRepository;
import com.tsystems.logistics.repository.OrderRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import com.tsystems.logistics.exception.CargoAlreadyExistsException;
import com.tsystems.logistics.exception.CargoNotFoundException;
import com.tsystems.logistics.exception.CargoAssignmentException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.tsystems.logistics.dto.CargoDTO;

@Service
@RequiredArgsConstructor
public class CargoService {

    private final CargoRepository cargoRepository;
    private final OrderRepository orderRepository;

    private final List<String> validStatuses = Arrays.asList("ready", "shipped", "delivered");

    @Transactional
    public Cargo addCargo(Cargo cargo) {
        if (cargo.getWeight() < 0 ) {
            throw new CargoAssignmentException("Cargo weight cannot be null.");
        }

        if (!validStatuses.contains(cargo.getStatus())) {
            throw new CargoAssignmentException("Invalid status, must be a valid type.");
        }

        return cargoRepository.save(cargo);
    }

    @Transactional
    public Cargo updateCargo(Cargo cargo) {

        Cargo existingCargo = cargoRepository.findById(cargo.getId())
                .orElseThrow(() -> new CargoNotFoundException("Cargo not found with id: " + cargo.getId()));

        if (existingCargo == null) {
            throw new CargoAlreadyExistsException("A cargo with the same number already exists.");
        }

        if (cargo.getWeight() < 0 ) {
            throw new CargoAssignmentException("Cargo weight cannot be null.");
        }

        if (!validStatuses.contains(cargo.getStatus())) {
            throw new CargoAssignmentException("Invalid status, must be a valid type.");
        }

        existingCargo.setName(cargo.getName());
        existingCargo.setWeight(cargo.getWeight());
        existingCargo.setStatus(cargo.getStatus());

        return cargoRepository.save(existingCargo);
    }

    @Transactional
    public void deleteCargo(Integer id) {

        Cargo existingCargo = cargoRepository.findById(id)
                .orElseThrow(() -> new CargoNotFoundException("Cargo not found with id: " + id));

        if (!existingCargo.getWaypoints().isEmpty()) {
            throw new CargoAssignmentException("Cargo is currently assigned and cannot be deleted.");
        }

        cargoRepository.deleteById(id);
    }

    public List<Cargo> getAllCargos() {

        return cargoRepository.findAll();
    }

    public Cargo getCargoById(Integer id) {

        return cargoRepository.findById(id)
                .orElseThrow(() -> new CargoNotFoundException("Cargo not found with id: " + id));
    }

    public List<Cargo> getAvailableCargos() {
        return cargoRepository.findAll().stream()
                .filter(cargo -> "ready".equals(cargo.getStatus()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void assignCargoToOrder(Integer cargoId, Integer orderId) {

        Cargo cargo = cargoRepository.findById(cargoId)
                .orElseThrow(() ->  new CargoNotFoundException("Cargo not found with id: " + cargoId));
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->  new CargoNotFoundException("Order not found with id: " + orderId));

        if (!"ready".equals(cargo.getStatus())) {
            throw new CargoAssignmentException("Cargo is not ready to be shipped.");
        }

        if (order.getCompleted()) {
            throw new CargoAssignmentException("Order is already completed.");
        }

        Waypoint newWaypoint = new Waypoint();
        newWaypoint.setCargo(cargo);
        newWaypoint.setOrder(order);
        order.getWaypoints().add(newWaypoint);

        cargo.setStatus("shipped");
        cargoRepository.save(cargo);

        orderRepository.save(order);
    }

    public int cargoByStatus(String status) {
        return cargoRepository.countByStatus(status);
    }

    public CargoDTO convertToDTO(Cargo cargo) {
        return new CargoDTO(cargo.getId(), cargo.getName(), cargo.getWeight(), cargo.getStatus());
    }

    public List<CargoDTO> convertToDTOList(List<Cargo> cargos) {
        return cargos.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

}
