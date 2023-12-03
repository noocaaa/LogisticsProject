package com.tsystems.logistics.controller;

import com.tsystems.logistics.entities.Driver;
import org.springframework.stereotype.Controller;
import com.tsystems.logistics.service.DriverService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.security.Principal;

import com.tsystems.logistics.dto.DriverDTO;

import java.util.List;

@Controller
@RequestMapping("/drivers")
public class DriverController {

    @Autowired
    private DriverService driverService;

    @GetMapping
    public String driversPage(Model model) {
        List<Driver> drivers = driverService.getAllDrivers();
        model.addAttribute("drivers", drivers);
        return "drivers";
    }


    @GetMapping("/add")
    public String addDriverForm(Model model, RedirectAttributes redirectAttributes) {
        if (redirectAttributes.containsAttribute("error")) {
            model.addAttribute("error", redirectAttributes.getFlashAttributes().get("error"));
        }
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



    /* @GetMapping("/{id}")
    public ResponseEntity<Driver> getDriverById(@PathVariable Integer id) {
        return ResponseEntity.ok(driverService.getDriverById(id));
    }

    @PostMapping
    public ResponseEntity<Driver> addDriver(@RequestBody Driver driver) {
        return ResponseEntity.ok(driverService.addDriver(driver));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Driver> updateDriver(@PathVariable Integer id, @RequestBody Driver driver) {
        driver.setId(id);
        return ResponseEntity.ok(driverService.updateDriver(driver));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDriver(@PathVariable Integer id) {
        driverService.deleteDriver(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/assign")
    public ResponseEntity<Void> assignDriverToTruck(@RequestParam Integer driverId, @RequestParam Integer truckId) {
        driverService.assignDriverToTruck(driverId, truckId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/update-status")
    public ResponseEntity<Void> updateDriverStatus(@RequestParam Integer driverId, @RequestParam String status) {
        driverService.updateDriverStatus(driverId, status);
        return ResponseEntity.ok().build();
    } */
}
