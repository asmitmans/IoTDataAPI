package com.futuro.iotdataapi.service;

import java.util.List;

import com.futuro.iotdataapi.dto.MenuDto;
import com.futuro.iotdataapi.dto.MenuModelDto;
import com.futuro.iotdataapi.dto.MenuModelItemDto;

public interface MenuService {

	List<MenuDto> getMenusByUsername(String username);

	List<MenuModelItemDto> getMenusItemsByUserModel(String username);

	List<MenuModelDto> getMenusByUserModel(String username);

	

}
