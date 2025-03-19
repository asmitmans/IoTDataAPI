package com.futuro.iotdataapi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AuthController {

    @GetMapping("/public")
    public String publicEndpoint() {
        return "Este endpoint es publico";
    }

    @GetMapping("/admin")
    public String adminEndpoint() {
        return "Este es endpoint es solo para ADMIN";
    }

    @PostMapping("/login")
    public String login() {
        return "Simulación de login: Aquí debería generarse el token JWT (se implementará en el siguiente paso).";
    }

}
