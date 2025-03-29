package com.futuro.iotdataapi.controller;

import com.futuro.iotdataapi.dto.SensorDataUploadRequest;
import com.futuro.iotdataapi.dto.SensorDataUploadResponse;
import com.futuro.iotdataapi.service.SensorDataService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/sensor_data")
public class SensorDataController {

    private final SensorDataService sensorDataService;

    public SensorDataController(SensorDataService sensorDataService) {
        this.sensorDataService = sensorDataService;
    }

    @PostMapping
    public ResponseEntity<SensorDataUploadResponse> receiveSensorData(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @Valid @RequestBody SensorDataUploadRequest request) {

        SensorDataUploadResponse response =
                sensorDataService.receiveSensorData(request, authorization);
        return ResponseEntity.status(201).body(response);
    }

}
