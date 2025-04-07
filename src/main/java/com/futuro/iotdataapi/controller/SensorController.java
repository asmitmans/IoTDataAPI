package com.futuro.iotdataapi.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.futuro.iotdataapi.dto.SensorRegisterRequest;
import com.futuro.iotdataapi.dto.SensorRegisterResponse;
import com.futuro.iotdataapi.dto.SensorResponse;
import com.futuro.iotdataapi.service.SensorService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/sensors")
public class SensorController {
	
	private static final String PAGE_DEFAULT_SIZE = "7";
	private static final String LOCATION_DEFAULT = "-1";

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
    
    @GetMapping("/location/{id}")
    public ResponseEntity<Page<SensorResponse>> findAllByLocationId(
    		@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization, 
    		@PathVariable Integer id,
    		@RequestParam("page") int pageIndex,
			@RequestParam(value = "size", required = false, 
			defaultValue = PAGE_DEFAULT_SIZE) int pageSize) {
    	
    	Page<SensorResponse> pageDto = sensorService.findAllByLocationIdPageable(authorization, id, pageIndex, pageSize);

		return ResponseEntity.ok(pageDto);
    }
    
    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<SensorResponse>> getAllSensors(
    		@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization, 
    		@PathVariable Integer companyId,
    		@RequestParam(value = "location", required = false, 
			defaultValue = LOCATION_DEFAULT) int locationId) {
        return ResponseEntity.ok(sensorService.getAllSensors(authorization, companyId, locationId));
    }
    
}
