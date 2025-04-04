package com.futuro.iotdataapi.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SensorDataResponse {

	private Integer id;
    private SensorResponse sensor;
    private Long timestamp;
    private String valueName;
    private Double value;

}
