package com.futuro.iotdataapi.util;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.futuro.iotdataapi.entity.Company;
import com.futuro.iotdataapi.exception.UnauthorizedException;
import com.futuro.iotdataapi.repository.CompanyRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class CompanyResolver {

  private final CompanyRepository companyRepository;
  private final JwtUtils jwtUtils;

  public CompanyResolver(CompanyRepository companyRepository, JwtUtils jwtUtils) {
    this.companyRepository = companyRepository;
    this.jwtUtils = jwtUtils;
  }

  public Company resolveFromAuthorization(String rawAuthorization) {
    if (rawAuthorization != null && rawAuthorization.startsWith("ApiKey ")) {
      String apiKey = extractApiKey(rawAuthorization);
      return companyRepository
          .findByCompanyApiKey(apiKey)
          .orElseThrow(() -> new UnauthorizedException("Company not found or unauthorized"));
    } else {
      Integer companyId = extractCompanyIdFromJwtToken();
      return companyRepository
          .findById(companyId)
          .orElseThrow(() -> new UnauthorizedException("Company not found or unauthorized"));
    }
  }

  private String extractApiKey(String rawHeader) {
    return rawHeader.substring("ApiKey ".length()).trim();
  }

  private Integer extractCompanyIdFromJwtToken() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !auth.isAuthenticated()) {
      throw new UnauthorizedException("No authenticated user");
    }
    String token = extractBearerToken();
    DecodedJWT jwt = jwtUtils.validateToken(token);
    return jwt.getClaim("companyId").asInt();
  }

  private String extractBearerToken() {
    HttpServletRequest request =
        ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      throw new UnauthorizedException("No JWT token found");
    }
    return authHeader.substring(7);
  }
}
