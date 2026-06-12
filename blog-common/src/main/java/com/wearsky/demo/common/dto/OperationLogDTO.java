package com.wearsky.demo.common.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 操作日志消息体
 * 通过 RabbitMQ 从 Producer 发送到 log-service
 */
@Data
@Builder
public class OperationLogDTO {

    /**
     * 操作用户ID
     */
    private Long userId;

    /**
     * 操作用户名
     */
    private String username;

    /**
     * 所属模块: "用户", "博客", "评论", "互动"
     */
    private String module;

    /**
     * 操作类型: "登录", "发布博客", "点赞" 等
     */
    private String operation;

    /**
     * 请求方法 (类名.方法名)
     */
    private String method;

    /**
     * HTTP方法 (GET/POST/PUT/DELETE)
     */
    private String httpMethod;

    /**
     * 请求URL
     */
    private String url;

    /**
     * 请求参数 (JSON)
     */
    private String params;

    /**
     * 客户端IP
     */
    private String ip;

    /**
     * 操作状态 (0成功/1失败)
     */
    private Integer status;

    /**
     * 错误信息
     */
    private String errorMsg;

    /**
     * 耗时(ms)
     */
    private Long duration;

    /**
     * 操作时间
     */
    private LocalDateTime createdAt;
}