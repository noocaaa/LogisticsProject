package com.tsystems.logistics.service;

import static org.mockito.Mockito.*;

import com.tsystems.logistics.entities.Cargo;
import com.tsystems.logistics.entities.Waypoint;
import com.tsystems.logistics.repository.WaypointRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


import com.tsystems.logistics.repository.OrderRepository;
import com.tsystems.logistics.repository.TruckRepository;
import com.tsystems.logistics.entities.Truck;
import com.tsystems.logistics.entities.Order;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

public class TruckServiceTest {

    @Mock
    private TruckRepository truckRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private WaypointRepository waypointRepository;

    @InjectMocks
    private TruckService truckService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void addTruck_Successful() {
        Truck truck = new Truck();
        truck.setNumber("1234ABC");
        truck.setStatus("OK");

        when(truckRepository.findByNumber(truck.getNumber())).thenReturn(null);
        when(truckRepository.save(any(Truck.class))).thenAnswer(i -> i.getArguments()[0]);

        Truck savedTruck = truckService.addTruck(truck);

        assertNotNull(savedTruck);
        verify(truckRepository).save(truck);
    }

    @Test
    void addTruck_Failure_NumberAlreadyInUse() {
        Truck truck = new Truck();
        truck.setNumber("1234ABC");

        when(truckRepository.findByNumber(truck.getNumber())).thenReturn(truck);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> truckService.addTruck(truck));

        assertEquals("Truck number already in use.", exception.getMessage());
    }

    @Test
    public void updateTruck_Successful() {
        Truck truck = new Truck();
        truck.setId(1);
        truck.setNumber("1234XYZ");
        truck.setStatus("OK");

        when(truckRepository.findById(truck.getId())).thenReturn(Optional.of(truck));
        when(truckRepository.findByNumber(truck.getNumber())).thenReturn(null);
        when(truckRepository.save(any(Truck.class))).thenAnswer(i -> i.getArguments()[0]);

        Truck updatedTruck = truckService.updateTruck(truck);

        assertNotNull(updatedTruck);
        verify(truckRepository).save(truck);
    }

    @Test
    void updateTruck_Failure_TruckNotFound() {
        Truck truck = new Truck();
        truck.setId(1);

        when(truckRepository.findById(truck.getId())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> truckService.updateTruck(truck));

        assertEquals("Truck not found with id: " + 1, exception.getMessage());
    }

    @Test
    void updateTruck_Failure_NumberAlreadyInUse() {
        Truck truck = new Truck();
        truck.setId(1);
        truck.setNumber("1234XYZ");

        Truck anotherTruck = new Truck();
        anotherTruck.setId(2);
        anotherTruck.setNumber("1234XYZ");

        when(truckRepository.findById(1)).thenReturn(Optional.of(truck));
        when(truckRepository.findByNumber("1234XYZ")).thenReturn(anotherTruck);

        assertThrows(RuntimeException.class, () -> truckService.updateTruck(truck));
    }


    @Test
    public void deleteTruck_Successful() {
        Integer truckId = 1;
        Truck truck = new Truck();
        truck.setId(truckId);
        truck.setOrders(Collections.emptySet());

        when(truckRepository.findById(truckId)).thenReturn(Optional.of(truck));
        doNothing().when(truckRepository).deleteById(truckId);

        truckService.deleteTruck(truckId);

        verify(truckRepository).deleteById(truckId);
    }

    @Test
    void deleteTruck_Failure_TruckNotFound() {
        Integer truckId = 1;

        when(truckRepository.findById(truckId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> truckService.deleteTruck(truckId));

        assertEquals("Truck not found with id: " + 1, exception.getMessage());
    }

    @Test
    void deleteTruck_Failure_TruckInUse() {
        Integer truckId = 1;

        Truck truck = new Truck();
        truck.setId(truckId);
        truck.setOrders(Collections.singleton(new Order()));

        when(truckRepository.findById(truckId)).thenReturn(Optional.of(truck));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> truckService.deleteTruck(truckId));

        assertEquals("Truck is currently in use and cannot be deleted.", exception.getMessage());
    }

    @Test
    void getAllTrucks_ReturnsList() {
        List<Truck> trucks = Arrays.asList(new Truck(), new Truck());
        when(truckRepository.findAll()).thenReturn(trucks);

        List<Truck> result = truckService.getAllTrucks();

        assertEquals(trucks, result);
    }

    @Test
    void getTruckById_Successful() {
        Integer truckId = 1;
        Truck truck = new Truck();
        when(truckRepository.findById(truckId)).thenReturn(Optional.of(truck));

        Truck foundTruck = truckService.getTruckById(truckId);

        assertEquals(truck, foundTruck);
    }

    @Test
    void getTruckById_Failure_NotFound() {
        Integer truckId = 1;
        when(truckRepository.findById(truckId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> truckService.getTruckById(truckId));
    }

    @Test
    void assignTruckToOrder_Successful() {
        Integer truckId = 1, orderId = 1;
        Truck truck = new Truck();
        truck.setId(truckId);
        truck.setStatus("OK");
        truck.setOrders(new HashSet<>());

        Order order = new Order();
        order.setId(orderId);

        when(truckRepository.findById(truckId)).thenReturn(Optional.of(truck));
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        truckService.getAvailableTrucksForOrder(orderId);

        truckService.assignTruckToOrder(truckId, orderId);

        assertEquals("OK", truck.getStatus());
        assertTrue(truck.getOrders().contains(order));
        verify(truckRepository).save(truck);
    }

    @Test
    void assignTruckToOrder_TruckNotFound() {
        Integer truckId = 1, orderId = 1;

        when(truckRepository.findById(truckId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            truckService.assignTruckToOrder(truckId, orderId);
        });

        assertEquals("Truck not found with id: " + truckId, exception.getMessage());
    }

    @Test
    void getAvailableTrucksForOrder_ReturnsAvailableTrucks() {
        Integer orderId = 1;
        Order order = new Order();
        order.setId(orderId);

        Truck availableTruck = new Truck();
        availableTruck.setId(1);
        availableTruck.setStatus("OK");
        availableTruck.setCapacity(1);
        availableTruck.setOrders(Collections.emptySet());

        Truck unavailableTruck = new Truck();
        unavailableTruck.setId(2);
        unavailableTruck.setStatus("OK");
        unavailableTruck.setCapacity(2);
        unavailableTruck.setOrders(Collections.singleton(new Order()));

        List<Truck> trucks = Arrays.asList(availableTruck, unavailableTruck);
        when(truckRepository.findAll()).thenReturn(trucks);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        List<Truck> result = truckService.getAvailableTrucksForOrder(orderId);

        assertTrue(result.contains(availableTruck));
        assertFalse(result.contains(unavailableTruck));
    }


    @Test
    void assignTruckToOrder_OrderNotFound() {
        Integer truckId = 1, orderId = 1;
        Truck truck = new Truck();
        truck.setId(truckId);

        when(truckRepository.findById(truckId)).thenReturn(Optional.of(truck));
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            truckService.assignTruckToOrder(truckId, orderId);
        });

        assertEquals("Order not found with id: " + orderId, exception.getMessage());
    }

    @Test
    void assignTruckToOrder_TruckNotAvailable() {
        Integer truckId = 1, orderId = 1;
        Truck truck = new Truck();
        truck.setId(truckId);
        truck.setStatus("NOK");

        Order order = new Order();
        order.setId(orderId);

        when(truckRepository.findById(truckId)).thenReturn(Optional.of(truck));
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            truckService.assignTruckToOrder(truckId, orderId);
        });

        assertEquals("Truck is currently unavailable.", exception.getMessage());
    }

    @Test
    void assignTruckToOrder_TruckAlreadyAssigned() {
        Integer truckId = 1, orderId = 1;
        Truck truck = new Truck();
        truck.setId(truckId);
        truck.setStatus("OK");

        Order order = new Order();
        order.setId(orderId);
        Set<Order> orders = new HashSet<>();
        orders.add(order);
        truck.setOrders(orders);

        when(truckRepository.findById(truckId)).thenReturn(Optional.of(truck));
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            truckService.assignTruckToOrder(truckId, orderId);
        });

        assertEquals("Truck is already assigned to this order.", exception.getMessage());
    }

    @Test
    void assignTruckToOrder_InsufficientCapacityForCargoWeight() {
        Integer truckId = 1, orderId = 24;
        Truck truck = new Truck();
        truck.setId(truckId);
        truck.setCapacity(1);
        truck.setStatus("OK");

        Order order = new Order();
        order.setId(orderId);

        Cargo cargo = new Cargo();
        cargo.setWeight(1100);

        Waypoint loadingWaypoint = new Waypoint();
        loadingWaypoint.setOrder(order);
        loadingWaypoint.setCargo(cargo);
        loadingWaypoint.setType("loading");

        Set<Waypoint> waypoints = new HashSet<Waypoint>();

        waypoints.add(loadingWaypoint);

        order.setWaypoints(waypoints);

        when(truckRepository.findById(truckId)).thenReturn(Optional.of(truck));
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            truckService.assignTruckToOrder(truckId, orderId);
        });

        assertEquals("Insufficient truck capacity for the assigned cargo.", exception.getMessage());
    }

    @Test
    void assignTruckToOrder_TruckNotInOKStatus() {
        Integer truckId = 1, orderId = 1;
        Truck truck = new Truck();
        truck.setId(truckId);
        truck.setStatus("NOK");

        Order order = new Order();
        order.setId(orderId);

        when(truckRepository.findById(truckId)).thenReturn(Optional.of(truck));
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            truckService.assignTruckToOrder(truckId, orderId);
        });

        assertEquals("Truck is currently unavailable.", exception.getMessage());
    }

    @Test
    void assignTruckToMultipleOrders_Concurrently() throws Exception {
        Integer truckId = 1;
        Integer orderId1 = 1;
        Integer orderId2 = 2;

        Truck truck = new Truck();
        truck.setId(truckId);
        truck.setStatus("OK");

        Order order1 = new Order();
        order1.setId(orderId1);

        Order order2 = new Order();
        order2.setId(orderId2);

        when(truckRepository.findById(truckId)).thenReturn(Optional.of(truck));
        when(orderRepository.findById(orderId1)).thenReturn(Optional.of(order1));
        when(orderRepository.findById(orderId2)).thenReturn(Optional.of(order2));

        // Simula la asignación concurrente.
        Runnable task1 = () -> truckService.assignTruckToOrder(truckId, orderId1);
        Runnable task2 = () -> truckService.assignTruckToOrder(truckId, orderId2);

        Thread thread1 = new Thread(task1);
        Thread thread2 = new Thread(task2);

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();

        // Verifica el resultado según la lógica de tu aplicación.
    }


}
