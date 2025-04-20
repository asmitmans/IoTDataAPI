package com.futuro.iotdataapi.config.filter;

import com.futuro.iotdataapi.repository.CompanyRepository;
import com.futuro.iotdataapi.repository.SensorRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
public class SensorApiKeyAuthFilter extends OncePerRequestFilter {

    private final SensorRepository sensorRepository;
    
    private final CompanyRepository companyRepository; 

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        String method = request.getMethod();
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (path.equals("/api/v1/sensor_data") &&
                authorizationHeader != null &&
                authorizationHeader.startsWith("ApiKey")) {
        	
        	String apiKey = authorizationHeader.replace("ApiKey", "").trim();
        	
        	if ("POST".equalsIgnoreCase(method)) {
	            
	
	            sensorRepository.findBySensorApiKey(apiKey).ifPresentOrElse(
	                    sensor -> {
	                        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
	                                sensor.getSensorName(),
	                                null,
	                                List.of(new SimpleGrantedAuthority("ROLE_SENSOR"))
	                        );
	                        SecurityContextHolder.getContext().setAuthentication(auth);
	                        },
	                    () -> response.setStatus(HttpServletResponse.SC_UNAUTHORIZED)
	            );
        	} else if ("GET".equalsIgnoreCase(method)){        		
                companyRepository.findByCompanyApiKey(apiKey).ifPresentOrElse(
                        company -> {
                            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                                    company.getCompanyName(),
                                    null,
                                    List.of(new SimpleGrantedAuthority("ROLE_COMPANY"))
                            );
                            SecurityContextHolder.getContext().setAuthentication(auth);
                        },
                        () -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        }
                );
        	}
        }

        filterChain.doFilter(request, response);
    }
}
