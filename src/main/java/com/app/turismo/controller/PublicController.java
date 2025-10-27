package com.app.turismo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/public")
public class PublicController {

    @GetMapping("/home")
    public String homePage() {
        return "index"; // Devuelve la plantilla index.html
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login"; // Devuelve la plantilla login.html
    }

    @GetMapping("/armar-paquete")
    public String mostrarPaginaPaquete() {
        return "armar-paquete"; // Devuelve la plantilla armar-paquete.html
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register"; // Devuelve la plantilla register.html
    }

    @GetMapping("/mis-paquetes")
    public String misPaquetesPage() {
        return "mis-paquetes"; // Devuelve la plantilla mis-paquetes.html

    }
}

@Controller
class RootController {
    @GetMapping("/")
    public String redirectToHome() {
        return "redirect:/public/home";
    }
}