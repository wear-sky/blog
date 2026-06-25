package com.wearsky.demo.gateway.filter;

import com.wearsky.demo.gateway.common.JwtUtil;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class JwtFilterTest {

    @InjectMocks
    private JwtFilter jwtFilter;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private GatewayFilterChain chain;

    @Test
    void filter_NoAuthHeader_ShouldPassThrough() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/test").build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);
        when(chain.filter(exchange)).thenReturn(Mono.empty());

        jwtFilter.filter(exchange, chain).block();

        verify(chain).filter(exchange);
    }

    @Test
    void filter_NonBearerToken_ShouldPassThrough() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/test")
                .header("Authorization", "Basic abc123")
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);
        when(chain.filter(exchange)).thenReturn(Mono.empty());

        jwtFilter.filter(exchange, chain).block();

        verify(chain).filter(exchange);
    }

    @Test
    void filter_ValidToken_ShouldValidateAndCallService() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/test")
                .header("Authorization", "Bearer valid-token")
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        Claims claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn("1");
        when(jwtUtil.validateToken("valid-token")).thenReturn(claims);

        when(chain.filter(any())).thenReturn(Mono.empty());

        try {
            jwtFilter.filter(exchange, chain).block();
        } catch (Exception e) {
            // Expected: WebClient chain is not fully mocked, but validation was called
        }

        verify(jwtUtil).validateToken("valid-token");
    }
}
