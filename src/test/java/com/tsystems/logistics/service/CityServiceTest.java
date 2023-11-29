package com.tsystems.logistics.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.tsystems.logistics.repository.CityRepository;

import com.tsystems.logistics.entities.City;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Optional;
import java.util.List;
import java.util.Arrays;

class CityServiceTest {

    @Mock
    private CityRepository cityRepository;

    @InjectMocks
    private CityService cityService;

    private City city;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        city = new City();
        city.setId(1);
        city.setName("Granada");
    }

    @Test
    void addCity_Success() {
        when(cityRepository.findByName("Granada")).thenReturn(null);
        when(cityRepository.save(any(City.class))).thenReturn(city);

        City savedCity = cityService.addCity(city);

        assertNotNull(savedCity);
        assertEquals(city.getName(), savedCity.getName());
    }

    @Test
    void addCity_ThrowsException_IfCityExists() {
        when(cityRepository.findByName("Granada")).thenReturn(city);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            cityService.addCity(city);
        });

        assertEquals("A city with the same name already exists.", exception.getMessage());
    }

    @Test
    void updateCity_Success() {
        City updatedCity = new City();
        updatedCity.setId(1);
        updatedCity.setName("Sevilla");

        when(cityRepository.findById(1)).thenReturn(Optional.of(city));
        when(cityRepository.save(any(City.class))).thenReturn(updatedCity);

        City result = cityService.updateCity(updatedCity);

        assertNotNull(result);
        assertEquals(updatedCity.getName(), result.getName());
    }

    @Test
    void updateCity_ThrowsException_IfCityNotFound() {
        City updatedCity = new City();
        updatedCity.setId(1);
        updatedCity.setName("Sevilla");

        when(cityRepository.findById(1)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> cityService.updateCity(updatedCity));

        assertEquals("City not found with id: " + updatedCity.getId(), exception.getMessage());
    }

    @Test
    void deleteCity_Success() {
        when(cityRepository.findById(1)).thenReturn(Optional.of(city));
        doNothing().when(cityRepository).deleteById(1);

        assertDoesNotThrow(() -> cityService.deleteCity(1));
    }

    @Test
    void deleteCity_ThrowsException_IfCityNotFound() {
        when(cityRepository.findById(1)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> cityService.deleteCity(1));

        assertEquals("City not found with id: " + 1, exception.getMessage());
    }

    @Test
    void getAllCities_Success() {
        List<City> cities = Arrays.asList(city);
        when(cityRepository.findAll()).thenReturn(cities);

        List<City> result = cityService.getAllCities();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(cities.size(), result.size());
    }

    @Test
    void getCityById_Success() {
        when(cityRepository.findById(1)).thenReturn(Optional.of(city));

        City result = cityService.getCityById(1);

        assertNotNull(result);
        assertEquals(city.getId(), result.getId());
    }

    @Test
    void getCityById_ThrowsException_IfCityNotFound() {
        when(cityRepository.findById(1)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> cityService.getCityById(1));

        assertEquals("City not found with id: " + 1, exception.getMessage());
    }

    @Test
    void getCityByName_Success() {
        when(cityRepository.findByName("Granada")).thenReturn(city);

        City result = cityService.getCityByName("Granada");

        assertNotNull(result);
        assertEquals(city.getName(), result.getName());
    }


}
