package com.futuro.iotdataapi.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.futuro.iotdataapi.dto.CompanyDTO;
import com.futuro.iotdataapi.dto.CompanyRequestDTO;
import com.futuro.iotdataapi.entity.Company;
import com.futuro.iotdataapi.repository.CompanyRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;

    @Override
    public List<CompanyDTO> findAll() {
        return companyRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public CompanyDTO findById(Integer id) {
    	Company company = companyRepository.findById(id).orElseThrow(() -> new RuntimeException("Company not found with id: " + id));    	
        return toDTO(company);
    }

    @Override
    public CompanyDTO save(CompanyRequestDTO request) {
    	
    	String  uuid = "";
    	Company c = null;
    	
    	do {
    		uuid = UUID.randomUUID().toString();
    		c = companyRepository.findByCompanyApiKey(uuid).orElse(null);
    	} while(c != null);    	
    	
        Company company = Company.builder()
                .companyName(request.getCompanyName())
                .companyApiKey(uuid)
                .build();
        return toDTO(companyRepository.save(company));
    }

    @Override
    public CompanyDTO update(Integer id, CompanyRequestDTO request) {
        Company company = companyRepository.findById(id).orElseThrow(() -> new RuntimeException("Company not found with id: " + id));

        company.setCompanyName(request.getCompanyName());
        company.setCompanyApiKey(request.getCompanyApiKey());

        return toDTO(companyRepository.save(company));
    }

    @Override
    public void delete(Integer id) {
        companyRepository.deleteById(id);
    }
    
    @Override
	public Page<CompanyDTO> findAllPageable(int pageIndex, int pageSize) {

		Pageable pageable = PageRequest.of(pageIndex, pageSize);

		Page<Company> pages = companyRepository.findAll(pageable);

		
		return pages.map(this::toDTO);

	}

    private CompanyDTO toDTO(Company company) {
        return CompanyDTO.builder()
                .id(company.getId())
                .companyName(company.getCompanyName())
                .companyApiKey(company.getCompanyApiKey())
                .build();
    }
}
