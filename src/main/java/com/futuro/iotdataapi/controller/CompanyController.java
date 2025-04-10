package com.futuro.iotdataapi.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.futuro.iotdataapi.dto.CompanyDTO;
import com.futuro.iotdataapi.dto.CompanyRequestDTO;
import com.futuro.iotdataapi.service.CompanyService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
@Tag(name = "Companies resource")
public class CompanyController {

	private static final String PAGE_DEFAULT_SIZE = "7";

	private final CompanyService companyService;

	@GetMapping
	public ResponseEntity<List<CompanyDTO>> getAllCompanies() {
		return ResponseEntity.ok(companyService.findAll());
	}

	@GetMapping("/{id}")
	public ResponseEntity<CompanyDTO> getCompanyById(@PathVariable Integer id) {
		Optional<CompanyDTO> company = companyService.findById(id);
		return company.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	@PostMapping
	public ResponseEntity<CompanyDTO> createCompany(@Valid @RequestBody CompanyRequestDTO request) {
		return ResponseEntity.ok(companyService.save(request));
	}

	@PutMapping("/{id}")
	public ResponseEntity<CompanyDTO> updateCompany(@PathVariable Integer id,
			@Valid @RequestBody CompanyRequestDTO request) {
		return ResponseEntity.ok(companyService.update(id, request));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteCompany(@PathVariable Integer id) {
		companyService.delete(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping(value = "/paging")
	public ResponseEntity<Page<CompanyDTO>> getClientsPaging(@RequestParam("page") int pageIndex,
			@RequestParam(value = "size", required = false, defaultValue = PAGE_DEFAULT_SIZE) int pageSize) {

		Page<CompanyDTO> pageDto = companyService.findAllPageable(pageIndex, pageSize);

		return ResponseEntity.ok(pageDto);
	}
}
