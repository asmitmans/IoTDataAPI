package com.futuro.iotdataapi.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.futuro.iotdataapi.dto.SensorDataResponse;
import com.futuro.iotdataapi.dto.SensorDataUploadRequest;
import com.futuro.iotdataapi.dto.SensorDataUploadResponse;
import com.futuro.iotdataapi.service.SensorDataService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/sensor_data")
public class SensorDataController {
	
	private static final String PAGE_DEFAULT_SIZE = "7";

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
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<SensorDataResponse>> getSensorData(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @RequestParam("from") long fromEpoch,
            @RequestParam("to") long toEpoch,
            @RequestParam("sensor_id") List<Integer> sensorIds,
            @RequestParam("page") int pageIndex,
			@RequestParam(value = "size", required = false, 
			defaultValue = PAGE_DEFAULT_SIZE) int pageSize) {
                
        Page<SensorDataResponse> pageDto = sensorDataService.findAllByLocationIdPageable(authorization, fromEpoch, toEpoch, sensorIds, pageIndex, pageSize);

		return ResponseEntity.ok(pageDto);
    }
}
