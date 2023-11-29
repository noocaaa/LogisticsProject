package com.tsystems.logistics.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.tsystems.logistics.repository.WaypointRepository;
import com.tsystems.logistics.repository.OrderRepository;
import com.tsystems.logistics.repository.CityRepository;
import com.tsystems.logistics.repository.CargoRepository;
import com.tsystems.logistics.entities.Waypoint;
import com.tsystems.logistics.entities.Order;
import com.tsystems.logistics.entities.City;
import com.tsystems.logistics.entities.Cargo;

import java.util.*;

public class WaypointServiceTest {

    @Mock
    private WaypointRepository waypointRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CityRepository cityRepository;

    @Mock
    private CargoRepository cargoRepository;

    @InjectMocks
    private WaypointService waypointService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void addWaypoint_Successful() {
        Waypoint waypoint = new Waypoint();
        City city = new City(); // Asume que esta es una ciudad válida
        city.setId(1);
        waypoint.setCity(city);
        waypoint.setType("loading");

        when(cityRepository.existsById(city.getId())).thenReturn(true);
        when(waypointRepository.save(any(Waypoint.class))).thenAnswer(i -> i.getArguments()[0]);

        Waypoint savedWaypoint = waypointService.addWaypoint(waypoint);

        assertNotNull(savedWaypoint);
        verify(waypointRepository).save(waypoint);
    }

    @Test
    public void addWaypoint_Failure_InvalidCity() {
        Waypoint waypoint = new Waypoint();
        waypoint.setCity(new City());
        waypoint.setType("loading");

        when(cityRepository.existsById(anyInt())).thenReturn(false);

        Exception exception = assertThrows(RuntimeException.class, () -> waypointService.addWaypoint(waypoint));

        assertEquals("Invalid city for waypoint.", exception.getMessage());
    }

    @Test
    public void updateWaypoint_Successful() {
        City city = new City();
        city.setId(1); // Asegúrate de que la ciudad tenga un ID
        city.setName("Granada");
        Waypoint waypoint = new Waypoint();
        waypoint.setId(1);
        waypoint.setCity(city);
        waypoint.setType("unloading");

        when(waypointRepository.findById(waypoint.getId())).thenReturn(Optional.of(waypoint));
        when(cityRepository.existsById(city.getId())).thenReturn(true); // Simular la existencia de la ciudad
        when(cargoRepository.existsById(any())).thenReturn(true); // Simular la existencia de la carga, si es necesario
        when(waypointRepository.save(any(Waypoint.class))).thenAnswer(i -> i.getArguments()[0]);

        Waypoint updatedWaypoint = waypointService.updateWaypoint(waypoint);

        assertNotNull(updatedWaypoint);
        verify(waypointRepository).save(waypoint);
    }


    @Test
    public void updateWaypoint_Failure_NotFound() {
        Waypoint waypoint = new Waypoint();
        waypoint.setId(1);

        when(waypointRepository.findById(waypoint.getId())).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> waypointService.updateWaypoint(waypoint));

        assertEquals("Waypoint not found with id: 1", exception.getMessage());
    }

    @Test
    public void deleteWaypoint_Successful() {
        Integer waypointId = 1;
        Waypoint waypoint = new Waypoint();
        waypoint.setId(waypointId);

        when(waypointRepository.findById(waypointId)).thenReturn(Optional.of(waypoint));
        doNothing().when(waypointRepository).deleteById(waypointId);

        waypointService.deleteWaypoint(waypointId);

        verify(waypointRepository).deleteById(waypointId);
    }

    @Test
    public void deleteWaypoint_Failure_NotFound() {
        Integer waypointId = 1;

        when(waypointRepository.findById(waypointId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> waypointService.deleteWaypoint(waypointId));

        assertEquals("Waypoint not found with id: 1", exception.getMessage());
    }

    @Test
    public void getAllWaypoints_ReturnsList() {
        List<Waypoint> waypoints = Arrays.asList(new Waypoint(), new Waypoint());
        when(waypointRepository.findAll()).thenReturn(waypoints);

        List<Waypoint> result = waypointService.getAllWaypoints();

        assertEquals(waypoints, result);
    }

    @Test
    public void getWaypointById_Successful() {
        Integer waypointId = 1;
        Waypoint waypoint = new Waypoint();
        waypoint.setId(waypointId);

        when(waypointRepository.findById(waypointId)).thenReturn(Optional.of(waypoint));

        Waypoint foundWaypoint = waypointService.getWaypointById(waypointId);

        assertEquals(waypoint, foundWaypoint);
    }

    @Test
    public void getWaypointById_Failure_NotFound() {
        Integer waypointId = 1;

        when(waypointRepository.findById(waypointId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> waypointService.getWaypointById(waypointId));

        assertEquals("Waypoint not found with id: 1", exception.getMessage());
    }

    @Test
    public void reorderWaypoints_Successful() {
        Integer orderId = 1;
        List<Integer> waypointIds = Arrays.asList(1, 2);
        Order order = new Order();
        order.setId(orderId);
        Waypoint waypoint1 = new Waypoint();
        waypoint1.setId(1);
        Waypoint waypoint2 = new Waypoint();
        waypoint2.setId(2);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(waypointRepository.findById(1)).thenReturn(Optional.of(waypoint1));
        when(waypointRepository.findById(2)).thenReturn(Optional.of(waypoint2));

        waypointService.reorderWaypoints(orderId, waypointIds);

        verify(waypointRepository, times(1)).save(waypoint1);
        verify(waypointRepository, times(1)).save(waypoint2);
    }

    @Test
    public void reorderWaypoints_Failure_OrderNotFound() {
        Integer orderId = 1;
        List<Integer> waypointIds = Arrays.asList(1, 2);

        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> waypointService.reorderWaypoints(orderId, waypointIds));

        assertEquals("Order not found with id: 1", exception.getMessage());
    }

    @Test
    public void reorderWaypoints_Failure_WaypointNotFound() {
        Integer orderId = 1, waypointId = 1;
        List<Integer> waypointIds = Collections.singletonList(waypointId);
        Order order = new Order();
        order.setId(orderId);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(waypointRepository.findById(waypointId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> waypointService.reorderWaypoints(orderId, waypointIds));

        assertEquals("Waypoint not found with id: 1", exception.getMessage());
    }


}
