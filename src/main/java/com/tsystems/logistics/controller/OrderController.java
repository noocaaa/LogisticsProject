package com.tsystems.logistics.controller;

import com.tsystems.logistics.dto.OrderDTO;
import com.tsystems.logistics.entities.Order;
import com.tsystems.logistics.entities.Truck;
import com.tsystems.logistics.service.OrderService;
import com.tsystems.logistics.service.TruckService;

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

        List<Truck> availableTrucks = truckService.getAllTrucks().stream()
                .filter(truck -> "OK".equals(truck.getStatus()))
                .collect(Collectors.toList());

        model.addAttribute("availableTrucks", availableTrucks);

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

    @GetMapping("/details/{id}")
    public String details(@PathVariable Integer id, Model model) {
        OrderDTO orderDTO = orderService.getOrderDTOById(id);
        System.out.println(orderDTO.toString());
        model.addAttribute("order", orderDTO);
        return "detailsOrder";
    }


    /*@GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Integer id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        return ResponseEntity.ok(orderService.createOrder(order));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Order> updateOrder(@PathVariable Integer id, @RequestBody Order order) {
        order.setId(id);
        return ResponseEntity.ok(orderService.updateOrder(order));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelOrder(@PathVariable Integer id) {
        orderService.cancelOrder(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/status/{completed}")
    public ResponseEntity<Void> changeOrderStatus(@PathVariable Integer id, @PathVariable boolean completed) {
        orderService.changeOrderStatus(id, completed);
        return ResponseEntity.ok().build();
    }*/

}
