package com.wearsky.demo.common.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wearsky.demo.common.domain.header.UserAuthoritiesHeader;
import feign.Logger;
import feign.RequestInterceptor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;
import java.util.List;

public class FeignConfig {

    @Resource
    ObjectMapper objectMapper;

    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Object userId = authentication.getPrincipal();
            Collection<? extends GrantedAuthority> grantedAuthorities = authentication.getAuthorities();

            // 如果是默认的匿名用户，直接return
            if (userId.equals("anonymousUser")
                    || grantedAuthorities.iterator().next().getAuthority().equals("ROLE_ANONYMOUS")) {
                return;
            }

            // 构建userAuthoritiesHeader并写入openFeign的header
            List<String> authorities =
                    authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
            UserAuthoritiesHeader userAuthoritiesHeader = new UserAuthoritiesHeader((Long) userId, authorities);
            try {
                requestTemplate.header(
                        "UserAuthoritiesInfo", objectMapper.writeValueAsString(userAuthoritiesHeader));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("用户信息序列化失败", e);
            }
        };
    }
}
