package com.wearsky.demo.user.config;

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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity(jsr250Enabled = true) // 启用@PermitAll和@PreAuthorize注解
@AllArgsConstructor
public class SecurityConfig {

    ObjectMapper objectMapper;

    // 配置SpringSecurity的过滤器链
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity httpSecurity, UserAuthoritiesInfoFilter userAuthoritiesInfoFilter) throws Exception {
        return httpSecurity
                // 貌似UrlBasedCorsConfigurationSource的Bean会自动装配，没必要手动设置cors
                // .cors(httpSecurityCorsConfigurer -> apiConfigurationSource())
                // 设置关闭csrf，前后端分离的jwt校验不需要它
                .csrf(AbstractHttpConfigurer::disable)
                // 关闭session
                .sessionManagement(sessionManagementConfigurer ->
                        sessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 设置url放行策略
                .authorizeHttpRequests(requestMatcherRegistry -> requestMatcherRegistry
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(
                                HttpMethod.POST, "/user-service/user", "/user-service/user/login").permitAll()
                        .requestMatchers(
                                HttpMethod.GET, "/user-service/user/{id:\\d+}", "/user-service/user/ids").permitAll()
                        .requestMatchers(
                                "/doc.html", "/webjars/**" // 放行knife4j
                                , "/v3/**" // 放行OpenAPI JSON文档
                                , "/swagger-ui/**") // 放行swagger
                        .permitAll()
                        .anyRequest().authenticated()
                )
                // 设置鉴权失败的异常捕捉处理
                // accessDenied由全局异常捕捉管理
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

    // 密码加密工具
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
