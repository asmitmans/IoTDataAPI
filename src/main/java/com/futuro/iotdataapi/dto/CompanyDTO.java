package com.futuro.iotdataapi.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CompanyDTO {
    private Integer id;
    private String companyName;
    private String companyApiKey;
}
