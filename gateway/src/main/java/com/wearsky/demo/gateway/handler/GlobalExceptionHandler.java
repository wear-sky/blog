package com.wearsky.demo.gateway.handler;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.micrometer.common.lang.NonNullApi;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.ConnectException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

@Slf4j
@Order(-1) // 优先于默认的异常处理器
@Component
@AllArgsConstructor
@NonNullApi
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();

        // 响应已提交，无法处理
        if (response.isCommitted()) {
            return Mono.error(ex);
        }

        // 根据异常类型确定状态码和消息
        HttpStatus status;
        String message;

        if (ex instanceof ResponseStatusException responseStatusException) {
            status = HttpStatus.valueOf(responseStatusException.getStatusCode().value());
            message = switch (status) {
                case NOT_FOUND -> "请求的资源不存在";
                case METHOD_NOT_ALLOWED -> "请求方法不允许";
                case BAD_REQUEST -> "请求参数错误";
                case UNAUTHORIZED -> "请先登录";
                case FORBIDDEN -> "没有权限";
                default -> "请求失败: " + responseStatusException.getReason();
            };
        } else if (ex instanceof ExpiredJwtException) {
            status = HttpStatus.UNAUTHORIZED;
            message = "Token已过期，请重新登录";
        } else if (ex instanceof JwtException) {
            status = HttpStatus.UNAUTHORIZED;
            message = "Token无效，请重新登录";
        } else if (ex instanceof ConnectException) {
            status = HttpStatus.SERVICE_UNAVAILABLE;
            message = "服务暂时不可用，请稍后重试";
            log.error("服务连接失败: {}", ex.getMessage());
        } else if (ex instanceof TimeoutException) {
            status = HttpStatus.GATEWAY_TIMEOUT;
            message = "请求超时，请稍后重试";
            log.error("请求超时: {}", ex.getMessage());
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            message = "服务器内部错误";
            log.error("网关异常", ex);
        }

        return writeResponse(response, status, message);
    }

    private Mono<Void> writeResponse(ServerHttpResponse response, HttpStatus status, String message) {
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        byte[] bytes = ("{\"code\":" + status.value() + ",\"message\":\"" + message + "\"}")
                .getBytes(StandardCharsets.UTF_8);

        DataBuffer buffer = response.bufferFactory().wrap(bytes);
        return response.writeWith(Mono.just(buffer));
    }
}