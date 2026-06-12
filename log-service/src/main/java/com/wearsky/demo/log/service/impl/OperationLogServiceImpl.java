package com.wearsky.demo.log.service.impl;

import com.wearsky.demo.common.dto.OperationLogDTO;
import com.wearsky.demo.log.document.OperationLogDocument;
import com.wearsky.demo.log.repository.OperationLogRepository;
import com.wearsky.demo.log.service.OperationLogService;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 操作日志服务实现
 * 使用批量写入优化 ES 性能
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OperationLogServiceImpl implements OperationLogService {

    private final OperationLogRepository repository;

    /**
     * 写入缓冲区
     */
    private final List<OperationLogDocument> buffer = new ArrayList<>();

    /**
     * 批量写入阈值
     */
    private static final int BATCH_SIZE = 100;

    @Override
    public void saveLog(OperationLogDTO dto) {
        OperationLogDocument doc = convertToDocument(dto);
        synchronized (buffer) {
            buffer.add(doc);
            if (buffer.size() >= BATCH_SIZE) {
                doFlush();
            }
        }
    }

    @Scheduled(fixedDelay = 5000)
    void flushBuffer() {
        doFlushWithLock();
    }

    @PreDestroy
    void onShutdown() {
        log.info("服务关闭，刷新剩余 {} 条操作日志", buffer.size());
        doFlushWithLock();
    }

    private void doFlushWithLock() {
        synchronized (buffer) {
            if (!buffer.isEmpty()) {
                doFlush();
            }
        }
    }

    /**
     * 执行批量写入
     */
    private void doFlush() {
        try {
            repository.saveAll(buffer);
            log.debug("批量写入 {} 条操作日志到 ES", buffer.size());
            buffer.clear();
        } catch (Exception e) {
            log.error("批量写入操作日志到 ES 失败: {}", e.getMessage(), e);
            // 写入失败时清空缓冲区，避免无限重试
            buffer.clear();
        }
    }

    /**
     * DTO 转 Document
     */
    private OperationLogDocument convertToDocument(OperationLogDTO dto) {
        OperationLogDocument doc = new OperationLogDocument();
        doc.setId(UUID.randomUUID().toString());
        doc.setUserId(dto.getUserId());
        doc.setUsername(dto.getUsername());
        doc.setModule(dto.getModule());
        doc.setOperation(dto.getOperation());
        doc.setMethod(dto.getMethod());
        doc.setHttpMethod(dto.getHttpMethod());
        doc.setUrl(dto.getUrl());
        doc.setParams(dto.getParams());
        doc.setIp(dto.getIp());
        doc.setStatus(dto.getStatus());
        doc.setErrorMsg(dto.getErrorMsg());
        doc.setDuration(dto.getDuration());
        doc.setCreatedAt(dto.getCreatedAt());
        return doc;
    }
}
