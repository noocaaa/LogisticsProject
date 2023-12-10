package com.tsystems.logistics.controller;

import com.tsystems.logistics.dto.CityDTO;
import com.tsystems.logistics.dto.OrderDTO;
import com.tsystems.logistics.entities.City;
import com.tsystems.logistics.entities.Driver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import com.tsystems.logistics.service.DriverService;
import com.tsystems.logistics.service.CityService;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.security.Principal;

import com.tsystems.logistics.dto.DriverDTO;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@PreAuthorize("hasAuthority('ROLE_EMPLOYEE')")
@RequestMapping("/drivers")
public class DriverController {

    @Autowired
    private DriverService driverService;

    @Autowired
    private CityService cityService;

    @GetMapping
    public String driversPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size,
            Model model) {

        Page<Driver> drivers = driverService.getOrderPage(PageRequest.of(page, size));

        model.addAttribute("drivers", drivers.getContent());
        model.addAttribute("currentPage", drivers.getNumber());
        model.addAttribute("totalPages", drivers.getTotalPages());
        return "drivers";
    }

    @GetMapping("/add")
    public String addDriverForm(Model model, RedirectAttributes redirectAttributes) {
        if (redirectAttributes.containsAttribute("error")) {
            model.addAttribute("error", redirectAttributes.getFlashAttributes().get("error"));
        }

        List<City> cities = cityService.getAllCities();
        List<CityDTO> cityDTOs = cities.stream()
                .map(city -> new CityDTO(city.getId(), city.getName()))
                .collect(Collectors.toList());
        model.addAttribute("cities", cityDTOs);

        return "addDrivers";
    }


    @PostMapping("/addDriver")
    public String addDriver(@ModelAttribute DriverDTO driverDTO, RedirectAttributes redirectAttributes) {
        try {
            Driver driver = driverService.convertToEntity(driverDTO);
            driverService.addDriver(driver);
            return "redirect:/drivers";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/drivers/add";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        Driver driver = driverService.getDriverById(id);
        model.addAttribute("driver", driver);

        if (redirectAttributes.containsAttribute("error")) {
            model.addAttribute("error", redirectAttributes.getFlashAttributes().get("error"));
        }

        List<City> cities = cityService.getAllCities();
        List<CityDTO> cityDTOs = cities.stream()
                .map(city -> new CityDTO(city.getId(), city.getName()))
                .collect(Collectors.toList());
        model.addAttribute("cities", cityDTOs);

        return "editDriver";
    }

    @PutMapping("/editDrivers/{id}")
    public String updateDriver(@PathVariable Integer id, @ModelAttribute DriverDTO driverDTO, RedirectAttributes redirectAttributes) {
        try {
            Driver driver = driverService.convertToEntity(driverDTO);
            driver.setId(id);
            driverService.updateDriver(driver);
            return "redirect:/drivers";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/drivers/edit/" + id;
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteDriver(@PathVariable Integer id) {
        driverService.deleteDriver(id);
        return "redirect:/drivers";
    }

    @PostMapping("/startShift")
    public String startShift(Principal principal) {
        String username = principal.getName();
        driverService.startShift(username);
        return "redirect:/dashboard";
    }

    @PostMapping("/endShift")
    public String endShift(Principal principal, RedirectAttributes redirectAttributes) {
        try {
            String username = principal.getName();
            driverService.endShift(username);
            return "redirect:/dashboard";
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/dashboard";
        }
    }

}
