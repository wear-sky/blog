package com.wearsky.demo.gateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wearsky.demo.gateway.common.JwtUtil;
import com.wearsky.demo.gateway.domain.header.UserAuthoritiesHeader;
import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class JwtFilter implements GlobalFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";

    private static final String BEARER_PREFIX = "Bearer ";

    private static final String USER_AUTHORITIES_INFO_HEADER = "UserAuthoritiesInfo";

    private JwtUtil jwtUtil;

    ObjectMapper objectMapper;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        // 获取Authorization头
        List<String> authorizations = request.getHeaders().get(AUTHORIZATION_HEADER);

        // 没有Authorization头或为空，直接放行（由下游服务处理认证）
        if (authorizations == null || authorizations.isEmpty()) {
            return chain.filter(exchange);
        }

        String bearerToken = authorizations.get(0);
        if (!bearerToken.startsWith(BEARER_PREFIX)) {
            return chain.filter(exchange);
        }

        String token = bearerToken.substring(BEARER_PREFIX.length());

        // 验证token（异常由 GlobalExceptionHandler 处理）
        Claims claims = jwtUtil.validateToken(token);

        @SuppressWarnings("unchecked")
        UserAuthoritiesHeader userAuthoritiesHeader = new UserAuthoritiesHeader(
                Long.parseLong(claims.getSubject()),
                claims.get("authorities", List.class));

        // 将用户权限信息写入请求头传递给下游服务
        String headerValue;
        try {
            headerValue = objectMapper.writeValueAsString(userAuthoritiesHeader);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("用户信息序列化失败", e);
        }

        ServerHttpRequest newRequest = request.mutate()
                .header(USER_AUTHORITIES_INFO_HEADER, headerValue)
                .build();

        return chain.filter(exchange.mutate().request(newRequest).build());
    }
}
