package com.futuro.iotdataapi.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;

import com.futuro.iotdataapi.dto.LocationDTO;
import com.futuro.iotdataapi.dto.LocationRequestDTO;

public interface LocationService {

	public List<LocationDTO> findAll();

	public Optional<LocationDTO> findById(Integer id);
	
	public List<LocationDTO> findAllByCompanyId(Integer id);

	public LocationDTO save(LocationRequestDTO request);

	public LocationDTO update(Integer id, LocationRequestDTO request);

	public void delete(Integer id);
	
	public Page<LocationDTO> findAllPageable(int pageIndex, int pageSize);	

}
