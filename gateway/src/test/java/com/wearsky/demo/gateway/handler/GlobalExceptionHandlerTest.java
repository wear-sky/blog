package com.wearsky.demo.gateway.handler;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;

import java.net.ConnectException;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void handle_ResponseAlreadyCommitted_ShouldRethrow() {
        ServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/test").build());
        exchange.getResponse().setStatusCode(HttpStatus.OK);
        exchange.getResponse().setComplete().subscribe();

        // After setComplete, isCommitted should be true
        // The handler should return Mono.error for committed responses
        assertThrows(Exception.class, () -> handler.handle(exchange, new RuntimeException("test")).block());
    }

    @Test
    void handle_NotFound_ShouldReturn404() {
        ServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/test").build());
        ResponseStatusException ex = new ResponseStatusException(HttpStatus.NOT_FOUND);

        handler.handle(exchange, ex).block();

        assertEquals(HttpStatus.NOT_FOUND, exchange.getResponse().getStatusCode());
    }

    @Test
    void handle_BadRequest_ShouldReturn400() {
        ServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/test").build());
        ResponseStatusException ex = new ResponseStatusException(HttpStatus.BAD_REQUEST);

        handler.handle(exchange, ex).block();

        assertEquals(HttpStatus.BAD_REQUEST, exchange.getResponse().getStatusCode());
    }

    @Test
    void handle_ExpiredJwt_ShouldReturn401() {
        ServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/test").build());
        ExpiredJwtException ex = mock(ExpiredJwtException.class);

        handler.handle(exchange, ex).block();

        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
    }

    @Test
    void handle_JwtException_ShouldReturn401() {
        ServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/test").build());
        JwtException ex = mock(JwtException.class);

        handler.handle(exchange, ex).block();

        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
    }

    @Test
    void handle_ConnectException_ShouldReturn503() {
        ServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/test").build());
        ConnectException ex = new ConnectException("refused");

        handler.handle(exchange, ex).block();

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, exchange.getResponse().getStatusCode());
    }

    @Test
    void handle_TimeoutException_ShouldReturn504() {
        ServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/test").build());
        TimeoutException ex = new TimeoutException("timed out");

        handler.handle(exchange, ex).block();

        assertEquals(HttpStatus.GATEWAY_TIMEOUT, exchange.getResponse().getStatusCode());
    }

    @Test
    void handle_RuntimeException_ShouldReturn500() {
        ServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/test").build());

        handler.handle(exchange, new RuntimeException("unexpected")).block();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exchange.getResponse().getStatusCode());
    }
}
