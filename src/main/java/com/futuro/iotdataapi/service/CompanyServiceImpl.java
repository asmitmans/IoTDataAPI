package com.futuro.iotdataapi.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public Optional<CompanyDTO> findById(Integer id) {
        return companyRepository.findById(id).map(this::toDTO);
    }

    @Override
    public CompanyDTO save(CompanyRequestDTO request) {
        Company company = Company.builder()
                .companyName(request.getCompanyName())
                .companyApiKey(request.getCompanyApiKey())
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

    private CompanyDTO toDTO(Company company) {
        return CompanyDTO.builder()
                .id(company.getId())
                .companyName(company.getCompanyName())
                .companyApiKey(company.getCompanyApiKey())
                .build();
    }
}
