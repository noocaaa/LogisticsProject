package com.tsystems.logistics.controller;

import com.tsystems.logistics.dto.CargoDTO;
import com.tsystems.logistics.dto.OrderDTO;
import com.tsystems.logistics.entities.Cargo;
import com.tsystems.logistics.service.CargoService;
import com.tsystems.logistics.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/cargos")
public class CargoController {

    @Autowired
    private CargoService cargoService;

    @Autowired
    private OrderService orderService;

    @GetMapping
    public String cargosPage(Model model) {
        List<Cargo> cargos = cargoService.getAllCargos();
        List<OrderDTO> orderDTOs = orderService.getAllOrderDTOs();
        List<CargoDTO> cargoDTOs = cargos.stream()
                .map(cargoService::convertToDTO)
                .collect(Collectors.toList());

        model.addAttribute("cargos", cargoDTOs);
        model.addAttribute("orders", orderDTOs);

        return "cargo";
    }

    @PostMapping("/assignCargoToOrder")
    public String assignCargoToOrder(@RequestParam("cargoId") Integer cargoId, @RequestParam("orderId") Integer orderId, RedirectAttributes redirectAttributes) {
        try {
            cargoService.assignCargoToOrder(cargoId, orderId);
            redirectAttributes.addFlashAttribute("success", "Cargo assigned to order successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/cargos";
    }


    /*

    @GetMapping
    public ResponseEntity<List<Cargo>> getAllCargos() {
        return ResponseEntity.ok(cargoService.getAllCargos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cargo> getCargoById(@PathVariable Integer id) {
        return ResponseEntity.ok(cargoService.getCargoById(id));
    }

    @PostMapping
    public ResponseEntity<Cargo> addCargo(@RequestBody Cargo cargo) {
        return ResponseEntity.ok(cargoService.addCargo(cargo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cargo> updateCargo(@PathVariable Integer id, @RequestBody Cargo cargo) {
        cargo.setId(id);
        return ResponseEntity.ok(cargoService.updateCargo(cargo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCargo(@PathVariable Integer id) {
        cargoService.deleteCargo(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/assign")
    public ResponseEntity<Void> assignCargoToOrder(@RequestParam Integer cargoId, @RequestParam Integer orderId) {
        cargoService.assignCargoToOrder(cargoId, orderId);
        return ResponseEntity.ok().build();
    }
     */
}
