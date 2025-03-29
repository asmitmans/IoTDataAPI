package com.futuro.iotdataapi.service;

import com.futuro.iotdataapi.dto.SensorDataUploadRequest;
import com.futuro.iotdataapi.dto.SensorDataUploadResponse;


public interface SensorDataService {

    SensorDataUploadResponse receiveSensorData(SensorDataUploadRequest request, String rawAuthorization);

}
