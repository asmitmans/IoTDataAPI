package com.futuro.iotdataapi.controller;

import com.futuro.iotdataapi.dto.SensorRegisterRequest;
import com.futuro.iotdataapi.dto.SensorRegisterResponse;
import com.futuro.iotdataapi.service.SensorService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sensors")
public class SensorController {

    private final SensorService sensorService;

    public SensorController(SensorService sensorService) {
        this.sensorService = sensorService;
    }

    @PostMapping
    public ResponseEntity<SensorRegisterResponse> registerSensor(
            @RequestHeader("Authorization") String authorization,
            @RequestBody @Valid SensorRegisterRequest request) {

        SensorRegisterResponse response = sensorService.registerSensor(request, authorization);
        return ResponseEntity.ok(response);
    }
}
