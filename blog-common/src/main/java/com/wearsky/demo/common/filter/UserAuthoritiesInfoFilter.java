package com.wearsky.demo.common.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wearsky.demo.common.domain.header.UserAuthoritiesHeader;
import io.micrometer.common.lang.NonNullApi;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@NonNullApi
@AllArgsConstructor
public class UserAuthoritiesInfoFilter extends OncePerRequestFilter {

    private static final String USER_AUTHORITIES_INFO_HEADER = "UserAuthoritiesInfo";

    ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String userAuthoritiesInfo = request.getHeader(USER_AUTHORITIES_INFO_HEADER);
        if (StringUtils.isBlank(userAuthoritiesInfo)) {
            filterChain.doFilter(request, response);
            return;
        }
        UserAuthoritiesHeader userAuthoritiesHeader;
        try {
            userAuthoritiesHeader = objectMapper.readValue(userAuthoritiesInfo, UserAuthoritiesHeader.class);
        } catch (Exception e) {
            log.error("解析UserAuthoritiesInfo失败：{}", e.getMessage(), e);
            filterChain.doFilter(request, response);
            return;
        }
        List<SimpleGrantedAuthority> authorities
                = userAuthoritiesHeader.getAuthorities().stream().map(SimpleGrantedAuthority::new).toList();
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                = new UsernamePasswordAuthenticationToken(userAuthoritiesHeader.getId(), null, authorities);
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        filterChain.doFilter(request, response);
    }
}
