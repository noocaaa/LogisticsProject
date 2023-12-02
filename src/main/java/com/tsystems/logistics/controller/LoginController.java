package com.tsystems.logistics.controller;

import com.tsystems.logistics.service.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.tsystems.logistics.service.OrderService;
import com.tsystems.logistics.service.TruckService;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.tsystems.logistics.entities.Truck;

import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

@Controller
public class LoginController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private TruckService truckService;

    @Autowired
    private DriverService driverService;

    @GetMapping("/")
    public String root() {
        return "redirect:/dashboard";
    }

    @GetMapping("/login")
    public String login() {
        System.out.print("Hola amigo");
        return "minelogin";
    }

    @GetMapping("/home")
    public String home() {
        return "dashboard";
    }

    @GetMapping("/dashboard")
    public String getDashboardData(Model model) {
        Map<String, Integer> dashboardData = new HashMap<>();

        dashboardData.put("totalOrders", orderService.getTotalNumberOfOrders());
        dashboardData.put("completedOrders", orderService.getNumberOfCompletedOrders());
        dashboardData.put("inProgressOrders", orderService.getNumberOfInProgressOrders());

        dashboardData.put("maintenanceTrucks", truckService.getNOKStatus());
        dashboardData.put("driversRelax", driverService.getDriverswithMaximumWorkHours());

        List<Truck> availableTrucks = truckService.getAllTrucks().stream()
                .filter(truck -> "OK".equals(truck.getStatus()))
                .collect(Collectors.toList());


        model.addAttribute("dashboardData", dashboardData);
        model.addAttribute("trucks", availableTrucks);

        return "dashboard";
    }

    @GetMapping("/orders")
    public String ordersPage() {
        return "orders";
    }

    @GetMapping("/ordersAndTruck")
    public String ordersAndTruckPage() {
        return "ordersAndTruck";
    }

    @GetMapping("/settings")
    public String settingsPage() {
        return "settings";
    }
}
