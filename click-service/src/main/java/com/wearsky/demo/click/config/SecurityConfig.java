package com.wearsky.demo.click.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wearsky.demo.common.domain.vo.ApiResponse;
import com.wearsky.demo.common.filter.UserAuthoritiesInfoFilter;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@AllArgsConstructor
public class SecurityConfig {

    ObjectMapper objectMapper;

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity httpSecurity, UserAuthoritiesInfoFilter userAuthoritiesInfoFilter) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sessionManagementConfigurer ->
                        sessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(requestMatcherRegistry -> requestMatcherRegistry
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/click-service/**").permitAll()
                        .requestMatchers(
                                "/doc.html", "/webjars/**" // 放行knife4j
                                , "/v3/**" // 放行OpenAPI JSON文档
                                , "/swagger-ui/**") // 放行swagger
                        .permitAll()
                        .anyRequest().authenticated())
                .exceptionHandling(handlingConfigurer -> handlingConfigurer
                        .authenticationEntryPoint((request, response,
                                                   authException) -> {
                            int code = HttpStatus.UNAUTHORIZED.value();
                            response.setStatus(code);
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.setCharacterEncoding("UTF-8");
                            objectMapper.writeValue(response.getWriter(), ApiResponse.error(code, "请先登录"));
                        }))
                .addFilterBefore(userAuthoritiesInfoFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
