package com.futuro.iotdataapi.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfilePwdDto {

	private Integer idUser;
	private String username;
	private String pwdOld;
	private String pwdNew;
	private String pwdNewConfirm;
}
