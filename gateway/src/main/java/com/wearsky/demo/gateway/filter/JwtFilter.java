package com.wearsky.demo.gateway.filter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wearsky.demo.gateway.common.JwtUtil;
import com.wearsky.demo.gateway.domain.header.UserAuthoritiesHeader;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter implements GlobalFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String USER_AUTHORITIES_INFO_HEADER = "UserAuthoritiesInfo";

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;
    private final WebClient webClient;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        List<String> authorizations = request.getHeaders().get(AUTHORIZATION_HEADER);
        if (authorizations == null || authorizations.isEmpty()) {
            return chain.filter(exchange);
        }

        String bearerToken = authorizations.get(0);
        if (!bearerToken.startsWith(BEARER_PREFIX)) {
            return chain.filter(exchange);
        }

        String token = bearerToken.substring(BEARER_PREFIX.length());

        Claims claims = jwtUtil.validateToken(token);
        Long userId = Long.parseLong(claims.getSubject());

        return getAuthorities(userId)
                .map(authorities -> {
                    String headerValue;
                    try {
                        UserAuthoritiesHeader header = new UserAuthoritiesHeader(userId, authorities);
                        headerValue = objectMapper.writeValueAsString(header);
                    } catch (Exception e) {
                        throw new RuntimeException("用户信息序列化失败", e);
                    }
                    ServerHttpRequest newRequest = request.mutate()
                            .header(USER_AUTHORITIES_INFO_HEADER, headerValue)
                            .build();
                    return exchange.mutate().request(newRequest).build();
                })
                .switchIfEmpty(Mono.just(exchange))
                .flatMap(chain::filter);
    }

    private Mono<List<String>> getAuthorities(Long userId) {
        return webClient.get()
                .uri("http://user-service/user-service/user/{id}/authorities", userId)
                .retrieve()
                .bodyToMono(String.class)
                .map(responseBody -> {
                    try {
                        JsonNode jsonNode = objectMapper.readTree(responseBody);
                        return objectMapper.convertValue(
                                jsonNode.get("data"), new TypeReference<List<String>>() {});
                    } catch (Exception e) {
                        throw new RuntimeException("解析authorities响应失败", e);
                    }
                })
                .onErrorResume(e -> {
                    log.error("获取authorities失败, userId={}: {}", userId, e.getMessage());
                    return Mono.empty();
                });
    }
}
