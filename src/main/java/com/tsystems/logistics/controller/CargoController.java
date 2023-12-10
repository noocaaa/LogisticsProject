package com.tsystems.logistics.controller;

import com.tsystems.logistics.dto.CargoDTO;
import com.tsystems.logistics.dto.OrderDTO;
import com.tsystems.logistics.entities.Cargo;
import com.tsystems.logistics.service.CargoService;
import com.tsystems.logistics.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@PreAuthorize("hasAuthority('ROLE_EMPLOYEE')")
@RequestMapping("/cargos")
public class CargoController {

    @Autowired
    private CargoService cargoService;

    @Autowired
    private OrderService orderService;

    @GetMapping
    public String cargosPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        List<OrderDTO> orderDTOs = orderService.getAllOrderDTOs();
        Page<CargoDTO> cargoPage = cargoService.getCargoPage(PageRequest.of(page, size));

        model.addAttribute("cargos", cargoPage.getContent());
        model.addAttribute("orders", orderDTOs);
        model.addAttribute("currentPage", cargoPage.getNumber());
        model.addAttribute("totalPages", cargoPage.getTotalPages());

        return "cargo";
    }

    @GetMapping("/add")
    public String addDriverForm(Model model, RedirectAttributes redirectAttributes) {

        if (redirectAttributes.containsAttribute("error")) {
            model.addAttribute("error", redirectAttributes.getFlashAttributes().get("error"));
        }

        return "addCargo";
    }


    @PostMapping("/addCargo")
    public String addDriver(@ModelAttribute Cargo cargo, RedirectAttributes redirectAttributes) {
        try {
            cargoService.addCargo(cargo);
            return "redirect:/cargos";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/cargos/add";
        }
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

    @PostMapping("/delete/{id}")
    public String deleteDriver(@PathVariable Integer id) {
        cargoService.deleteCargo(id);
        return "redirect:/cargos";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        Cargo cargo = cargoService.getCargoById(id);

        model.addAttribute("cargo", cargo);

        if (redirectAttributes.containsAttribute("error")) {
            model.addAttribute("error", redirectAttributes.getFlashAttributes().get("error"));
        }

        return "editCargo";
    }

    @PutMapping("/editCargo/{id}")
    public String updateDriver(@PathVariable Integer id, @ModelAttribute Cargo cargo, RedirectAttributes redirectAttributes) {
        try {
            cargoService.updateCargo(cargo);
            return "redirect:/cargos";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/cargos/edit/" + id;
        }
    }

}
