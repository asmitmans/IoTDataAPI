package com.futuro.iotdataapi.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SensorCategoryKpiDto {

	private String category;
	private Double lastValue;
	private LocalDateTime lastTimestamp;
	private Double minValue;
	private Double maxValue;
	private Double averageValue;
	private Long totalReadings;

}
