package com.futuro.iotdataapi.dto;

import java.util.Map;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SensorResponse {

	private Integer id;
	private String sensorName;
	private String sensorCategory;
	private String sensorApiKey;
	private Map<String, Object> sensorMeta;
	private LocationDTO location;
}
