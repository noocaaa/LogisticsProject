package com.tsystems.logistics.controller;

import com.tsystems.logistics.entities.Order;
import com.tsystems.logistics.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import java.util.List;

@RestController
@RequestMapping("/orderss")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping
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
    }

}
