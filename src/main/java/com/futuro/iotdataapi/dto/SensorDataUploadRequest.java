package com.futuro.iotdataapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class SensorDataUploadRequest {

    @JsonProperty("api_key")
    @NotBlank
    private String sensor_api_key;

    @NotNull
    @Size(min = 1, message = "Se requiere al menos una medición")
    private List<Map<String, Object>> json_data;

}
