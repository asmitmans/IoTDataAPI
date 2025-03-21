package com.futuro.iotdataapi.dto;

import java.util.Map;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LocationRequestDTO {

    @NotNull(message = "El ID de la compañía es obligatorio")
    private Integer companyId;

    @NotNull(message = "El nombre de la ubicación es obligatorio")
    private String locationName;

    private String locationCountry;
    private String locationCity;
    private Map<String, Object> locationMeta;
}
