package com.tsystems.logistics.controller;
import com.tsystems.logistics.dto.DistanceDTO;
import com.tsystems.logistics.service.*;

import com.tsystems.logistics.entities.*;

import com.tsystems.logistics.dto.DriverDTO;
import com.tsystems.logistics.dto.CityDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.*;

import com.tsystems.logistics.entities.Truck;

import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

@Controller
public class LoginController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private CityService cityService;

    @Autowired
    private DistanceService distanceService;

    @Autowired
    private TruckService truckService;

    @Autowired
    private DriverService driverService;

    @Autowired
    private CargoService cargoService;

    @Autowired
    private UserService userService;

    @Autowired
    private WaypointService waypointService;

    @GetMapping("/")
    public String root() {
        return "redirect:/dashboard";
    }

    @GetMapping("/home")
    public String home() {
        return "dashboard";
    }

    @GetMapping("/403")
    public String forbidden() {
        return "403";
    }

    @GetMapping("/login")
    public String login() {
        return "minelogin";
    }

    @GetMapping("/dashboard")
    public String getDashboardData(Principal principal, Model model) {

        String username = principal.getName();
        Set<String> roles = userService.getUserRoles(username);;

        if (roles.contains("ROLE_DRIVER")) {
            DriverDTO driverDTO = driverService.getDriverDashboardData(username);
            model.addAttribute("driverDTO", driverDTO);

            return "driverDashboard";
        } else {

            Map<String, Integer> dashboardData = new HashMap<>();

            dashboardData.put("totalOrders", orderService.getTotalNumberOfOrders());
            dashboardData.put("completedOrders", orderService.getNumberOfCompletedOrders());
            dashboardData.put("inProgressOrders", orderService.getNumberOfInProgressOrders());

            dashboardData.put("maintenanceTrucks", truckService.getNOKStatus());
            dashboardData.put("driversRelax", driverService.getDriverswithMaximumWorkHours());

            dashboardData.put("totalCargos", cargoService.getAllCargos().size());
            dashboardData.put("shippedCargos", cargoService.cargoByStatus("shipped"));
            dashboardData.put("readyCargos", cargoService.cargoByStatus("ready"));
            dashboardData.put("deliveredCargos", cargoService.cargoByStatus("delivered"));


            List<Truck> availableTrucks = truckService.getAllTrucks().stream()
                    .filter(truck -> "OK".equals(truck.getStatus()))
                    .collect(Collectors.toList());


            model.addAttribute("dashboardData", dashboardData);
            model.addAttribute("trucks", availableTrucks);

            return "dashboard";
        }
    }

    @PostMapping("/updateDriverStatus")
    public String updateDriverStatus(Principal principal, @RequestParam("status") String status, RedirectAttributes redirectAttributes) {
        try {
            String username = principal.getName();
            Integer driverId = driverService.getDriverByUsername(username).getId();

            if (status.equals("Behind the wheel")) {
                status = "DRIVING";
            } else if (status.equals("Rest")) {
                status = "REST";
            } else if (status.equals("Second driver")) {
                status = "SECOND_DRIVER";
            } else if(status.equals("Loading and unloading work")) {
                status = "LOADING_UNLOADING";
            }

            driverService.updateDriverStatus(driverId, status);
            return "redirect:/dashboard";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/dashboard";
        }
    }
    @PostMapping("/updateCargoAndOrderStatus")
    public String updateCargoAndOrderStatus(@RequestParam("waypointId") Integer waypointId, @RequestParam("orderStatus") String orderStatus, RedirectAttributes redirectAttributes) {
        try {
            Waypoint waypoint = waypointService.getWaypointById(waypointId);
            Cargo cargo = cargoService.getCargoById(waypoint.getCargo().getId());
            Order order = orderService.getOrderById(waypoint.getOrder().getId());

            if ("Uploaded".equals(orderStatus)) {
                cargo.setStatus("shipped");
            } else if ("Unloaded".equals(orderStatus)) {
                if ("shipped".equals(cargo.getStatus())) {
                    cargo.setStatus("delivered");
                } else {
                    redirectAttributes.addFlashAttribute("error", "Cargo must be shipped before it can be delivered");
                    return "redirect:/dashboard";
                }
                cargo.setStatus("delivered");
            }
            cargoService.updateCargo(cargo);

            boolean isOrderCompleted = orderService.checkAndUpdateOrderStatus(waypoint.getOrder().getId());

            if (isOrderCompleted) {
                order.setCompleted(true);
                orderService.updateOrder(order, true);
            }

            return "redirect:/dashboard";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/dashboard";
        }
    }

    @PreAuthorize("hasAuthority('ROLE_EMPLOYEE')")
    @GetMapping("/planning")
    public String planning() {
        return "template";
    }

    @ResponseBody
    @GetMapping("/api/cities")
    public List<CityDTO> apicities() {
        List<City> cities= cityService.getAllCities();
        List<CityDTO> cityDTOs = new ArrayList<>();

        for (City city : cities) {
            cityDTOs.add(cityService.convertToDTO(city));
        }

        return cityDTOs;
    }

    @ResponseBody
    @GetMapping("/api/distances")
    public List<DistanceDTO> apidistance() {
        return distanceService.getAllDistanceDTOs();
    }


}
