package com.futuro.iotdataapi.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
public class MenuDto {

    @EqualsAndHashCode.Include
    private Integer idMenu;

    private String icon;
    private String name;
    private String url;
    private List<RolDto> roles;
}
