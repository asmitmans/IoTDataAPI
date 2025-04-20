package com.futuro.iotdataapi.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.futuro.iotdataapi.dto.SensorCategoryDto;
import com.futuro.iotdataapi.dto.SensorRegisterRequest;
import com.futuro.iotdataapi.dto.SensorRegisterResponse;
import com.futuro.iotdataapi.dto.SensorResponse;
import com.futuro.iotdataapi.entity.Company;
import com.futuro.iotdataapi.entity.Location;
import com.futuro.iotdataapi.entity.Sensor;
import com.futuro.iotdataapi.exception.NotFoundException;
import com.futuro.iotdataapi.exception.UnauthorizedException;
import com.futuro.iotdataapi.repository.CompanyRepository;
import com.futuro.iotdataapi.repository.LocationRepository;
import com.futuro.iotdataapi.repository.SensorRepository;
import com.futuro.iotdataapi.util.CompanyResolver;
import com.futuro.iotdataapi.util.JwtUtils;

import jakarta.transaction.Transactional;

@Service
public class SensorServiceImpl implements SensorService {

  private final CompanyRepository companyRepository;
  private final LocationRepository locationRepository;
  private final SensorRepository sensorRepository;
  private final ObjectMapper objectMapper;
  private final JwtUtils jwtUtils;
  private final CompanyResolver companyResolver;

  public SensorServiceImpl(
      CompanyRepository companyRepository,
      LocationRepository locationRepository,
      SensorRepository sensorRepository,
      ObjectMapper objectMapper,
      JwtUtils jwtUtils,
      CompanyResolver companyResolver) {
    this.companyRepository = companyRepository;
    this.locationRepository = locationRepository;
    this.sensorRepository = sensorRepository;
    this.objectMapper = objectMapper;
    this.jwtUtils = jwtUtils;
    this.companyResolver = companyResolver;
  }

  @Override
  @Transactional
  public SensorRegisterResponse registerSensor(
      SensorRegisterRequest request, String rawAuthorization) {
    Company company;

    // ADMIN
    if (hasAdminRole()) {
      if (request.getCompanyId() == null) {
        throw new UnauthorizedException("Admin must provide companyId");
      }

      company =
          companyRepository
              .findById(request.getCompanyId())
              .orElseThrow(
                  () ->
                      new NotFoundException(
                          "Company not found with id: " + request.getCompanyId()));
    } else {
      company = companyResolver.resolveFromAuthorization(rawAuthorization);
    }

    Location location =
        locationRepository
            .findById(request.getLocationId())
            .filter(loc -> loc.getCompany().getId().equals(company.getId()))
            .orElseThrow(() -> new UnauthorizedException("Invalid location for this company"));

    String metaJson;
    try {
      metaJson =
          request.getSensorMeta() != null
              ? objectMapper.writeValueAsString(request.getSensorMeta())
              : "{}";
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Error converting sensorMeta to JSON", e);
    }

    Sensor sensor = new Sensor();
    sensor.setLocation(location);
    sensor.setSensorName(request.getSensorName());
    sensor.setCategory(request.getSensorCategory());
    sensor.setSensorMeta(metaJson);
    sensor.setSensorApiKey(UUID.randomUUID().toString());

    Sensor savedSensor = sensorRepository.save(sensor);

    return SensorRegisterResponse.builder()
        .id(savedSensor.getId())
        .message(savedSensor.getSensorName())
        .sensorApiKey(savedSensor.getSensorApiKey())
        .build();
  }

  @Override
  public SensorResponse getSensorById(Integer id, String authorization) {
    Sensor sensor =
        sensorRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Sensor not found with id: " + id));

    if (hasAdminRole()) {
      return toDTO(sensor);
    }

    Company company = companyResolver.resolveFromAuthorization(authorization);

    if (!sensor.getLocation().getCompany().getId().equals(company.getId())) {
      throw new UnauthorizedException("This sensor does not belong to your company");
    }

    return toDTO(sensor);
  }

  @Override
  public List<SensorResponse> getAllSensors(String authorization) {
    if (hasAdminRole()) {
      return sensorRepository.findAll().stream().map(this::toDTO).toList();
    }

    Company company = companyResolver.resolveFromAuthorization(authorization);

    List<Integer> locationIds =
        locationRepository
            .findAllByCompanyId(company.getId())
            .orElse(Collections.emptyList())
            .stream()
            .map(Location::getId)
            .toList();

    return sensorRepository.findByLocationIdIn(locationIds).stream().map(this::toDTO).toList();
  }

  @Override
  public Page<SensorResponse> findAllByLocationIdPageable(
      String rawAuthorization, Integer locationId, int pageIndex, int pageSize) {
    if (hasAdminRole()) {
      Pageable pageable = PageRequest.of(pageIndex, pageSize);
      return sensorRepository.findByLocationId(locationId, pageable).map(this::toDTO);
    }

    Company company = companyResolver.resolveFromAuthorization(rawAuthorization);

    Location location =
        locationRepository
            .findById(locationId)
            .filter(loc -> loc.getCompany().getId().equals(company.getId()))
            .orElseThrow(() -> new UnauthorizedException("Invalid location for this company"));

    Pageable pageable = PageRequest.of(pageIndex, pageSize);
    return sensorRepository.findByLocationId(location.getId(), pageable).map(this::toDTO);
  }

  @Override
  public List<SensorResponse> getAllSensorsByCompany(
      String rawAuthorization, Integer companyId, int locationId) {
    Company company;

    if (hasAdminRole()) {
      if (companyId == null) {
        throw new UnauthorizedException("Admin must provide companyId");
      }

      company =
          companyRepository
              .findById(companyId)
              .orElseThrow(() -> new NotFoundException("Company not found with id: " + companyId));

    } else {
      company = companyResolver.resolveFromAuthorization(rawAuthorization);

      if (!company.getId().equals(companyId)) {
        throw new UnauthorizedException("Unauthorized companyId");
      }
    }

    List<Integer> locationIds;

    if (locationId == -1) {
      locationIds =
          locationRepository
              .findAllByCompanyId(company.getId())
              .orElse(Collections.emptyList())
              .stream()
              .map(Location::getId)
              .toList();
    } else {
      Location location =
          locationRepository
              .findById(locationId)
              .filter(loc -> loc.getCompany().getId().equals(company.getId()))
              .orElseThrow(() -> new UnauthorizedException("Invalid location for this company"));

      locationIds = Collections.singletonList(location.getId());
    }

    return sensorRepository.findByLocationIdIn(locationIds).stream().map(this::toDTO).toList();
  }

  @Override
  @Transactional
  public SensorRegisterResponse updateSensor(
      Integer id, SensorRegisterRequest request, String rawAuthorization) {

    Sensor sensor =
        sensorRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Sensor not found with id: " + id));

    Company company;

    if (hasAdminRole()) {
      if (request.getCompanyId() == null) {
        throw new UnauthorizedException("Admin must provide companyId");
      }

      company =
          companyRepository
              .findById(request.getCompanyId())
              .orElseThrow(
                  () ->
                      new NotFoundException(
                          "Company not found with id: " + request.getCompanyId()));
    } else {
      company = companyResolver.resolveFromAuthorization(rawAuthorization);
    }

    if (!sensor.getLocation().getCompany().getId().equals(company.getId())) {
      throw new UnauthorizedException("This sensor does not belong to your company");
    }

    Location location =
        locationRepository
            .findById(request.getLocationId())
            .filter(loc -> loc.getCompany().getId().equals(company.getId()))
            .orElseThrow(() -> new UnauthorizedException("Invalid location for this company"));
    String metaJson;
    try {
      metaJson =
          request.getSensorMeta() != null
              ? objectMapper.writeValueAsString(request.getSensorMeta())
              : "{}";
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Error al convertir sensorMeta a JSON", e);
    }

    sensor.setLocation(location);
    sensor.setSensorName(request.getSensorName());
    sensor.setCategory(request.getSensorCategory());
    sensor.setSensorMeta(metaJson);

    Sensor savedSensor = sensorRepository.save(sensor);

    return SensorRegisterResponse.builder()
        .id(savedSensor.getId())
        .message(savedSensor.getSensorName())
        .sensorApiKey(savedSensor.getSensorApiKey())
        .build();
  }

  @Override
  @Transactional
  public void deleteSensor(Integer id, String authorization) {
    Sensor sensor =
        sensorRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Sensor not found with id: " + id));

    if (hasAdminRole()) {
      sensorRepository.delete(sensor);
      return;
    }

    Company company = companyResolver.resolveFromAuthorization(authorization);

    if (!sensor.getLocation().getCompany().getId().equals(company.getId())) {
      throw new UnauthorizedException("This sensor does not belong to your company");
    }

    sensorRepository.delete(sensor);
  }

  private SensorResponse toDTO(Sensor sensor) {
    Map<String, Object> meta;
    try {
      meta =
          sensor.getSensorMeta() != null
              ? objectMapper.readValue(sensor.getSensorMeta(), new TypeReference<>() {})
              : Map.of();
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Error parsing sensorMeta JSON", e);
    }

    return SensorResponse.builder()
        .id(sensor.getId())
        .sensorName(sensor.getSensorName())
        .sensorCategory(sensor.getCategory())
        .sensorApiKey(sensor.getSensorApiKey())
        .sensorMeta(meta)
        .locationId(sensor.getLocation().getId())
        .build();
  }

  private boolean hasAdminRole() {
    return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
        .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
  }
  
  public List<SensorCategoryDto> getSensorCategoriesWithCount() {
      return sensorRepository.findSensorCategoriesWithCount();
  }
}
