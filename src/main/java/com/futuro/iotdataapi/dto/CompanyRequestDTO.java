package com.futuro.iotdataapi.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CompanyRequestDTO {

    @NotNull(message = "El nombre de la compañía es obligatorio")
    private String companyName;

    //@NotNull(message = "El API Key de la compañía es obligatorio")
    private String companyApiKey;
}
