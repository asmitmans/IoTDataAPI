package com.futuro.iotdataapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SensorDataUploadResponse {

    @NotBlank
    private String message;

    @NotEmpty
    private int recordsSaved;
}
