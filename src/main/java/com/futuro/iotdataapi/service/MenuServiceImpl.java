package com.futuro.iotdataapi.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.futuro.iotdataapi.dto.MenuDto;
import com.futuro.iotdataapi.dto.MenuItemDto;
import com.futuro.iotdataapi.dto.MenuModelDto;
import com.futuro.iotdataapi.dto.MenuModelItemDto;
import com.futuro.iotdataapi.dto.RolDto;
import com.futuro.iotdataapi.entity.Menu;
import com.futuro.iotdataapi.entity.Role;
import com.futuro.iotdataapi.repository.MenuRepository;
import com.futuro.iotdataapi.util.MenuTypeFuryEnum;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

	private final MenuRepository menuRepository;

	@Override
	public List<MenuDto> getMenusByUsername(String username) {
		List<Menu> menus = menuRepository.getMenusByUsername(username);
		return menus.stream().map(this::toDto).toList();
	}

	@Override
	public List<MenuModelItemDto> getMenusItemsByUserModel(String username) {
		List<MenuModelItemDto> menuItemList = new ArrayList<>();
		List<MenuModelDto> menusDto = getMenusByUserModel(username);

		int index = 0;

		for (MenuModelDto menuDto : menusDto) {

			index += 5;

			MenuModelItemDto father = new MenuModelItemDto();
			father.setName(menuDto.getLabel().trim().toUpperCase());
			father.setPosition(index);
			father.setIcon(menuDto.getIconFury());
			father.setType(MenuTypeFuryEnum.SUBHEADING.getValue());

			menuItemList.add(father);

			for (MenuItemDto itemDto : menuDto.getItems()) {

				if (Optional.ofNullable(itemDto.getIconFury()).orElse("").trim().length() > 0) {
					index++;

					MenuModelItemDto item = new MenuModelItemDto();
					item.setName(itemDto.getLabel().trim());
					item.setPosition(index);
					item.setIcon(itemDto.getIconFury());
					item.setRouteOrFunction(itemDto.getRouterLink().get(0));

					menuItemList.add(item);
				}
			}
		}

		return menuItemList;
	}

	@Override
	public List<MenuModelDto> getMenusByUserModel(String username) {
		List<Menu> menus = menuRepository.getMenusByUsername(username);

		List<MenuModelDto> menusDto = new ArrayList<>();

		for (Menu menu : menus) {
			int i = getPos(menu.getIdFather(), menusDto);
			MenuModelDto model = null;
			if (i < 0) {
				model = new MenuModelDto();
				model.setIdFather(menu.getIdFather());
				model.setItems(new ArrayList<>());
				model.setLabel(menu.getFatherName());
				model.setIconFury(menu.getIconFury());

				menusDto.add(model);
			} else {
				model = menusDto.get(i);
			}

			model.getItems().add(crearItem(menu));
		}

		return menusDto;
	}

	private MenuItemDto crearItem(Menu menu) {
		MenuItemDto item = new MenuItemDto();
		item.setLabel(menu.getItemName());
		item.setIcon(menu.getIcon());
		item.setRouterLink(new ArrayList<>());

		item.getRouterLink().add(menu.getUrl());

		item.setIconFury(menu.getIconFury());

		return item;
	}

	private int getPos(int idFather, List<MenuModelDto> listModels) {
		int pos = -1;
		boolean exist = false;

		if (listModels != null && !listModels.isEmpty()) {
			for (MenuModelDto model : listModels) {
				pos++;
				if (idFather == model.getIdFather().intValue()) {
					exist = true;
					break;
				}
			}
		}

		if (!exist) {
			pos = -1;
		}

		return pos;
	}

	private MenuDto toDto(Menu menu) {
		return MenuDto.builder().
				idMenu(menu.getIdMenu()).
				icon(menu.getIcon()).
				name(menu.getItemName())
				.url(menu.getUrl())
				.roles(menu.getRoles() != null ? menu.getRoles().stream().map(this::toDto).collect(Collectors.toList())	: null)
				.build();
	}

	private RolDto toDto(Role role) {
		return new RolDto(role.getId().intValue(), role.getName(), null);
	}

}
