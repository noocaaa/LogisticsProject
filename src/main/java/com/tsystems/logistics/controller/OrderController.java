package com.tsystems.logistics.controller;

import com.tsystems.logistics.dto.DriverDTO;
import com.tsystems.logistics.dto.TruckDTO;
import com.tsystems.logistics.dto.OrderDTO;

import com.tsystems.logistics.entities.Driver;
import com.tsystems.logistics.entities.Order;
import com.tsystems.logistics.entities.Truck;
import com.tsystems.logistics.service.OrderService;
import com.tsystems.logistics.service.TruckService;
import com.tsystems.logistics.service.DriverService;
import org.springframework.http.ResponseEntity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private TruckService truckService;

    @Autowired
    private DriverService driverService;

    @GetMapping
    public String ordersPage(Model model) {
        List<OrderDTO> orderDTOs = orderService.getAllOrderDTOs();
        model.addAttribute("orders", orderDTOs);
        return "orders";
    }


    @GetMapping("/add")
    public String addDriverForm(Model model, RedirectAttributes redirectAttributes) {
        if (redirectAttributes.containsAttribute("error")) {
            model.addAttribute("error", redirectAttributes.getFlashAttributes().get("error"));
        }
        return "addOrder";
    }


    @PostMapping("/addOrder")
    public String addDriver(@ModelAttribute OrderDTO orderDTO, RedirectAttributes redirectAttributes) {
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
            return "redirect:/orders" + orderId;
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/orders" + orderId;
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
        System.out.println(orderDTO.toString());
        model.addAttribute("order", orderDTO);
        return "detailsOrder";
    }
}
