package com.futuro.iotdataapi.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ProfileDto {

	private Integer idUser;
	private String username;
	private String names;
	private String surnames;
	private String alias;
}
