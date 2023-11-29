package com.tsystems.logistics.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.tsystems.logistics.repository.OrderRepository;
import com.tsystems.logistics.repository.DistanceRepository;

import com.tsystems.logistics.entities.Distance;
import com.tsystems.logistics.entities.Order;
import com.tsystems.logistics.entities.City;
import com.tsystems.logistics.entities.Waypoint;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Optional;
import java.util.List;
import java.util.Arrays;
import java.util.HashSet;

class DistanceServiceTest {

    @Mock
    private DistanceRepository distanceRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private DistanceService distanceService;

    private Distance distance;
    private Order order;
    private City city1;
    private City city2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        city1 = new City();
        city2 = new City();
        city1.setName("Granada");
        city2.setName("Sevilla");

        distance = new Distance();
        distance.setId(1);
        distance.setCity1(city1);
        distance.setCity2(city2);
        distance.setDistance(250);

        order = new Order();
        order.setId(1);
        order.setWaypoints(new HashSet<>());
    }

    @Test
    void addDistance_Success() {
        when(distanceRepository.findByCity1NameAndCity2Name("Granada", "Sevilla")).thenReturn(Optional.empty());
        when(distanceRepository.save(any(Distance.class))).thenReturn(distance);

        Distance savedDistance = distanceService.addDistance(distance);

        assertNotNull(savedDistance);
        assertEquals(distance.getDistance(), savedDistance.getDistance());
    }

    @Test
    void addDistance_ThrowsException_IfDistanceExists() {
        when(distanceRepository.findByCity1NameAndCity2Name("Granada", "Sevilla")).thenReturn(Optional.of(distance));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            distanceService.addDistance(distance);
        });

        assertEquals("Distance already exists between these cities.", exception.getMessage());
    }

    @Test
    void updateDistance_Success() {
        Distance updatedDistance = new Distance();
        updatedDistance.setId(1);
        updatedDistance.setCity1(city1);
        updatedDistance.setCity2(city2);
        updatedDistance.setDistance(300);

        when(distanceRepository.findById(1)).thenReturn(Optional.of(distance));
        when(distanceRepository.save(any(Distance.class))).thenReturn(updatedDistance);

        Distance result = distanceService.updateDistance(updatedDistance);

        assertNotNull(result);
        assertEquals(updatedDistance.getDistance(), result.getDistance());
    }

    @Test
    void updateDistance_ThrowsException_IfDistanceNotFound() {
        Distance updatedDistance = new Distance();
        updatedDistance.setId(1);
        updatedDistance.setCity1(city1);
        updatedDistance.setCity2(city2);
        updatedDistance.setDistance(300);

        when(distanceRepository.findById(1)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> distanceService.updateDistance(updatedDistance));

        assertEquals("Distance not found with id: " + updatedDistance.getId(), exception.getMessage());
    }

    @Test
    void deleteDistance_Success() {
        when(distanceRepository.findById(1)).thenReturn(Optional.of(distance));
        doNothing().when(distanceRepository).deleteById(1);

        assertDoesNotThrow(() -> distanceService.deleteDistance(1));
    }

    @Test
    void deleteDistance_ThrowsException_IfDistanceNotFound() {
        when(distanceRepository.findById(1)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> distanceService.deleteDistance(1));

        assertEquals("Distance not found with id: " + 1, exception.getMessage());
    }

    @Test
    void reorderWaypoints_Success() {
        Waypoint waypoint1 = new Waypoint();
        waypoint1.setOrder(order);
        Waypoint waypoint2 = new Waypoint();
        waypoint2.setOrder(order);
        List<Waypoint> newWaypoints = Arrays.asList(waypoint1, waypoint2);

        when(orderRepository.findById(1)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order result = distanceService.reorderWaypoints(order, newWaypoints);

        assertNotNull(result);
        assertEquals(new HashSet<>(newWaypoints), result.getWaypoints());
    }

    @Test
    void reorderWaypoints_ThrowsException_IfWaypointDoesNotBelongToOrder() {
        Waypoint waypoint1 = new Waypoint();
        waypoint1.setOrder(new Order());
        List<Waypoint> newWaypoints = Arrays.asList(waypoint1);

        when(orderRepository.findById(1)).thenReturn(Optional.of(order));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> distanceService.reorderWaypoints(order, newWaypoints));
        assertEquals("Waypoint does not belong to the order", exception.getMessage());
    }

    @Test
    void reorderWaypoints_ThrowsException_IfOrderNotFound() {
        Order missingOrder = new Order();
        missingOrder.setId(99);

        Waypoint waypoint1 = new Waypoint();
        waypoint1.setOrder(missingOrder);
        Waypoint waypoint2 = new Waypoint();
        waypoint2.setOrder(missingOrder);
        List<Waypoint> newWaypoints = Arrays.asList(waypoint1, waypoint2);

        when(orderRepository.findById(99)).thenReturn(Optional.empty());

        RuntimeException exception =  assertThrows(RuntimeException.class, () -> distanceService.reorderWaypoints(missingOrder, newWaypoints));
        assertEquals("Order not found with id: " + missingOrder.getId(), exception.getMessage());
    }

    @Test
    void getAllDistances_Success() {
        List<Distance> distances = Arrays.asList(distance);
        when(distanceRepository.findAll()).thenReturn(distances);

        List<Distance> result = distanceService.getAllDistances();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(distances.size(), result.size());
    }

    @Test
    void getDistanceById_Success() {
        when(distanceRepository.findById(1)).thenReturn(Optional.of(distance));

        Distance result = distanceService.getDistanceById(1);

        assertNotNull(result);
        assertEquals(distance.getId(), result.getId());
    }

    @Test
    void getDistanceById_ThrowsException_IfDistanceNotFound() {
        when(distanceRepository.findById(1)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> distanceService.getDistanceById(1));

        assertEquals("Distance not found with id: " + 1, exception.getMessage());
    }

    @Test
    void getDistanceByCities_Success() {
        when(distanceRepository.findByCity1NameAndCity2Name("Granada", "Sevilla")).thenReturn(Optional.of(distance));

        Distance result = distanceService.getDistanceByCities("Granada", "Sevilla");

        assertNotNull(result);
        assertEquals(distance.getId(), result.getId());
    }

    @Test
    void getDistanceByCities_ThrowsException_IfDistanceNotFound() {
        when(distanceRepository.findByCity1NameAndCity2Name("Granada", "Sevilla")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> distanceService.getDistanceByCities("Granada", "Sevilla"));

        assertEquals("Distance not found between " + "Granada" + " and " + "Sevilla", exception.getMessage());

    }

}
