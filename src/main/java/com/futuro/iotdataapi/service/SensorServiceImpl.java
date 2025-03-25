package com.futuro.iotdataapi.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.futuro.iotdataapi.dto.SensorRegisterRequest;
import com.futuro.iotdataapi.dto.SensorRegisterResponse;
import com.futuro.iotdataapi.entity.Company;
import com.futuro.iotdataapi.entity.Location;
import com.futuro.iotdataapi.entity.Sensor;
import com.futuro.iotdataapi.repository.CompanyRepository;
import com.futuro.iotdataapi.repository.LocationRepository;
import com.futuro.iotdataapi.repository.SensorRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SensorServiceImpl implements SensorService {

    private final CompanyRepository companyRepository;
    private final LocationRepository locationRepository;
    private final SensorRepository sensorRepository;
    private final ObjectMapper objectMapper;

    public SensorServiceImpl(CompanyRepository companyRepository,
                             LocationRepository locationRepository,
                             SensorRepository sensorRepository, ObjectMapper objectMapper) {
        this.companyRepository = companyRepository;
        this.locationRepository = locationRepository;
        this.sensorRepository = sensorRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public SensorRegisterResponse registerSensor(SensorRegisterRequest request,
                                                 String rawAuthorization) {

        String companyApiKey = extractApiKey(rawAuthorization);

        Company company = companyRepository.findByCompanyApiKey(companyApiKey)
                .orElseThrow(() -> new RuntimeException("Company not found or unauthorized"));

        Location location = locationRepository.findById(request.getLocationId())
                .filter(loc -> loc.getCompany().getId().equals(company.getId()))
                .orElseThrow(() -> new RuntimeException("Invalid location for this company"));

        String metaJson;
        try {
            metaJson = request.getSensorMeta() != null
                    ? objectMapper.writeValueAsString(request.getSensorMeta())
                    : "{}";
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error al convertir sensorMeta a JSON", e);
        }

        Sensor sensor = new Sensor();
        sensor.setLocation(location);
        sensor.setSensorName(request.getSensorName());
        sensor.setCategory(request.getSensorCategory());
        sensor.setSensorMeta(metaJson);
        sensor.setSensorApiKey(UUID.randomUUID().toString());

        Sensor savedSensor = sensorRepository.save(sensor);

        return new SensorRegisterResponse(
                savedSensor.getId(),
                savedSensor.getSensorApiKey(),
                savedSensor.getSensorName()
        );

    }

    private String extractApiKey(String rawAuthorization) {
        if (rawAuthorization == null || !rawAuthorization.startsWith("ApiKey ")) {
            throw new RuntimeException("Missing or malformed Authorization header");
        }
        return rawAuthorization.replace("ApiKey ", "").trim();
    }

}