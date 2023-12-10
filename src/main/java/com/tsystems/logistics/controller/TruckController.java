package com.tsystems.logistics.controller;

import com.tsystems.logistics.dto.CityDTO;
import com.tsystems.logistics.entities.City;
import com.tsystems.logistics.entities.Truck;
import com.tsystems.logistics.service.TruckService;
import com.tsystems.logistics.service.CityService;

import com.tsystems.logistics.dto.TruckDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;

import  org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@PreAuthorize("hasAuthority('ROLE_EMPLOYEE')")
@RequestMapping("/trucks")
public class TruckController {

    @Autowired
    private TruckService truckService;

    @Autowired
    private CityService cityService;

    @GetMapping
    public String trucksPage(Model model) {
        List<Truck> trucks = truckService.getAllTrucks();
        model.addAttribute("trucks", trucks);
        return "trucks";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        Truck truck = truckService.getTruckById(id);
        model.addAttribute("truck", truck);

        if (redirectAttributes.containsAttribute("error")) {
            model.addAttribute("error", redirectAttributes.getFlashAttributes().get("error"));
        }

        List<City> cities = cityService.getAllCities();
        List<CityDTO> cityDTOs = cities.stream()
                .map(city -> new CityDTO(city.getId(), city.getName()))
                .collect(Collectors.toList());
        model.addAttribute("cities", cityDTOs);

        return "editTruck";
    }

    @PutMapping("/editTrucks/{id}")
    public String updateTruck(@PathVariable Integer id, @ModelAttribute TruckDTO truckDTO, RedirectAttributes redirectAttributes) {
        try {
            Truck truck = truckService.convertToEntity(truckDTO);
            truck.setId(id);
            truckService.updateTruck(truck);
            return "redirect:/trucks";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/trucks/edit/" + id;
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteTruck(@PathVariable Integer id) {
        truckService.deleteTruck(id);
        return "redirect:/trucks";
    }

    @GetMapping("/add")
    public String addTruckForm(Model model, RedirectAttributes redirectAttributes) {
        if (redirectAttributes.containsAttribute("error")) {
            model.addAttribute("error", redirectAttributes.getFlashAttributes().get("error"));
        }

        List<City> cities = cityService.getAllCities();
        List<CityDTO> cityDTOs = cities.stream()
                .map(city -> new CityDTO(city.getId(), city.getName()))
                .collect(Collectors.toList());
        model.addAttribute("cities", cityDTOs);

        return "addTruck";
    }


    @PostMapping("/addTruck")
    public String addTruck(@ModelAttribute TruckDTO truckDTO, RedirectAttributes redirectAttributes) {
        try {
            Truck truck = truckService.convertToEntity(truckDTO);
            truckService.addTruck(truck);
            return "redirect:/trucks";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/trucks/add";
        }
    }
}
