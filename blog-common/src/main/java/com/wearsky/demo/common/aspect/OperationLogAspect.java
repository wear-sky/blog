package com.wearsky.demo.common.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wearsky.demo.common.annotation.OperationLog;
import com.wearsky.demo.common.constant.OperationLogConstants;
import com.wearsky.demo.common.dto.OperationLogDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

/**
 * 操作日志 AOP 切面
 * 拦截带 @OperationLog 注解的方法，自动采集日志并发送到 RabbitMQ
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class OperationLogAspect {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    @Around("@annotation(operationLog)")
    public Object around(ProceedingJoinPoint joinPoint, OperationLog operationLog) throws Throwable {
        long startTime = System.currentTimeMillis();
        OperationLogDTO.OperationLogDTOBuilder dtoBuilder = OperationLogDTO.builder()
                .module(operationLog.module())
                .operation(operationLog.operation())
                .createdAt(LocalDateTime.now());

        // 获取方法信息
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        dtoBuilder.method(signature.getDeclaringType().getSimpleName() + "." + signature.getName());

        // 获取请求信息
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                dtoBuilder.httpMethod(request.getMethod());
                dtoBuilder.url(request.getRequestURI());
                dtoBuilder.ip(getClientIp(request));
            }
        } catch (Exception e) {
            log.warn("获取请求信息失败", e);
        }

        // 获取当前用户
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof Long userId) {
                dtoBuilder.userId(userId);
            }
        } catch (Exception e) {
            log.warn("获取用户信息失败", e);
        }

        // 获取请求参数（排除敏感信息）
        try {
            Object[] args = joinPoint.getArgs();
            if (args != null && args.length > 0) {
                // 过滤掉 HttpServletRequest 等不可序列化的参数
                Object[] serializableArgs = new Object[args.length];
                for (int i = 0; i < args.length; i++) {
                    if (args[i] instanceof HttpServletRequest) {
                        serializableArgs[i] = "[HttpServletRequest]";
                    } else {
                        serializableArgs[i] = args[i];
                    }
                }
                String params = objectMapper.writeValueAsString(serializableArgs);
                // 参数过长时截断
                if (params.length() > 1000) {
                    params = params.substring(0, 1000) + "...";
                }
                dtoBuilder.params(params);
            }
        } catch (Exception e) {
            log.warn("序列化请求参数失败", e);
        }

        // 执行目标方法
        Object result;
        try {
            result = joinPoint.proceed();
            dtoBuilder.status(0);
        } catch (Throwable e) {
            dtoBuilder.status(1);
            dtoBuilder.errorMsg(e.getMessage());
            throw e;
        } finally {
            // 计算耗时
            dtoBuilder.duration(System.currentTimeMillis() - startTime);

            // 异步发送日志到 RabbitMQ
            OperationLogDTO dto = dtoBuilder.build();
            buildRoutingKey(dto);
            try {
                rabbitTemplate.convertAndSend(
                        OperationLogConstants.EXCHANGE,
                        buildRoutingKey(dto),
                        dto
                );
            } catch (Exception e) {
                log.error("发送操作日志到 RabbitMQ 失败: {}", e.getMessage());
            }
        }

        return result;
    }

    /**
     * 根据模块和操作构建路由键
     * 格式: log.{module}.{operation}
     */
    private String buildRoutingKey(OperationLogDTO dto) {
        String module = dto.getModule() != null ? dto.getModule().toLowerCase() : "unknown";
        String operation = dto.getOperation() != null ? dto.getOperation().toLowerCase() : "unknown";
        // 将中文替换为拼音或英文标识
        module = switch (module) {
            case "用户" -> "user";
            case "博客" -> "blog";
            case "评论" -> "reply";
            case "互动" -> "click";
            default -> module;
        };
        return OperationLogConstants.PREFIX + module + "." + operation;
    }

    /**
     * 获取客户端真实 IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多级代理时取第一个
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
