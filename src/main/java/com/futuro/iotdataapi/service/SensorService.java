package com.futuro.iotdataapi.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.futuro.iotdataapi.dto.SensorCategoryDto;
import com.futuro.iotdataapi.dto.SensorRegisterRequest;
import com.futuro.iotdataapi.dto.SensorRegisterResponse;
import com.futuro.iotdataapi.dto.SensorResponse;

import jakarta.validation.Valid;

public interface SensorService {

  SensorRegisterResponse registerSensor(SensorRegisterRequest request, String companyApiKey);

  SensorResponse getSensorById(Integer id, String authorization);

  List<SensorResponse> getAllSensors(String authorization);

  Page<SensorResponse> findAllByLocationIdPageable(
      String rawAuthorization, Integer id, int pageIndex, int pageSize);

  List<SensorResponse> getAllSensorsByCompany(
      String authorization, Integer companyId, int locationId);

  SensorRegisterResponse updateSensor(
      Integer id, @Valid SensorRegisterRequest request, String authorization);

  void deleteSensor(Integer id, String authorization);

  List<SensorCategoryDto> getSensorCategoriesWithCount();
}
