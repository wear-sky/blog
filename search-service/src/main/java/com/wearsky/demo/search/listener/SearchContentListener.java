package com.wearsky.demo.search.listener;

import com.wearsky.demo.common.constant.SearchConstants;
import com.wearsky.demo.common.dto.SearchContentDTO;
import com.wearsky.demo.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 全文搜索 RabbitMQ 消费者
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SearchContentListener {

    private final SearchService searchService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = SearchConstants.QUEUE, durable = "true"),
            exchange = @Exchange(name = SearchConstants.EXCHANGE, type = "topic"),
            key = SearchConstants.ROUTING_KEY_ALL
    ))
    public void listenSearchContent(SearchContentDTO dto) {
        log.debug("收到搜索消息: type={}, id={}", dto.getType(), dto.getId());
        try {
            if (dto.getIds() != null && !dto.getIds().isEmpty()) {
                // 批量删除
                searchService.batchDeleteDocuments(dto.getIds(), dto.getType());
            } else if (dto.getContent() != null) {
                // 保存（新增/更新）
                searchService.saveDocument(dto);
            } else {
                // 单条删除
                searchService.deleteDocument(String.valueOf(dto.getId()), dto.getType());
            }
        } catch (Exception e) {
            log.error("处理搜索消息失败: {}", e.getMessage(), e);
        }
    }
}
