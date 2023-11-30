package com.tsystems.logistics.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InitialController {

    @GetMapping("/")
    public String initialPage(Model model) {
        return "initial";
    }
}
