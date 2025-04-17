package com.futuro.iotdataapi.service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.futuro.iotdataapi.dto.SensorCategoryKpiDto;
import com.futuro.iotdataapi.dto.SensorDataResponse;
import com.futuro.iotdataapi.dto.SensorDataUploadRequest;
import com.futuro.iotdataapi.dto.SensorDataUploadResponse;
import com.futuro.iotdataapi.entity.Company;
import com.futuro.iotdataapi.entity.Sensor;
import com.futuro.iotdataapi.entity.SensorData;
import com.futuro.iotdataapi.exception.UnauthorizedException;
import com.futuro.iotdataapi.repository.SensorDataRepository;
import com.futuro.iotdataapi.repository.SensorRepository;
import com.futuro.iotdataapi.util.CompanyResolver;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SensorDataServiceImpl implements SensorDataService {

  private final SensorRepository sensorRepository;
  private final SensorDataRepository sensorDataRepository;
  private final CompanyResolver companyResolver;

  @Override
  @Transactional
  public SensorDataUploadResponse receiveSensorData(
      SensorDataUploadRequest request, String rawAuthorization) {

    String sensorApiKey = extractApiKey(rawAuthorization);

    if (!request.getSensor_api_key().equals(sensorApiKey)) {
      throw new UnauthorizedException("Sensor API Key no coincide entre header y body");
    }

    Sensor sensor =
        sensorRepository
            .findBySensorApiKey(request.getSensor_api_key())
            .orElseThrow(() -> new UnauthorizedException("Sensor no registrado"));

    List<SensorData> registers = parseSensorData(request.getJson_data(), sensor);

    sensorDataRepository.saveAll(registers);

    return SensorDataUploadResponse.builder()
        .recordsSaved(registers.size())
        .message("Datos guardados correctamente")
        .build();
  }

  @Override
  public Page<SensorDataResponse> findAllByLocationIdPageable(
      String rawAuthorization,
      long fromEpoch,
      long toEpoch,
      List<Integer> sensorIds,
      int pageIndex,
      int pageSize) {

    if (sensorIds == null || sensorIds.isEmpty()) {
      throw new IllegalArgumentException("Debe proporcionar al menos un sensorId");
    }

    List<Sensor> sensors = sensorRepository.findAllById(sensorIds);

    if (sensors.size() != sensorIds.size()) {
      throw new IllegalArgumentException("Algunos sensores no existen");
    }

    if (hasAdminRole()) {
      // ADMIN
      Pageable pageable = PageRequest.of(pageIndex, pageSize);
      return sensorDataRepository
          .findAllBySensorIdAndTimestampBetween(sensorIds, fromEpoch, toEpoch, pageable)
          .map(this::toDTO);
    }

    // COMPANY + USER
    Company company = companyResolver.resolveFromAuthorization(rawAuthorization);

    boolean allSensorsOk =
        sensors.stream()
            .allMatch(
                sensor ->
                    sensor.getLocation() != null
                        && sensor.getLocation().getCompany() != null
                        && sensor.getLocation().getCompany().getId().equals(company.getId()));

    if (!allSensorsOk) {
      throw new UnauthorizedException("Algunos sensores no pertenecen a su compañía");
    }

    Pageable pageable = PageRequest.of(pageIndex, pageSize);
    Page<SensorData> pages =
        sensorDataRepository.findAllBySensorIdAndTimestampBetween(
            sensorIds, fromEpoch, toEpoch, pageable);

    return pages.map(this::toDTO);
  }

  public SensorDataResponse toDTO(SensorData sensorData) {
    return SensorDataResponse.builder()
        .id(sensorData.getId())
        .sensorId(sensorData.getSensor().getId())
        .timestamp(sensorData.getTimestamp())
        .valueName(sensorData.getValueName())
        .value(sensorData.getValue())
        .build();
  }

  private String extractApiKey(String rawAuthorization) {
    if (rawAuthorization == null || !rawAuthorization.startsWith("ApiKey ")) {
      throw new RuntimeException("Falta o es inválido el header Authorization");
    }
    return rawAuthorization.replace("ApiKey ", "").trim();
  }

  private List<SensorData> parseSensorData(List<Map<String, Object>> lectures, Sensor sensor) {
    List<SensorData> registers = new ArrayList<>();

    for (Map<String, Object> lecture : lectures) {
      Object datetimeObj = lecture.get("datetime");

      if (!(datetimeObj instanceof Number)) {
        throw new IllegalArgumentException("El campo 'datetime' debe ser un número (epoch).");
      }

      Long timestamp = ((Number) datetimeObj).longValue();

      for (Map.Entry<String, Object> entry : lecture.entrySet()) {
        String key = entry.getKey();

        if (!key.equals("datetime")) {
          Double value = parseDouble(entry.getValue(), key);

          registers.add(
              SensorData.builder()
                  .sensor(sensor)
                  .timestamp(timestamp)
                  .valueName(key)
                  .value(value)
                  .build());
        }
      }
    }

    return registers;
  }

  private Double parseDouble(Object valueObj, String fieldName) {
    try {
      return Double.parseDouble(valueObj.toString());
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("El campo '" + fieldName + "' debe ser un número.");
    }
  }

  private boolean hasAdminRole() {
    return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
        .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
  }
  
  @Override
  public List<SensorCategoryKpiDto> getCategoryKpisForLocation(Integer locationId) {
	    long oneDayAgo = (System.currentTimeMillis() - Duration.ofDays(1).toMillis())/1000;
	    List<SensorData> data = sensorDataRepository.findByLocationAndRecent(locationId, oneDayAgo);

	    return data.stream()
	        .collect(Collectors.groupingBy(sd -> sd.getSensor().getCategory()))
	        .entrySet().stream()
	        .map(entry -> {
	            String category = entry.getKey();
	            List<SensorData> group = entry.getValue();

	            SensorData last = group.stream()
	                .max(Comparator.comparingLong(SensorData::getTimestamp))
	                .orElse(null);

	            return SensorCategoryKpiDto.builder()
	                .category(category)
	                .lastValue(last != null ? last.getValue() : null)
	                .lastTimestamp(last != null ? toLocalDateTime(last.getTimestamp()) : null)
	                .minValue(group.stream().mapToDouble(SensorData::getValue).min().orElse(0))
	                .maxValue(group.stream().mapToDouble(SensorData::getValue).max().orElse(0))
	                .averageValue(group.stream().mapToDouble(SensorData::getValue).average().orElse(0))
	                .totalReadings((long) group.size())
	                .build();
	        }).toList();
	}
  
  private LocalDateTime toLocalDateTime(Long ts) {
	    return Instant.ofEpochMilli(ts).atZone(ZoneId.systemDefault()).toLocalDateTime();
	}


}
