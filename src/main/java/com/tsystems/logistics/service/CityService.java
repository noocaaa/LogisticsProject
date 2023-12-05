package com.tsystems.logistics.service;

import com.tsystems.logistics.entities.City;
import com.tsystems.logistics.repository.CityRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import com.tsystems.logistics.exception.CityAlreadyExistsException;
import com.tsystems.logistics.exception.CityNotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CityService {

    private final CityRepository cityRepository;

    @Transactional
    public City addCity(City city) {
        if (cityRepository.findByName(city.getName()) != null) {
            throw new CityAlreadyExistsException("A city with the same name already exists.");
        }
        return cityRepository.save(city);
    }

    @Transactional
    public City updateCity(City city) {
        City existingCity = cityRepository.findById(city.getId())
                .orElseThrow(() -> new CityNotFoundException("City not found with id: " + city.getId()));

        existingCity.setName(city.getName());
        return cityRepository.save(existingCity);
    }

    @Transactional
    public void deleteCity(Integer id) {
        City existingCity = cityRepository.findById(id)
                .orElseThrow(() -> new CityNotFoundException("City not found with id: " + id));
        cityRepository.deleteById(id);
    }

    public List<City> getAllCities() {
        return cityRepository.findAll();
    }

    public City getCityById(Integer id) {
        return cityRepository.findById(id)
                .orElseThrow(() -> new CityNotFoundException("City not found with id: " + id));
    }

    public City getCityByName(String name) {
        return cityRepository.findByName(name);
    }
}
