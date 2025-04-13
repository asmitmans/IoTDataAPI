package com.futuro.iotdataapi.consumer;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.futuro.iotdataapi.dto.SensorDataUploadRequest;
import com.futuro.iotdataapi.dto.SensorDataUploadResponse;
import com.futuro.iotdataapi.service.SensorDataService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConsumerService {

	private final String API_KEY = "ApiKey ";

	private final SensorDataService sensorDataService;
	private final ObjectMapper objectMapper;

	public void proccesMessage(String message) {
		log.debug("Message: {}", message);

		try {
			SensorDataUploadRequest request = objectMapper.readValue(message, SensorDataUploadRequest.class);

			String apiKey = request.getSensor_api_key();

			SensorDataUploadResponse response = sensorDataService.receiveSensorData(request, API_KEY + apiKey);

			log.debug(response.toString());

		} catch (Exception e) {
			log.error("Error invalid JSON", e);
		}
	}
}
