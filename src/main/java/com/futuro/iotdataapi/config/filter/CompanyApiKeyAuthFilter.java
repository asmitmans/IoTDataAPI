package com.futuro.iotdataapi.config.filter;

import com.futuro.iotdataapi.repository.CompanyRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class CompanyApiKeyAuthFilter extends OncePerRequestFilter {

    private final CompanyRepository companyRepository;

    public CompanyApiKeyAuthFilter(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException,
            IOException {

        String path = request.getRequestURI();
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (path.startsWith("/api/sensors") && authorizationHeader != null &&
                authorizationHeader.startsWith("ApiKey")) {

            String apiKey = authorizationHeader.replace("ApiKey", "").trim();

            companyRepository.findByCompanyApiKey(apiKey).ifPresentOrElse(
                    company -> {
                        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                                company.getCompanyName(), null, null
                        );
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    },
                    () -> {
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    }
            );
        }

        filterChain.doFilter(request, response);
    }
}