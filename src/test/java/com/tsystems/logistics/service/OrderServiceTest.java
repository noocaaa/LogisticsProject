package com.tsystems.logistics.service;

import com.tsystems.logistics.repository.OrderRepository;
import com.tsystems.logistics.repository.TruckRepository;
import com.tsystems.logistics.repository.DriverRepository;
import com.tsystems.logistics.entities.Truck;
import com.tsystems.logistics.entities.Waypoint;
import com.tsystems.logistics.entities.Driver;
import com.tsystems.logistics.entities.Order;
import com.tsystems.logistics.entities.Cargo;
import com.tsystems.logistics.entities.City;

import static org.mockito.Mockito.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private TruckRepository truckRepository;
    @Mock
    private DriverRepository driverRepository;

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void createOrder_Successful() {
        Order testOrder = new Order();
        testOrder.setId(1);
        Truck testTruck = new Truck();
        Set<Driver> testDrivers = new HashSet<>();
        Set<Waypoint> testWaypoints = new HashSet<>();

        Cargo cargo1 = new Cargo();
        cargo1.setId(1);
        cargo1.setStatus("loading");
        City cityA = new City();
        cityA.setName("CityA");

        City cityB = new City();
        cityB.setName("CityB");

        Waypoint waypoint1 = new Waypoint();
        waypoint1.setType("loading");
        waypoint1.setCargo(cargo1);
        waypoint1.setCity(cityA);

        Waypoint waypoint2 = new Waypoint();
        waypoint2.setType("unloading");
        waypoint2.setCargo(cargo1);
        waypoint2.setCity(cityB);

        testWaypoints.add(waypoint1);
        testWaypoints.add(waypoint2);

        testTruck.setStatus("OK");

        testOrder.setTruck(testTruck);
        testOrder.setDrivers(testDrivers);
        testOrder.setWaypoints(testWaypoints);

        when(orderRepository.findById(testOrder.getId())).thenReturn(Optional.of(testOrder));
        when(truckRepository.findById(testTruck.getId())).thenReturn(Optional.of(testTruck));
        testDrivers.forEach(driver -> when(driverRepository.findById(driver.getId())).thenReturn(Optional.of(driver)));

        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArguments()[0]);

        Order savedOrder = orderService.createOrder(testOrder);

        assertNotNull(savedOrder);
        verify(orderRepository).save(testOrder);
    }

    @Test
    void createOrder_Failure_TruckNotAvailable() {
        Order order = new Order();
        Truck truck = new Truck();
        truck.setStatus("NOK"); // Camión no disponible
        order.setTruck(truck);

        when(truckRepository.findById(truck.getId())).thenReturn(Optional.of(truck));

        assertThrows(RuntimeException.class, () -> {
            orderService.createOrder(order);
        });
    }


    @Test
    public void updateOrder_Successful() {
        Order testOrder = new Order();
        testOrder.setId(1);
        Truck testTruck = new Truck();
        Set<Driver> testDrivers = new HashSet<>();
        Set<Waypoint> testWaypoints = new HashSet<>();

        Cargo cargo1 = new Cargo();
        cargo1.setId(1);
        cargo1.setStatus("loading");
        City cityA = new City();
        cityA.setName("CityA");

        City cityB = new City();
        cityB.setName("CityB");

        Waypoint waypoint1 = new Waypoint();
        waypoint1.setType("loading");
        waypoint1.setCargo(cargo1);
        waypoint1.setCity(cityA);

        Waypoint waypoint2 = new Waypoint();
        waypoint2.setType("unloading");
        waypoint2.setCargo(cargo1);
        waypoint2.setCity(cityB);

        testWaypoints.add(waypoint1);
        testWaypoints.add(waypoint2);

        testTruck.setStatus("OK");

        testOrder.setTruck(testTruck);
        testOrder.setDrivers(testDrivers);
        testOrder.setWaypoints(testWaypoints);

        when(orderRepository.findById(testOrder.getId())).thenReturn(Optional.of(testOrder));
        when(truckRepository.findById(testTruck.getId())).thenReturn(Optional.of(testTruck));
        testDrivers.forEach(driver -> when(driverRepository.findById(driver.getId())).thenReturn(Optional.of(driver)));

        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArguments()[0]);

        Order updatedOrder = orderService.updateOrder(testOrder, false);

        assertNotNull(updatedOrder);

        verify(orderRepository).findById(testOrder.getId());
        verify(truckRepository).findById(testTruck.getId());
        testDrivers.forEach(driver -> verify(driverRepository).findById(driver.getId()));
        verify(orderRepository).save(any(Order.class));
    }




    @Test
    void updateOrder_Failure_OrderNotFound() {
        Integer orderId = 6788;
        Order order = new Order();
        order.setId(orderId);

        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.updateOrder(order, false);
        });

        assertEquals("Order not found with id: " + 6788 , exception.getMessage());
    }

    @Test
    void cancelOrder_Successful() {
        Integer orderId = 1;
        Order order = new Order();
        order.setCompleted(false);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        orderService.cancelOrder(orderId);

        verify(orderRepository).save(order);
    }

    @Test
    void cancelOrder_Failure_AlreadyCompleted() {
        Integer orderId = 1;
        Order order = new Order();
        order.setCompleted(true);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        RuntimeException exception =  assertThrows(RuntimeException.class, () -> {
            orderService.cancelOrder(orderId);
        });
        assertEquals("Completed orders cannot be cancelled.", exception.getMessage());
    }

    @Test
    void changeOrderStatus_Failure_AlreadyInSpecifiedState() {
        Integer orderId = 1;
        Order order = new Order();
        order.setCompleted(true);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.changeOrderStatus(orderId, true);
        });

        assertEquals("Order is already in the specified state.", exception.getMessage());
    }


    @Test
    void getAllOrders_ReturnsList() {
        List<Order> orders = Arrays.asList(new Order(), new Order());
        when(orderRepository.findAll()).thenReturn(orders);

        List<Order> result = orderService.getAllOrders();

        assertEquals(orders, result);
    }

    @Test
    void getOrderById_Successful() {
        Integer orderId = 1;
        Order order = new Order();
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        Order foundOrder = orderService.getOrderById(orderId);

        assertEquals(order, foundOrder);
    }

    @Test
    void getOrderById_Failure_NotFound() {
        Integer orderId = 1;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        RuntimeException exception =  assertThrows(RuntimeException.class, () -> {
            orderService.getOrderById(orderId);
        });

        assertEquals("Order not found with id: " + 1 , exception.getMessage());
    }

    @Test
    void changeOrderStatus_Successful() {
        Integer orderId = 1;
        Order order = new Order();
        order.setCompleted(false);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        orderService.changeOrderStatus(orderId, true);

        assertTrue(order.getCompleted());
        verify(orderRepository).save(order);
    }

    @Test
    void changeOrderStatus_Failure_OrderNotFound() {
        Integer orderId = 1;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        RuntimeException exception =  assertThrows(RuntimeException.class, () -> {
            orderService.changeOrderStatus(orderId, true);
        });

        assertEquals("Order not found with id: " + 1 , exception.getMessage());
    }

    @Test
    void createOrder_Failure_TruckNotOkStatus() {
        Order order = new Order();
        Truck truck = new Truck();
        truck.setId(1);
        truck.setStatus("NOK");
        order.setTruck(truck);

        when(truckRepository.findById(truck.getId())).thenReturn(Optional.of(truck));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.createOrder(order);
        });

        assertEquals("Truck is not suitable for the order.", exception.getMessage());
    }

    @Test
    void createOrder_Failure_TruckWithExistingOrders() {
        Order order = new Order();
        Truck truck = new Truck();
        truck.setId(1);
        truck.setStatus("OK");

        Set<Order> existingOrders = new HashSet<>();
        existingOrders.add(new Order());
        truck.setOrders(existingOrders);
        order.setTruck(truck);

        when(truckRepository.findById(truck.getId())).thenReturn(Optional.of(truck));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.createOrder(order);
        });

        assertEquals("Truck is not suitable for the order.", exception.getMessage());
    }

    @Test
    void createOrder_Failure_DriverOverWorked() {
        Order order = new Order();
        Driver driver = new Driver();
        driver.setId(1);
        driver.setWorkingHours(200); // Horas de trabajo excedidas
        Set<Driver> drivers = new HashSet<>();
        drivers.add(driver);
        order.setDrivers(drivers);

        when(driverRepository.findById(driver.getId())).thenReturn(Optional.of(driver));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.createOrder(order);
        });

        assertEquals("Driver is not suitable for the order.", exception.getMessage());

    }

    @Test
    void createOrder_Failure_DriverWithExistingOrders() {
        Order order = new Order();
        Driver driver = new Driver();
        driver.setId(1);
        driver.setWorkingHours(100);
        // Simula que el conductor ya está asignado a otras órdenes
        Set<Order> existingOrders = new HashSet<>();
        existingOrders.add(new Order());
        driver.setOrders(existingOrders);
        Set<Driver> drivers = new HashSet<>();
        drivers.add(driver);
        order.setDrivers(drivers);

        when(driverRepository.findById(driver.getId())).thenReturn(Optional.of(driver));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.createOrder(order);
        });

        assertEquals("Driver is not suitable for the order.", exception.getMessage());
    }

    @Test
    void createOrder_Failure_NoWaypoints() {
        Order order = new Order();
        Set<Waypoint> waypoints = new HashSet<>(); // No waypoints

        order.setWaypoints(waypoints);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.createOrder(order);
        });

        assertEquals("Order must have at least one waypoint.", exception.getMessage());
    }

    @Test
    void createOrder_Failure_UnbalancedLoadingUnloading() {
        Order order = new Order();
        Set<Waypoint> waypoints = new HashSet<>();

        Cargo cargo1 = new Cargo();
        cargo1.setId(1);
        City cityA = new City();
        cityA.setName("CityA");

        Waypoint waypoint1 = new Waypoint();
        waypoint1.setType("loading");
        waypoint1.setCargo(cargo1);
        waypoint1.setCity(cityA);

        Waypoint waypoint2 = new Waypoint();
        waypoint2.setType("loading");
        waypoint2.setCargo(cargo1);
        waypoint2.setCity(cityA);

        waypoints.add(waypoint1);
        waypoints.add(waypoint2);

        order.setWaypoints(waypoints);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.createOrder(order);
        });

        assertEquals("Mismatch in loading and unloading for cargos.", exception.getMessage());
    }


}