package com.futuro.iotdataapi.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.futuro.iotdataapi.dto.SensorRegisterRequest;
import com.futuro.iotdataapi.dto.SensorRegisterResponse;
import com.futuro.iotdataapi.dto.SensorResponse;

import jakarta.validation.Valid;

public interface SensorService {
    public SensorRegisterResponse registerSensor(SensorRegisterRequest request,
                                                 String companyApiKey);

	public Page<SensorResponse> findAllByLocationIdPageable(String rawAuthorization, Integer id, int pageIndex,
			int pageSize);

	public List<SensorResponse> getAllSensors(String authorization, Integer companyId, int locationId);

	public SensorRegisterResponse updateSensor(Integer id, @Valid SensorRegisterRequest request, String authorization);

	public SensorResponse findById(Integer id);

	public void deleteSensor(Integer id, String authorization);

}
