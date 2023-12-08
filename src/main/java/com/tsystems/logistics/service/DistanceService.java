package com.tsystems.logistics.service;

import com.tsystems.logistics.dto.CityDTO;
import com.tsystems.logistics.dto.DistanceDTO;
import com.tsystems.logistics.entities.City;
import com.tsystems.logistics.entities.Distance;
import com.tsystems.logistics.entities.Waypoint;
import com.tsystems.logistics.entities.Order;

import com.tsystems.logistics.repository.DistanceRepository;
import com.tsystems.logistics.repository.OrderRepository;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.*;

import com.tsystems.logistics.exception.DistanceNotFoundException;
import com.tsystems.logistics.exception.DistanceAlreadyExistsException;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DistanceService {

    private final DistanceRepository distanceRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public Distance addDistance(Distance distance) {
        Optional<Distance> existingDistance = distanceRepository.findByCity1NameAndCity2Name(
                distance.getCity1().getName(), distance.getCity2().getName());
        if (existingDistance.isPresent()) {
            throw new DistanceAlreadyExistsException("Distance already exists between these cities.");
        }
        return distanceRepository.save(distance);
    }

    @Transactional
    public Distance updateDistance(Distance distance) {
        Distance existingDistance = distanceRepository.findById(distance.getId())
                .orElseThrow(() -> new DistanceNotFoundException("Distance not found with id: " + distance.getId()));
        existingDistance.setCity1(distance.getCity1());
        existingDistance.setCity2(distance.getCity2());
        existingDistance.setDistance(distance.getDistance());

        return distanceRepository.save(existingDistance);
    }

    @Transactional
    public void deleteDistance(Integer id) {
        Distance existingDistance = distanceRepository.findById(id)
                .orElseThrow(() -> new DistanceNotFoundException("Distance not found with id: " + id));
        distanceRepository.deleteById(id);
    }

    @Transactional
    public Order reorderWaypoints(Order order, List<Waypoint> newWaypointsOrder) {
        Order existingOrder = orderRepository.findById(order.getId())
                .orElseThrow(() -> new DistanceNotFoundException("Order not found with id: " + order.getId()));

        for (Waypoint wp : newWaypointsOrder) {
            if (!wp.getOrder().equals(existingOrder)) {
                throw new DistanceNotFoundException("Waypoint does not belong to the order");
            }
        }

        Set<Waypoint> waypointsSet = new HashSet<>(newWaypointsOrder);
        existingOrder.setWaypoints(waypointsSet);

        return orderRepository.save(existingOrder);
    }

    @Transactional
    public List<Distance> getAllDistances() {
        return distanceRepository.findAll();
    }

    public Distance getDistanceById(Integer id) {
        return distanceRepository.findById(id)
                .orElseThrow(() -> new DistanceNotFoundException("Distance not found with id: " + id));
    }

    public Distance getDistanceByCities(String city1Name, String city2Name) {
        return distanceRepository.findByCity1NameAndCity2Name(city1Name, city2Name)
                .orElseThrow(() -> new DistanceNotFoundException("Distance not found between " + city1Name + " and " + city2Name));
    }

    @Transactional
    public List<DistanceDTO> getAllDistanceDTOs() {
        List<Distance> distances = distanceRepository.findAll();
        List<DistanceDTO> distancesDTOs = new ArrayList<>();

        for (Distance distance : distances) {
            distancesDTOs.add(convertToDTO(distance));
        }

        return distancesDTOs;
    }


    public DistanceDTO convertToDTO(Distance distance) {
        if (distance == null) {
            return null;
        }

        CityDTO city1DTO = new CityDTO(
                distance.getCity1().getId(),
                distance.getCity1().getName()
        );

        CityDTO city2DTO = new CityDTO(
                distance.getCity2().getId(),
                distance.getCity2().getName()
        );

        return new DistanceDTO(
                distance.getId(),
                city1DTO,
                city2DTO,
                distance.getDistance()
        );
    }
}
