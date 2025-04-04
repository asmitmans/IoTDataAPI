package com.futuro.iotdataapi.dto;

import java.util.Map;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LocationDTO {
    private Integer id;    
    private CompanyDTO company;
    private String locationName;
    private String locationCountry;
    private String locationCity;
    private Map<String, Object> locationMeta;
}
