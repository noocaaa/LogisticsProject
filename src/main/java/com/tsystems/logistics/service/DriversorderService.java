package com.tsystems.logistics.service;

import com.tsystems.logistics.entities.Driversorder;
import com.tsystems.logistics.entities.DriversorderId;
import com.tsystems.logistics.repository.DriversorderRepository;
import com.tsystems.logistics.repository.OrderRepository;

import com.tsystems.logistics.exception.DriverOrderAssignmentException;
import com.tsystems.logistics.exception.DriverOrderNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.*;

@Service
@RequiredArgsConstructor
public class DriversorderService {

    private final DriversorderRepository driversorderRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public Driversorder addDriversorder(Driversorder driversorder) {
        DriversorderId id = new DriversorderId();
        id.setDriverId(driversorder.getDriver().getId());
        id.setOrderId(driversorder.getOrder().getId());

        if (driversorderRepository.existsById(id)) {
            throw new DriverOrderAssignmentException("This driver is already assigned to the order.");
        }

        return driversorderRepository.save(driversorder);
    }

    @Transactional
    public void deleteDriversorder(Integer driverId, Integer orderId) {
        DriversorderId id = new DriversorderId();
        id.setDriverId(driverId);
        id.setOrderId(orderId);

        if (!driversorderRepository.existsById(id)) {
            throw new DriverOrderNotFoundException("Driversorder not found with driverId: " + driverId + " and orderId: " + orderId);
        }

        driversorderRepository.deleteById(id);
    }
    public List<Driversorder> getAllDriversorders() {
        return driversorderRepository.findAll();
    }

    public List<Driversorder> getDriversorderByDriverId(Integer driverId) {
        return driversorderRepository.findByDriverId(driverId);
    }
}
