package com.futuro.iotdataapi.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.futuro.iotdataapi.dto.MenuDto;
import com.futuro.iotdataapi.dto.MenuModelDto;
import com.futuro.iotdataapi.dto.MenuModelItemDto;
import com.futuro.iotdataapi.service.MenuService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/menus")
@RequiredArgsConstructor
public class MenuController {
	
	private final MenuService service;

	@PostMapping("/user")
	public ResponseEntity<List<MenuDto>> getMenusByUser(@RequestBody String username) throws Exception {
		List<MenuDto> menusDTO = service.getMenusByUsername(username);

		return new ResponseEntity<>(menusDTO, HttpStatus.OK);
	}

	@PostMapping("/user/model")
	public ResponseEntity<List<MenuModelDto>> getMenusByUserModel(@RequestBody String username) throws Exception {
		List<MenuModelDto> menuModelDto = service.getMenusByUserModel(username);

		return new ResponseEntity<>(menuModelDto, HttpStatus.OK);
	}
	
	@PostMapping("/user/model/item")
	public ResponseEntity<List<MenuModelItemDto>> getMenusItemsByUserModel(@RequestBody String username) throws Exception {
		List<MenuModelItemDto> menuModelDto = service.getMenusItemsByUserModel(username);

		return new ResponseEntity<>(menuModelDto, HttpStatus.OK);
	}
		
}
