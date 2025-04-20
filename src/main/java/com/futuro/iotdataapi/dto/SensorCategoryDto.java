package com.futuro.iotdataapi.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SensorCategoryDto {

	private String label;
	private Long value;

}
