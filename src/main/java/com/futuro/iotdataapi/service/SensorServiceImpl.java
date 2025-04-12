package com.futuro.iotdataapi.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.futuro.iotdataapi.util.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.futuro.iotdataapi.dto.CompanyDTO;
import com.futuro.iotdataapi.dto.LocationDTO;
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

import jakarta.transaction.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
public class SensorServiceImpl implements SensorService {

  private final CompanyRepository companyRepository;
  private final LocationRepository locationRepository;
  private final SensorRepository sensorRepository;
  private final ObjectMapper objectMapper;
  private final JwtUtils jwtUtils;

  public SensorServiceImpl(
      CompanyRepository companyRepository,
      LocationRepository locationRepository,
      SensorRepository sensorRepository,
      ObjectMapper objectMapper,
      JwtUtils jwtUtils) {
    this.companyRepository = companyRepository;
    this.locationRepository = locationRepository;
    this.sensorRepository = sensorRepository;
    this.objectMapper = objectMapper;
    this.jwtUtils = jwtUtils;
  }

  @Override
  public SensorResponse findById(Integer id) {
    Sensor sensor =
        sensorRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Sensor not found " + "with " + "id: " + id));
    return toDTO(sensor);
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

    String companyApiKey = extractApiKey(authorization);

    Company company =
        companyRepository
            .findByCompanyApiKey(companyApiKey)
            .orElseThrow(() -> new UnauthorizedException("Company not found or unauthorized"));

    if (!sensor.getLocation().getCompany().getId().equals(company.getId())) {
      throw new UnauthorizedException("This sensor does not belong to your company");
    }

    sensorRepository.delete(sensor);
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
      // API-KEY
      if (rawAuthorization != null && rawAuthorization.startsWith("ApiKey ")) {
        String companyApiKey = extractApiKey(rawAuthorization);
        company =
            companyRepository
                .findByCompanyApiKey(companyApiKey)
                .orElseThrow(() -> new UnauthorizedException("Company not found or unauthorized"));
      } else {
        // JWT
        Integer companyId = extractCompanyIdFromJwtToken();
        company =
            companyRepository
                .findById(companyId)
                .orElseThrow(() -> new UnauthorizedException("Company not found or unauthorized"));
      }
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
  @Transactional
  public SensorRegisterResponse updateSensor(
      Integer id, SensorRegisterRequest request, String rawAuthorization) {

    Sensor sensor =
        sensorRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Sensor not found " + "with " + "id: " + id));

    String companyApiKey = extractApiKey(rawAuthorization);

    Company company =
        companyRepository
            .findByCompanyApiKey(companyApiKey)
            .orElseThrow(
                () -> new UnauthorizedException("Company not " + "found " + "or unauthorized"));

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

  private String extractApiKey(String rawAuthorization) {
    if (rawAuthorization == null || !rawAuthorization.startsWith("ApiKey ")) {
      throw new RuntimeException("Missing or malformed Authorization " + "header");
    }
    return rawAuthorization.replace("ApiKey ", "").trim();
  }

  private Company getAuthorizedCompany(String rawAuthorization) {
    String companyApiKey = extractApiKey(rawAuthorization);
    return companyRepository
        .findByCompanyApiKey(companyApiKey)
        .orElseThrow(
            () -> new UnauthorizedException("Company not " + "found " + "or " + "unauthorized"));
  }

  @Override
  public Page<SensorResponse> findAllByLocationIdPageable(
      String rawAuthorization, Integer id, int pageIndex, int pageSize) {
    String companyApiKey = extractApiKey(rawAuthorization);

    Company company =
        companyRepository
            .findByCompanyApiKey(companyApiKey)
            .orElseThrow(
                () -> new UnauthorizedException("Company not " + "found " + "or unauthorized"));

    Location location =
        locationRepository
            .findById(id)
            .filter(loc -> loc.getCompany().getId().intValue() == company.getId().intValue())
            .orElseThrow(() -> new UnauthorizedException("Invalid location for this company"));

    Pageable pageable = PageRequest.of(pageIndex, pageSize);

    Page<Sensor> pages = sensorRepository.findByLocationId(location.getId(), pageable);

    return pages.map(this::toDTO);
  }

  @Override
  public List<SensorResponse> getAllSensors(
      String rawAuthorization, Integer companyId, int locationId) {
    String companyApiKey = extractApiKey(rawAuthorization);

    Company company =
        companyRepository
            .findByCompanyApiKey(companyApiKey)
            .orElseThrow(
                () -> new UnauthorizedException("Company not " + "found " + "or unauthorized"));

    if (company.getId().intValue() != companyId.intValue()) {
      throw new UnauthorizedException("Company not found or " + "unauthorized");
    }

    List<Integer> locationIds;

    if (locationId == -1) {
      locationIds =
          locationRepository.findAllByCompanyId(companyId).orElse(Collections.emptyList()).stream()
              .map(Location::getId)
              .collect(Collectors.toList());
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

  private SensorResponse toDTO(Sensor sensor) {
    CompanyDTO companyDto =
        CompanyDTO.builder()
            .id(sensor.getLocation().getCompany().getId())
            .companyName(sensor.getLocation().getCompany().getCompanyName())
            .companyApiKey(sensor.getLocation().getCompany().getCompanyApiKey())
            .build();
    LocationDTO locationDto =
        LocationDTO.builder()
            .id(sensor.getLocation().getId())
            .company(companyDto)
            .locationName(sensor.getLocation().getLocationName())
            .locationCountry(sensor.getLocation().getLocationCountry())
            .locationCity(sensor.getLocation().getLocationCity())
            .locationMeta(sensor.getLocation().getLocationMeta())
            .build();

    SensorResponse sensorResponse = null;
    try {
      Map<String, Object> meta =
          objectMapper.readValue(sensor.getSensorMeta(), new TypeReference<>() {});

      sensorResponse =
          SensorResponse.builder()
              .id(sensor.getId())
              .sensorName(sensor.getSensorName())
              .sensorCategory(sensor.getCategory())
              .sensorApiKey(sensor.getSensorApiKey())
              .location(locationDto)
              .sensorMeta(meta)
              .build();
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Error sensorMeta JSON", e);
    }

    return sensorResponse;
  }

  private boolean hasAdminRole() {
    return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
        .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
  }

  private Integer extractCompanyIdFromJwtToken() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
      throw new UnauthorizedException("No authenticated user");
    }

    String token = extractBearerToken(); // creamos esta función abajo
    DecodedJWT decodedJWT = jwtUtils.validateToken(token);
    return decodedJWT.getClaim("companyId").asInt();
  }

  private String extractBearerToken() {
    HttpServletRequest request =
        ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      throw new UnauthorizedException("No JWT token found in Authorization header");
    }

    return authHeader.substring(7); // remover "Bearer "
  }
}
