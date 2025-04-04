package com.futuro.iotdataapi.service;

import org.springframework.data.domain.Page;

import com.futuro.iotdataapi.dto.SensorRegisterRequest;
import com.futuro.iotdataapi.dto.SensorRegisterResponse;
import com.futuro.iotdataapi.dto.SensorResponse;

public interface SensorService {
    public SensorRegisterResponse registerSensor(SensorRegisterRequest request,
                                                 String companyApiKey);

	public Page<SensorResponse> findAllByLocationIdPageable(String rawAuthorization, Integer id, int pageIndex,
			int pageSize);
}
