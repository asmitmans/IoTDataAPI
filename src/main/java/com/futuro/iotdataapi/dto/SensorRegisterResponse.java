package com.futuro.iotdataapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SensorRegisterResponse {
    private Integer id;
    private String message;
    private String sensorApiKey;
}
