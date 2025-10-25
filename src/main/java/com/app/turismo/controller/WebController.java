package com.app.turismo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping("/armar-paquete")
    public String mostrarPaginaPaquete() {
        return "armar-paquete";
    }
}
