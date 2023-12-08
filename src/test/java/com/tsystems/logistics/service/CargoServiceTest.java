package com.tsystems.logistics.service;

import com.tsystems.logistics.entities.Cargo;
import com.tsystems.logistics.entities.Waypoint;
import com.tsystems.logistics.entities.Order;

import com.tsystems.logistics.exception.CargoAssignmentException;
import com.tsystems.logistics.repository.CargoRepository;
import com.tsystems.logistics.repository.OrderRepository;

import com.tsystems.logistics.repository.WaypointRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.*;

public class CargoServiceTest {

    @InjectMocks
    private CargoService cargoService;

    @Mock
    private CargoRepository cargoRepository;

    @Mock
    private OrderRepository orderRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void addCargo_Success() {
        Cargo cargo = new Cargo();
        cargo.setId(123);
        cargo.setWeight(1000);
        cargo.setStatus("ready");

        when(cargoRepository.findById(cargo.getId())).thenReturn(Optional.empty());
        when(cargoRepository.save(any(Cargo.class))).thenReturn(cargo);

        Cargo result = cargoService.addCargo(cargo);

        assertNotNull(result);
        assertEquals(123, result.getId()); // Check unique ID
        assertEquals(1000, result.getWeight());
        assertEquals("ready", result.getStatus());
    }


    @Test
    public void addCargo_Fail_DuplicateName() {
        Cargo existingCargo = new Cargo();
        existingCargo.setId(123);

        Cargo newCargo = new Cargo();
        newCargo.setId(123);

        Optional<Cargo> optionalCargo = Optional.of(existingCargo);

        when(cargoRepository.findById(newCargo.getId())).thenReturn(optionalCargo);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> cargoService.addCargo(newCargo));

        assertEquals("A cargo with the same number already exists.", exception.getMessage());
    }



    @Test
    public void updateCargo_Success() {
        Cargo existingCargo = new Cargo();
        existingCargo.setId(1);
        existingCargo.setName("CargoName");
        existingCargo.setWeight(1000);
        existingCargo.setStatus("ready");

        when(cargoRepository.findById(1)).thenReturn(Optional.of(existingCargo));
        when(cargoRepository.save(any(Cargo.class))).thenReturn(existingCargo);

        existingCargo.setWeight(1500); // Changing weight
        Cargo updatedCargo = cargoService.updateCargo(existingCargo);

        assertNotNull(updatedCargo);
        assertEquals(1500, updatedCargo.getWeight());
    }

    @Test
    public void updateCargo_Fail_NotFound() {
        Cargo nonExistingCargo = new Cargo();
        nonExistingCargo.setId(99);

        when(cargoRepository.findById(99)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> cargoService.updateCargo(nonExistingCargo));

        assertEquals("Cargo not found with id: 99", exception.getMessage());

    }

    @Test
    public void deleteCargo_Success() {
        Integer cargoId = 1;
        Cargo mockCargo = mock(Cargo.class);

        when(mockCargo.getWaypoints()).thenReturn(Collections.emptySet());
        when(cargoRepository.findById(cargoId)).thenReturn(Optional.of(mockCargo));
        doNothing().when(cargoRepository).deleteById(cargoId);

        cargoService.deleteCargo(cargoId);

        verify(cargoRepository, times(1)).deleteById(cargoId);
    }


    @Test
    public void deleteCargo_AssignedCargo_ThrowsException() {
        Integer cargoId = 1;
        Cargo mockCargo = new Cargo();
        mockCargo.setId(cargoId);

        // We simulate Waypoints, so it should not be deleted
        Set<Waypoint> waypoints = new HashSet<>();
        waypoints.add(new Waypoint());
        mockCargo.setWaypoints(waypoints);

        when(cargoRepository.findById(cargoId)).thenReturn(Optional.of(mockCargo));

        // Act & Assert
        try {
            cargoService.deleteCargo(cargoId);
            fail("Expected RuntimeException to be thrown");
        } catch (RuntimeException e) {
            assertEquals("Cargo is currently assigned and cannot be deleted.", e.getMessage());
        }

        // Check that delete was not called
        verify(cargoRepository, never()).deleteById(anyInt());
    }


    @Test
    public void deleteCargo_CargoNotFound_ThrowsException() {
        Integer cargoId = 1;

        when(cargoRepository.findById(cargoId)).thenReturn(Optional.empty());

        // Act & Assert
        try {
            cargoService.deleteCargo(cargoId);
            fail("Expected RuntimeException to be thrown");
        } catch (RuntimeException e) {
            assertEquals("Cargo not found with id: " + cargoId, e.getMessage());
        }

        // Check that delete was not called
        verify(cargoRepository, never()).deleteById(anyInt());
    }



    @Test
    public void getAllCargos_Success() {
        List<Cargo> cargos = Arrays.asList(new Cargo(), new Cargo());
        when(cargoRepository.findAll()).thenReturn(cargos);

        List<Cargo> result = cargoService.getAllCargos();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    public void getCargoById_Success() {
        Cargo cargo = new Cargo();
        cargo.setId(1);
        when(cargoRepository.findById(1)).thenReturn(Optional.of(cargo));

        Cargo result = cargoService.getCargoById(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
    }

    @Test
    public void getCargoById_Fail_NotFound() {
        when(cargoRepository.findById(99)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> cargoService.getCargoById(99));

        assertEquals("Cargo not found with id: 99", exception.getMessage());
    }

    @Test
    public void assignCargoToOrder_Success() {
        Integer cargoId = 1;
        Integer orderId = 1;

        Cargo cargo = new Cargo();
        cargo.setId(cargoId);
        cargo.setStatus("ready");

        Order order = new Order();
        order.setId(orderId);
        order.setCompleted(false);
        Set<Waypoint> waypoints = new HashSet<>();
        order.setWaypoints(waypoints);

        // Mock the responses for findById
        when(cargoRepository.findById(cargoId)).thenReturn(Optional.of(cargo));
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order savedOrder = invocation.getArgument(0);
            waypoints.addAll(savedOrder.getWaypoints());
            return savedOrder;
        });

        cargoService.assignCargoToOrder(cargoId, orderId);

        assertEquals("shipped", cargo.getStatus());
        assertTrue(order.getWaypoints().stream()
                .anyMatch(wp -> wp.getCargo().equals(cargo) && wp.getOrder().equals(order)));

        verify(cargoRepository).save(cargo);
        verify(orderRepository).save(order);
    }

    @Test
    public void assignCargoToCompletedOrder_Fail() {
        Integer cargoId = 1;
        Integer orderId = 1;

        Cargo cargo = new Cargo();
        cargo.setId(cargoId);
        cargo.setStatus("ready");

        Order order = new Order();
        order.setId(orderId);
        order.setCompleted(true);

        when(cargoRepository.findById(cargoId)).thenReturn(Optional.of(cargo));
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        assertThrows(CargoAssignmentException.class, () -> cargoService.assignCargoToOrder(cargoId, orderId));
    }

    @Test
    public void deleteCargoWithWaypoints_Fail() {
        Integer cargoId = 1;
        Cargo cargo = new Cargo();
        cargo.setId(cargoId);
        Set<Waypoint> waypoints = new HashSet<>();
        waypoints.add(new Waypoint());
        cargo.setWaypoints(waypoints);

        when(cargoRepository.findById(cargoId)).thenReturn(Optional.of(cargo));

        assertThrows(CargoAssignmentException.class, () -> cargoService.deleteCargo(cargoId));
    }

    @Test
    public void addCargo_InvalidWeight_Fail() {
        Cargo cargo = new Cargo();
        cargo.setId(123);
        cargo.setWeight(-100);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> cargoService.addCargo(cargo));

        assertEquals("Cargo weight cannot be null.", exception.getMessage());
    }

    @Test
    public void addCargo_MaxWeight_Success() {
        Cargo cargo = new Cargo();
        cargo.setId(320932847);
        cargo.setWeight(Integer.MAX_VALUE);

        when(cargoRepository.save(any(Cargo.class))).thenReturn(cargo);

        Cargo result = cargoService.addCargo(cargo);

        assertNotNull(result);
        assertEquals(Integer.MAX_VALUE, result.getWeight());
    }

    @Test
    public void repositoryFindById_Success() {
        Integer cargoId = 1;
        Cargo cargo = new Cargo();
        cargo.setId(cargoId);

        when(cargoRepository.findById(cargoId)).thenReturn(Optional.of(cargo));

        Cargo result = cargoService.getCargoById(cargoId);

        assertNotNull(result);
        assertEquals(cargoId, result.getId());
    }


}
