package com.tsystems.logistics.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class LoginController {

    @GetMapping("/")
    public String root() {
        return "redirect:/home";
    }

    @GetMapping("/login")
    public String login() {
        System.out.print("Hola amigo");
        return "minelogin";
    }

    @GetMapping("/home")
    public String home() {
        return "initial";
    }
}
