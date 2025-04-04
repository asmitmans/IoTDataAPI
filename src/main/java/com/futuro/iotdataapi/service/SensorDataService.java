package com.futuro.iotdataapi.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.futuro.iotdataapi.dto.SensorDataResponse;
import com.futuro.iotdataapi.dto.SensorDataUploadRequest;
import com.futuro.iotdataapi.dto.SensorDataUploadResponse;


public interface SensorDataService {

    SensorDataUploadResponse receiveSensorData(SensorDataUploadRequest request, String rawAuthorization);

	Page<SensorDataResponse> findAllByLocationIdPageable(String authorization, long fromEpoch, long toEpoch,
			List<Integer> sensorIds, int pageIndex, int pageSize);

}
