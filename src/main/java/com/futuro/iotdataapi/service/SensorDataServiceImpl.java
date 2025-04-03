package com.futuro.iotdataapi.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.futuro.iotdataapi.dto.SensorDataResponse;
import com.futuro.iotdataapi.dto.SensorDataUploadRequest;
import com.futuro.iotdataapi.dto.SensorDataUploadResponse;
import com.futuro.iotdataapi.dto.SensorResponse;
import com.futuro.iotdataapi.entity.Company;
import com.futuro.iotdataapi.entity.Sensor;
import com.futuro.iotdataapi.entity.SensorData;
import com.futuro.iotdataapi.exception.UnauthorizedException;
import com.futuro.iotdataapi.repository.CompanyRepository;
import com.futuro.iotdataapi.repository.SensorDataRepository;
import com.futuro.iotdataapi.repository.SensorRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SensorDataServiceImpl implements SensorDataService {

    private final SensorRepository sensorRepository;
    private final SensorDataRepository sensorDataRepository;
    private final CompanyRepository companyRepository;

    @Override
    @Transactional
    public SensorDataUploadResponse receiveSensorData(SensorDataUploadRequest request, String rawAuthorization) {

        String sensorApiKey = extractApiKey(rawAuthorization);

        if (!request.getSensor_api_key().equals(sensorApiKey)) {
            throw new RuntimeException("Sensor API Key no coincide entre header y body");
        }

        Sensor sensor = sensorRepository.findBySensorApiKey(request.getSensor_api_key())
                .orElseThrow(() -> new RuntimeException("Sensor no registrado"));

        List<SensorData> registers = parseSensorData(request.getJson_data(), sensor);

        sensorDataRepository.saveAll(registers);

        return SensorDataUploadResponse.builder()
                .recordsSaved(registers.size())
                .message("Datos guardados correctamente")
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
                                    .build()
                    );
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

	@Override
	public Page<SensorDataResponse> findAllByLocationIdPageable(String rawAuthorization, long fromEpoch, long toEpoch,
			List<Integer> sensorIds, int pageIndex, int pageSize) {
		
		String companyApiKey = extractApiKey(rawAuthorization);

        Company company = companyRepository.findByCompanyApiKey(companyApiKey)
                .orElseThrow(() -> new UnauthorizedException("Company not found or unauthorized"));
        
        List<Sensor> sensors = sensorRepository.findAllById(sensorIds);
        
        if (sensors.size() != sensorIds.size()) {
        	throw new IllegalArgumentException("Data cannot be delivered");
        }
        
        boolean allSensorsOk = sensors.stream()
                .allMatch(sensor -> sensor.getLocation() != null &&
                                    sensor.getLocation().getCompany() != null &&
                                    sensor.getLocation().getCompany().getId().equals(company.getId()));

        if (!allSensorsOk) {
        	throw new IllegalArgumentException("Data cannot be delivered");
        }
		
        Pageable pageable = PageRequest.of(pageIndex, pageSize);

        Page<SensorData> pages = sensorDataRepository.findAllBySensorIdAndTimestampBetween(
                sensorIds, fromEpoch, toEpoch, pageable);

        return pages.map(this::toDTO);
	}
	
	public SensorDataResponse toDTO(SensorData sensorData) {
	    return SensorDataResponse.builder()
	            .id(sensorData.getId())
	            .sensor(toSensorResponse(sensorData.getSensor()))
	            .timestamp(sensorData.getTimestamp())
	            .valueName(sensorData.getValueName())
	            .value(sensorData.getValue())
	            .build();
	}

	public SensorResponse toSensorResponse(Sensor sensor) {
	    return SensorResponse.builder()
	            .id(sensor.getId())
	            .sensorName(sensor.getSensorName())
	            .sensorCategory(sensor.getCategory())
	            .sensorApiKey(sensor.getSensorApiKey())
	            //.sensorMeta(sensor.getSensorMeta())
	            .build();
	}

}
