package com.futuro.iotdataapi.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.futuro.iotdataapi.dto.SensorCategoryDto;
import com.futuro.iotdataapi.dto.SensorCategoryKpiDto;
import com.futuro.iotdataapi.service.SensorDataService;
import com.futuro.iotdataapi.service.SensorService;
import com.futuro.iotdataapi.service.UserDetailsServiceImpl;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard resource")
public class DashboardController {

	private final SensorService sensorService;	
	private final SensorDataService sensorDataService;
	private final UserDetailsServiceImpl userDetailsService;
	
	@GetMapping("/active-users-count")
	  public long getActiveUsersCount() {
	    return userDetailsService.getActiveUsersCount();
	  }
	
	@GetMapping("/sensors/categories")
    public List<SensorCategoryDto> getSensorCategories() {
        return sensorService.getSensorCategoriesWithCount();
    }
	
	@GetMapping("/kpi/categories/location/{locationId}")
    public List<SensorCategoryKpiDto> getKpisByLocation(@PathVariable Integer locationId) {
        return sensorDataService.getCategoryKpisForLocation(locationId);
    }
}
