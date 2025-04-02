package com.futuro.iotdataapi.config;

import com.futuro.iotdataapi.config.filter.JwtTokenValidator;
import com.futuro.iotdataapi.config.filter.CompanyApiKeyAuthFilter;
import com.futuro.iotdataapi.config.filter.SensorApiKeyAuthFilter;
import com.futuro.iotdataapi.repository.CompanyRepository;
import com.futuro.iotdataapi.repository.SensorRepository;
import com.futuro.iotdataapi.service.UserDetailsServiceImpl;
import com.futuro.iotdataapi.util.JwtUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final JwtUtils jwtUtils;
    private final CompanyRepository companyRepository;
    private final SensorRepository sensorRepository;

    public SecurityConfig(UserDetailsServiceImpl userDetailsService,
                          JwtUtils jwtUtils,
                          CompanyRepository companyRepository,
                          SensorRepository sensorRepository) {

        this.userDetailsService = userDetailsService;
        this.jwtUtils = jwtUtils;
        this.companyRepository = companyRepository;
        this.sensorRepository = sensorRepository;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/login/**").permitAll()
                        .requestMatchers("/api/companies/**").hasRole("ADMIN")
                        .requestMatchers("/api/locations/**").hasRole("ADMIN")
                        .requestMatchers("/api/users/**").hasRole("ADMIN")                        
                        .requestMatchers("/api/sensors/**").hasAnyRole("COMPANY", "ADMIN")
                        .requestMatchers(HttpMethod.GET,"/api/v1/sensor_data").hasAnyRole("COMPANY", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/sensor_data").hasRole("SENSOR")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new SensorApiKeyAuthFilter(sensorRepository), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new CompanyApiKeyAuthFilter(companyRepository), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JwtTokenValidator(jwtUtils), UsernamePasswordAuthenticationFilter.class)
                .httpBasic(httpBasic -> httpBasic.disable())
                .formLogin(form -> form.disable());

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(DaoAuthenticationProvider authenticationProvider) {
        return new ProviderManager(authenticationProvider);
    }

}