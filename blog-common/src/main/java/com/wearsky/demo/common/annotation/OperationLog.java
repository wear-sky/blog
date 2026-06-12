package com.wearsky.demo.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用户操作日志注解
 * 标注在 Controller 方法上，由 AOP 切面自动采集并发送到 RabbitMQ
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OperationLog {

    /**
     * 所属模块: "用户", "博客", "评论", "互动"
     */
    String module() default "";

    /**
     * 操作类型: "登录", "发布博客", "点赞" 等
     */
    String operation() default "";
}