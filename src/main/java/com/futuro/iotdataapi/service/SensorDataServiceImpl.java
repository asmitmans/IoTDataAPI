package com.futuro.iotdataapi.service;

import com.futuro.iotdataapi.dto.SensorDataUploadRequest;
import com.futuro.iotdataapi.dto.SensorDataUploadResponse;
import com.futuro.iotdataapi.entity.Sensor;
import com.futuro.iotdataapi.entity.SensorData;
import com.futuro.iotdataapi.repository.SensorDataRepository;
import com.futuro.iotdataapi.repository.SensorRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SensorDataServiceImpl implements SensorDataService {

    private final SensorRepository sensorRepository;
    private final SensorDataRepository sensorDataRepository;

    public SensorDataServiceImpl(SensorRepository sensorRepository, SensorDataRepository sensorDataRepository) {
        this.sensorRepository = sensorRepository;
        this.sensorDataRepository = sensorDataRepository;
    }

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
}
