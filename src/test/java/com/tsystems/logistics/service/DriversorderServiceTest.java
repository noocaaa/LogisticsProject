package com.tsystems.logistics.service;

import com.tsystems.logistics.repository.DriversorderRepository;
import com.tsystems.logistics.entities.Driversorder;
import com.tsystems.logistics.entities.DriversorderId;
import com.tsystems.logistics.entities.Driver;
import com.tsystems.logistics.entities.Order;


import static org.mockito.Mockito.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

class DriversorderServiceTest {

    @Mock
    private DriversorderRepository driversorderRepository;

    @InjectMocks
    private DriversorderService driversorderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addDriversorder_Successful() {
        Driversorder driversorder = mock(Driversorder.class);
        DriversorderId id = new DriversorderId();
        id.setDriverId(1);
        id.setOrderId(1);

        when(driversorder.getDriver()).thenReturn(mock(Driver.class));
        when(driversorder.getOrder()).thenReturn(mock(Order.class));
        when(driversorder.getDriver().getId()).thenReturn(1);
        when(driversorder.getOrder().getId()).thenReturn(1);
        when(driversorder.getId()).thenReturn(id);
        when(driversorderRepository.existsById(id)).thenReturn(false);

        driversorderService.addDriversorder(driversorder);

        verify(driversorderRepository).save(driversorder);
    }

    @Test
    void addDriversorder_ThrowsExceptionWhenAlreadyExists() {
        Driversorder driversorder = mock(Driversorder.class);
        DriversorderId id = new DriversorderId();
        id.setDriverId(1);
        id.setOrderId(1);

        when(driversorder.getDriver()).thenReturn(mock(Driver.class));
        when(driversorder.getOrder()).thenReturn(mock(Order.class));
        when(driversorder.getDriver().getId()).thenReturn(1);
        when(driversorder.getOrder().getId()).thenReturn(1);
        when(driversorder.getId()).thenReturn(id);
        when(driversorderRepository.existsById(id)).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            driversorderService.addDriversorder(driversorder);
        });

        assertEquals("This driver is already assigned to the order.", exception.getMessage());
    }


    @Test
    void deleteDriversorder_Successful() {
        Integer driverId = 1;
        Integer orderId = 1;
        DriversorderId id = new DriversorderId();
        id.setDriverId(driverId);
        id.setOrderId(orderId);

        when(driversorderRepository.existsById(id)).thenReturn(true);

        driversorderService.deleteDriversorder(driverId, orderId);

        verify(driversorderRepository).deleteById(id);
    }

    @Test
    void deleteDriversorder_ThrowsExceptionWhenNotFound() {
        Integer driverId = 1;
        Integer orderId = 1;
        DriversorderId id = new DriversorderId();
        id.setDriverId(driverId);
        id.setOrderId(orderId);

        when(driversorderRepository.existsById(id)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            driversorderService.deleteDriversorder(driverId, orderId);
        });

        assertEquals("Driversorder not found with driverId: " + 1 + " and orderId: " + 1, exception.getMessage());

    }


    @Test
    void getAllDriversorders_ReturnsList() {
        List<Driversorder> mockList = Arrays.asList(mock(Driversorder.class), mock(Driversorder.class));
        when(driversorderRepository.findAll()).thenReturn(mockList);

        List<Driversorder> result = driversorderService.getAllDriversorders();

        assertEquals(mockList, result);
    }

    @Test
    void getDriversorderByDriverId_ReturnsList() {
        Integer driverId = 1;
        List<Driversorder> mockList = Arrays.asList(mock(Driversorder.class), mock(Driversorder.class));
        when(driversorderRepository.findByDriverId(driverId)).thenReturn(mockList);

        List<Driversorder> result = driversorderService.getDriversorderByDriverId(driverId);

        assertEquals(mockList, result);
    }


}

