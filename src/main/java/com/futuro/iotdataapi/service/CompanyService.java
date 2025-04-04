package com.futuro.iotdataapi.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;

import com.futuro.iotdataapi.dto.CompanyDTO;
import com.futuro.iotdataapi.dto.CompanyRequestDTO;

public interface CompanyService {

	public List<CompanyDTO> findAll();

    public Optional<CompanyDTO> findById(Integer id);

    public CompanyDTO save(CompanyRequestDTO request);

    public CompanyDTO update(Integer id, CompanyRequestDTO request);

    public void delete(Integer id);

	public Page<CompanyDTO> findAllPageable(int pageIndex, int pageSize);
	
}
