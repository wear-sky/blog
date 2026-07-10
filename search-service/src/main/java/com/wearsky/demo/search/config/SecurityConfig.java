package com.wearsky.demo.search.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wearsky.demo.common.domain.vo.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@AllArgsConstructor
public class SecurityConfig {

    ObjectMapper objectMapper;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sessionManagementConfigurer ->
                        sessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(requestMatcherRegistry -> requestMatcherRegistry
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/search-service/search").permitAll()
                        .anyRequest().authenticated())
                .exceptionHandling(handlingConfigurer -> handlingConfigurer
                        .authenticationEntryPoint((request, response,
                                                   authException) -> {
                            int code = HttpStatus.UNAUTHORIZED.value();
                            response.setStatus(code);
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.setCharacterEncoding("UTF-8");
                            objectMapper.writeValue(response.getWriter(), ApiResponse.error(code, "请先登录"));
                        })
                        .accessDeniedHandler((request, response,
                                              accessDeniedException) -> {
                            int code = HttpStatus.FORBIDDEN.value();
                            response.setStatus(code);
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.setCharacterEncoding("UTF-8");
                            objectMapper.writeValue(response.getWriter(), ApiResponse.error(code, "没有权限"));
                        }))
                .build();
    }
}
