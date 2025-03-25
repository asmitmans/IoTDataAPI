package com.futuro.iotdataapi.service;

import com.futuro.iotdataapi.dto.SensorRegisterRequest;
import com.futuro.iotdataapi.dto.SensorRegisterResponse;

public interface SensorService {
    public SensorRegisterResponse registerSensor(SensorRegisterRequest request,
                                                 String companyApiKey);
}
