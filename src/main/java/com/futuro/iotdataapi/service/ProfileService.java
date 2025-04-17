package com.futuro.iotdataapi.service;

import com.futuro.iotdataapi.dto.ProfileDto;
import com.futuro.iotdataapi.dto.ProfilePwdDto;

public interface ProfileService {

	public ProfileDto update(ProfileDto dto, Integer id);

	public ProfileDto findByUsername(String username);

	public void changePwd(ProfilePwdDto dto, Integer id) throws Exception;

}
