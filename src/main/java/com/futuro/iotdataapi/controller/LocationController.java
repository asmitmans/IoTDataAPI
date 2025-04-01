package com.futuro.iotdataapi.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.futuro.iotdataapi.dto.LocationDTO;
import com.futuro.iotdataapi.dto.LocationRequestDTO;
import com.futuro.iotdataapi.service.LocationServiceImpl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor
public class LocationController {
	
	private static final String PAGE_DEFAULT_SIZE = "7";

    private final LocationServiceImpl locationService;

    @GetMapping
    public ResponseEntity<List<LocationDTO>> getAllLocations() {
        return ResponseEntity.ok(locationService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LocationDTO> getLocationById(@PathVariable Integer id) {
        Optional<LocationDTO> location = locationService.findById(id);
        return location.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<LocationDTO> createLocation(@Valid @RequestBody LocationRequestDTO request) {
        return ResponseEntity.ok(locationService.save(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LocationDTO> updateLocation(@PathVariable Integer id, @Valid @RequestBody LocationRequestDTO request) {
        return ResponseEntity.ok(locationService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLocation(@PathVariable Integer id) {
        locationService.delete(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping(value = "/paging")
	public ResponseEntity<Page<LocationDTO>> getClientsPaging(@RequestParam("page") int pageIndex,
			@RequestParam(value = "size", required = false, defaultValue = PAGE_DEFAULT_SIZE) int pageSize) {

		Page<LocationDTO> pageDto = locationService.findAllPageable(pageIndex, pageSize);

		return ResponseEntity.ok(pageDto);
	}
}
