package com.futuro.iotdataapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.futuro.iotdataapi.dto.ProfileDto;
import com.futuro.iotdataapi.dto.ProfilePwdDto;
import com.futuro.iotdataapi.entity.User;
import com.futuro.iotdataapi.exception.NotFoundException;
import com.futuro.iotdataapi.repository.UserRepository;

@Service
public class ProfileServiceImpl implements ProfileService {

	@Autowired
	private PasswordEncoder bcrypt;

	@Autowired
	private UserRepository userRepository;

	@Override
	public ProfileDto update(ProfileDto dto, Integer id) {

		User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("Profile not found"));

		user.setAlias(dto.getAlias());
		user.setNames(dto.getNames());
		user.setSurnames(dto.getSurnames());

		return toDto(userRepository.save(user));
	}

	private ProfileDto toDto(User user) {
		return ProfileDto.builder().idUser(user.getId()).username(user.getUsername()).alias(user.getAlias())
				.names(user.getNames()).surnames(user.getSurnames()).build();
	}

	@Override
	public ProfileDto findByUsername(String username) {

		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new NotFoundException("Profile not found"));

		return toDto(user);
	}

	@Override
	public void changePwd(ProfilePwdDto dto, Integer id) throws Exception {
		User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("Profile not found"));

		if (!dto.getUsername().trim().equalsIgnoreCase(user.getUsername().trim())
				|| !dto.getPwdNew().equals(dto.getPwdNewConfirm())) {
			throw new Exception("INVALID_CREDENTIALS");
		}

		userRepository.changePassword(bcrypt.encode(dto.getPwdNew()), dto.getUsername());
	}

}
