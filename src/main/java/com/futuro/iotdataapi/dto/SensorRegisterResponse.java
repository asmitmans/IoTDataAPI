package com.futuro.iotdataapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class SensorRegisterResponse {
    private Integer id;
    private String message;
    private String sensorApiKey;
}
