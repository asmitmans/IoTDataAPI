package com.futuro.iotdataapi.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MenuModelItemDto {

	@JsonIgnore
	private Integer idPadre;
	private String name;
	private String icon;
	private String routeOrFunction;
	private MenuModelItemDto parent;
	private List<MenuModelItemDto> subItems;
	private Integer position;
	private Boolean pathMatchExact;
	private String badge;
	private String badgeColor;
	private String type;
	private String customClass;

}
