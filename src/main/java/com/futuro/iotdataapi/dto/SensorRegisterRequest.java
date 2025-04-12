package com.futuro.iotdataapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

@Data
public class SensorRegisterRequest {

    private Integer companyId; // Solo requerido por admin

    @NotNull
    private Integer locationId;

    @NotBlank
    private String sensorName;

    private String sensorCategory;

    // Opcional, permite incluir metadatos como unidad, tipo, descripción, etc.
    private Map<String, Object> sensorMeta;
}
