package com.futuro.iotdataapi.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LocationDTO {
    private Integer id;
    private Integer companyId;
    private String locationName;
    private String locationCountry;
    private String locationCity;
    private String locationMeta;
}
