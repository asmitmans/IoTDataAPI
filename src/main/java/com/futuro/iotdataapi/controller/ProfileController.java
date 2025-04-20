package com.futuro.iotdataapi.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.futuro.iotdataapi.dto.ProfileDto;
import com.futuro.iotdataapi.dto.ProfilePwdDto;
import com.futuro.iotdataapi.service.ProfileService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/profiles")
public class ProfileController {

	private final ProfileService profileService;
	private final AuthenticationManager authenticationManager;

	@GetMapping(value = "/{username}")
	public ResponseEntity<ProfileDto> findById(@PathVariable("username") String username) {

		ProfileDto dto = profileService.findByUsername(username);

		return ResponseEntity.ok(dto);

	}

	@PutMapping(value = "/{id}")
	public ResponseEntity<ProfileDto> update(@RequestBody ProfileDto dto, @PathVariable("id") Integer id) {

		ProfileDto dtoOut = profileService.update(dto, id);

		return ResponseEntity.ok(dtoOut);
	}

	@PostMapping(value = "/change/{id}")
	public ResponseEntity<Object> changePwd(@RequestBody ProfilePwdDto dto, @PathVariable("id") Integer id) throws Exception {

		String username = dto.getUsername().trim().toLowerCase();

		authenticate(username, dto.getPwdOld());

		profileService.changePwd(dto, id);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	private void authenticate(String username, String password) throws Exception {
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
		} catch (DisabledException e) {
			throw new Exception("USER_DISABLED", e);
		} catch (BadCredentialsException e) {
			throw new Exception("INVALID_CREDENTIALS", e);
		}
	}

}
