package com.futuro.iotdataapi.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.futuro.iotdataapi.dto.CompanyDTO;
import com.futuro.iotdataapi.dto.LocationDTO;
import com.futuro.iotdataapi.dto.LocationRequestDTO;
import com.futuro.iotdataapi.entity.Company;
import com.futuro.iotdataapi.entity.Location;
import com.futuro.iotdataapi.repository.CompanyRepository;
import com.futuro.iotdataapi.repository.LocationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

	private final LocationRepository locationRepository;
	private final CompanyRepository companyRepository;

	@Override
	public List<LocationDTO> findAll() {
        return locationRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

	@Override
    public Optional<LocationDTO> findById(Integer id) {
        return locationRepository.findById(id).map(this::toDTO);
    }

	@Override
    public LocationDTO save(LocationRequestDTO request) {
        Company company = companyRepository.findById(request.getCompanyId()).orElseThrow(() -> new RuntimeException("Company not found with id: " + request.getCompanyId()));

        Location location = Location.builder()
                .company(company)
                .locationName(request.getLocationName())
                .locationCountry(request.getLocationCountry())
                .locationCity(request.getLocationCity())
                .locationMeta(request.getLocationMeta())
                .build();

        return toDTO(locationRepository.save(location));
    }

	@Override
    public LocationDTO update(Integer id, LocationRequestDTO request) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Location not found with id: " + id));

        Company company = companyRepository.findById(request.getCompanyId()).orElseThrow(() -> new RuntimeException("Company not found with id: " + request.getCompanyId()));

        location.setCompany(company);
        location.setLocationName(request.getLocationName());
        location.setLocationCountry(request.getLocationCountry());
        location.setLocationCity(request.getLocationCity());
        location.setLocationMeta(request.getLocationMeta());

        return toDTO(locationRepository.save(location));
    }

	@Override
    public void delete(Integer id) {
        locationRepository.deleteById(id);
    }
	
	@Override
	public Page<LocationDTO> findAllPageable(int pageIndex, int pageSize) {

		Pageable pageable = PageRequest.of(pageIndex, pageSize);

		Page<Location> pages = locationRepository.findAll(pageable);
		
		return pages.map(this::toDTO);

	}

    private LocationDTO toDTO(Location location) {
    	CompanyDTO companyDto = CompanyDTO.builder()
    								.id(location.getCompany().getId())
    								.companyName(location.getCompany().getCompanyName())
    								.companyApiKey(location.getCompany().getCompanyApiKey())
    								.build();
    	
        return LocationDTO.builder()
                .id(location.getId())
                .company(companyDto)
                .locationName(location.getLocationName())
                .locationCountry(location.getLocationCountry())
                .locationCity(location.getLocationCity())
                .locationMeta(location.getLocationMeta())
                .build();
    }
}
