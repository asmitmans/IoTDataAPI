package com.futuro.iotdataapi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/companies")
public class CompanyController {

    @GetMapping
    public ResponseEntity<String> getCompanies() {
        return ResponseEntity.ok("Lista de compañías (simulación)");
    }
}
