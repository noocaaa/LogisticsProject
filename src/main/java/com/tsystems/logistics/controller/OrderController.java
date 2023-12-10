package com.tsystems.logistics.controller;

import com.tsystems.logistics.dto.*;


import com.tsystems.logistics.entities.Cargo;
import com.tsystems.logistics.entities.City;
import com.tsystems.logistics.entities.Order;
import com.tsystems.logistics.entities.Truck;
import com.tsystems.logistics.service.CargoService;
import com.tsystems.logistics.service.*;
import org.springframework.http.ResponseEntity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@PreAuthorize("hasAuthority('ROLE_EMPLOYEE')")
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private TruckService truckService;

    @Autowired
    private DriverService driverService;

    @Autowired
    private CityService cityService;

    @Autowired
    private CargoService cargoService;

    @GetMapping
    public String ordersPage(Model model) {
        List<OrderDTO> orderDTOs = orderService.getAllOrderDTOs();
        model.addAttribute("orders", orderDTOs);
        return "orders";
    }


    @GetMapping("/add")
    public String addOrderForm(Model model, RedirectAttributes redirectAttributes) {
        List<City> cities = cityService.getAllCities();
        List<CityDTO> cityDTOs = cities.stream()
                .map(city -> new CityDTO(city.getId(), city.getName()))
                .collect(Collectors.toList());
        model.addAttribute("cities", cityDTOs);

        List<Cargo> cargos = cargoService.getAvailableCargos();
        List<CargoDTO> cargosDTOs = cargoService.convertToDTOList(cargos);

        model.addAttribute("cargos", cargosDTOs);

        if (redirectAttributes.containsAttribute("error")) {
            model.addAttribute("error", redirectAttributes.getFlashAttributes().get("error"));
        }
        return "addOrder";
    }


    @PostMapping("/addOrder")
    public String addOrder(@ModelAttribute OrderDTO orderDTO, RedirectAttributes redirectAttributes) {
        try {
            Order order = orderService.convertToEntity(orderDTO);
            orderService.createOrder(order);
            return "redirect:/orders";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/orders/add";
        }
    }

    @GetMapping("/assignTruck/{orderId}")
    public ResponseEntity<List<TruckDTO>> showAssignTruckForm(@PathVariable Integer orderId, Model model) {
        List<Truck> availableTrucks = truckService.getAvailableTrucksForOrder(orderId);

        List<TruckDTO> truckDTOs = availableTrucks.stream()
                .map(truckService::convertToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(truckDTOs);
    }

    @PostMapping("/assignTruck/{orderId}")
    public String assignTruckToOrder(@PathVariable Integer orderId, @RequestParam Integer truckId, RedirectAttributes redirectAttributes) {
        try {
            orderService.assignTruckToOrder(orderId, truckId);
            return "redirect:/orders";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/orders";
        }
    }

    @GetMapping("/assignDriver/{orderId}")
    public ResponseEntity<List<DriverDTO>>  showAssignDriverForm(@PathVariable Integer orderId, Model model) {
        Order order = orderService.getOrderById(orderId);

        OrderDTO orderDTO = orderService.convertOrderToDTO(order);
        List<DriverDTO> availableDrivers = driverService.getAvailableDriversForOrder(orderDTO, orderDTO.getTruck().getId());

        return ResponseEntity.ok(availableDrivers);
    }

    @PostMapping("/assignDriver/{orderId}")
    public String assignDriverToOrder(@PathVariable Integer orderId, @RequestParam List<Integer> driverIds, RedirectAttributes redirectAttributes) {
        try {
            orderService.assignDriversToOrder(orderId, driverIds);
            return "redirect:/orders";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/orders/assignDriver/" + orderId;
        }
    }


    @GetMapping("/details/{id}")
    public String details(@PathVariable Integer id, Model model) {
        OrderDTO orderDTO = orderService.getOrderDTOById(id);
        model.addAttribute("order", orderDTO);
        return "detailsOrder";
    }
}
