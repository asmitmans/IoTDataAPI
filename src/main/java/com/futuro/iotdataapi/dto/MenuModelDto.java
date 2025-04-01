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
public class MenuModelDto {

	@JsonIgnore
	private Integer idPadre;
	private String label;
	private String icon;
	private List<MenuItemDto> items;

	@JsonIgnore
	private String iconFury;

}
