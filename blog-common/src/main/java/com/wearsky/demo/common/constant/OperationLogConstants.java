package com.wearsky.demo.common.constant;

/**
 * 操作日志 RabbitMQ 常量
 */
public final class OperationLogConstants {

    private OperationLogConstants() {
    }

    /**
     * 交换机名称 (Topic 类型)
     */
    public static final String EXCHANGE = "operation-log";

    /**
     * 队列名称
     */
    public static final String QUEUE = "operation-log.queue";

    /**
     * 通配符路由键，匹配所有操作日志
     */
    public static final String ROUTING_KEY_ALL = "log.#";

    /**
     * 路由键前缀
     */
    public static final String PREFIX = "log.";
}