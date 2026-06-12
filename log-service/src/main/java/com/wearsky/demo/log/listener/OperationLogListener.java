package com.wearsky.demo.log.listener;

import com.wearsky.demo.common.constant.OperationLogConstants;
import com.wearsky.demo.common.dto.OperationLogDTO;
import com.wearsky.demo.log.service.OperationLogService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 操作日志 RabbitMQ 消费者
 */
@Slf4j
@Component
@AllArgsConstructor
public class OperationLogListener {

    private final OperationLogService operationLogService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = OperationLogConstants.QUEUE, durable = "true"),
            exchange = @Exchange(name = OperationLogConstants.EXCHANGE, type = "topic"),
            key = OperationLogConstants.ROUTING_KEY_ALL
    ))
    public void listenOperationLog(OperationLogDTO dto) {
        log.debug("收到操作日志: {} - {}", dto.getModule(), dto.getOperation());
        try {
            operationLogService.saveLog(dto);
        } catch (Exception e) {
            log.error("处理操作日志失败: {}", e.getMessage(), e);
        }
    }
}
