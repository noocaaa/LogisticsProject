package com.tsystems.logistics.service;

import com.tsystems.logistics.entities.Order;
import com.tsystems.logistics.entities.Waypoint;
import com.tsystems.logistics.entities.Cargo;

import com.tsystems.logistics.dto.WaypointDTO;


import com.tsystems.logistics.repository.CityRepository;
import com.tsystems.logistics.repository.OrderRepository;
import com.tsystems.logistics.repository.WaypointRepository;
import com.tsystems.logistics.repository.CargoRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WaypointService {

    private final WaypointRepository waypointRepository;
    private final OrderRepository orderRepository;
    private final CityRepository cityRepository;
    private final CargoRepository cargoRepository;

    @Transactional
    public Waypoint addWaypoint(Waypoint waypoint) {
        validateWaypoint(waypoint);
        return waypointRepository.save(waypoint);
    }

    @Transactional
    public Waypoint updateWaypoint(Waypoint waypoint) {
        Waypoint existingWaypoint = waypointRepository.findById(waypoint.getId())
                .orElseThrow(() -> new RuntimeException("Waypoint not found with id: " + waypoint.getId()));

        validateWaypoint(waypoint);
        existingWaypoint.setOrder(waypoint.getOrder());
        existingWaypoint.setCity(waypoint.getCity());
        existingWaypoint.setCargo(waypoint.getCargo());
        existingWaypoint.setType(waypoint.getType());

        return waypointRepository.save(existingWaypoint);
    }

    @Transactional
    public void deleteWaypoint(Integer id) {
        Waypoint existingWaypoint = waypointRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Waypoint not found with id: " + id));
        waypointRepository.deleteById(id);
    }

    public List<Waypoint> getAllWaypoints() {
        return waypointRepository.findAll();
    }

    public Waypoint getWaypointById(Integer id) {
        return waypointRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Waypoint not found with id: " + id));
    }

    @Transactional
    public void reorderWaypoints(Integer orderId, List<Integer> waypointIds) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        waypointIds.forEach(waypointId -> {
            Waypoint waypoint = waypointRepository.findById(waypointId)
                    .orElseThrow(() -> new RuntimeException("Waypoint not found with id: " + waypointId));
            waypoint.setOrder(order);
            waypointRepository.save(waypoint);
        });
    }

    private void validateWaypoint(Waypoint waypoint) {
        if (waypoint.getCity() == null || !cityRepository.existsById(waypoint.getCity().getId())) {
            throw new RuntimeException("Invalid city for waypoint.");
        }

        if (waypoint.getCargo() != null && (!cargoRepository.existsById(waypoint.getCargo().getId()) || !isCargoValid(waypoint.getCargo()))) {
            throw new RuntimeException("Invalid or unavailable cargo for waypoint.");
        }

        if (!isValidWaypointType(waypoint.getType())) {
            throw new RuntimeException("Invalid waypoint type. Must be 'loading' or 'unloading'.");
        }
    }

    private boolean isCargoValid(Cargo cargo) {
        return "ready".equals(cargo.getStatus());
    }

    private boolean isValidWaypointType(String type) {
        return "loading".equals(type) || "unloading".equals(type);
    }

    public WaypointDTO convertToDTO(Waypoint waypoint) {
        if (waypoint == null) {
            return null;
        }

        WaypointDTO dto = new WaypointDTO();
        dto.setId(waypoint.getId());
        dto.setOrderId(waypoint.getOrder().getId());
        dto.setCityId(waypoint.getCity().getId());
        dto.setCityName(waypoint.getCity().getName());
        dto.setCargoId(waypoint.getCargo().getId());
        dto.setType(waypoint.getType());

        return dto;
    }

    private String getCityName(Waypoint waypoint) {
        return waypoint.getCity().getName();
    }
}