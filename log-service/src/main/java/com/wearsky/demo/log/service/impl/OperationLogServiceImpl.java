package com.wearsky.demo.log.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;
import com.wearsky.demo.common.dto.OperationLogDTO;
import com.wearsky.demo.log.document.OperationLogDocument;
import com.wearsky.demo.log.repository.OperationLogRepository;
import com.wearsky.demo.log.service.OperationLogService;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 操作日志服务实现
 * 使用批量写入优化 ES 性能
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OperationLogServiceImpl implements OperationLogService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String INDEX_NAME = "operation-log";
    private static final int BATCH_SIZE = 100;

    private final OperationLogRepository repository;
    private final ElasticsearchClient elasticsearchClient;

    /**
     * 写入缓冲区
     */
    private final List<OperationLogDocument> buffer = new ArrayList<>();

    // ==================== 写入 ====================

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

    // ==================== 查询 ====================

    @FunctionalInterface
    private interface EsQuery<T> {
        SearchResponse<T> execute() throws IOException;
    }

    private <T> SearchResponse<T> executeQuery(EsQuery<T> query) {
        try {
            return query.execute();
        } catch (IOException e) {
            throw new RuntimeException("ES 查询失败: " + e.getMessage(), e);
        }
    }

    @Override
    public Map<String, Object> page(int pageNum, int pageSize, Long userId,
                                    String module, String operation,
                                    String startTime, String endTime) {
        SearchResponse<OperationLogDocument> response = executeQuery(() -> elasticsearchClient.search(s -> {
                    s.index(INDEX_NAME)
                            .from((pageNum - 1) * pageSize)
                            .size(pageSize)
                            .sort(so -> so.field(f -> f.field("createdAt").order(SortOrder.Desc)));

                    s.query(q -> q.bool(b -> {
                        if (userId != null) {
                            b.filter(f -> f.term(t -> t.field("userId").value(userId)));
                        }
                        if (module != null && !module.isEmpty()) {
                            b.filter(f -> f.term(t -> t.field("module").value(module)));
                        }
                        if (operation != null && !operation.isEmpty()) {
                            b.filter(f -> f.term(t -> t.field("operation").value(operation)));
                        }
                        if (startTime != null || endTime != null) {
                            b.filter(f -> f.range(r -> r.date(d -> {
                                d.field("createdAt");
                                if (startTime != null && !startTime.isEmpty()) d.gte(startTime);
                                if (endTime != null && !endTime.isEmpty()) d.lte(endTime);
                                return d;
                            })));
                        }
                        return b;
                    }));
                    return s;
                },
                OperationLogDocument.class
        ));

        Map<String, Object> result = new HashMap<>();
        result.put("total", response.hits().total() != null ? response.hits().total().value() : 0);
        result.put("list", response.hits().hits().stream()
                .map(Hit::source)
                .toList());
        return result;
    }

    @Override
    public List<Map<String, Object>> statsByModule() {
        SearchResponse<Void> response = executeQuery(() -> elasticsearchClient.search(s -> s
                        .index(INDEX_NAME)
                        .size(0)
                        .aggregations("by_module", a -> a
                                .terms(t -> t.field("module").size(20))
                        ),
                Void.class
        ));
        return extractTermBuckets(response, "by_module", "module");
    }

    @Override
    public List<Map<String, Object>> statsByOperation() {
        SearchResponse<Void> response = executeQuery(() -> elasticsearchClient.search(s -> s
                        .index(INDEX_NAME)
                        .size(0)
                        .aggregations("by_operation", a -> a
                                .terms(t -> t.field("operation").size(50))
                        ),
                Void.class
        ));
        return extractTermBuckets(response, "by_operation", "operation");
    }

    @Override
    public List<Map<String, Object>> statsByTrend(String startTime, String endTime) {
        SearchResponse<Void> response = executeQuery(() -> elasticsearchClient.search(s -> {
                    s.index(INDEX_NAME).size(0);
                    if (startTime != null || endTime != null) {
                        s.query(q -> q.range(r -> r.untyped(u -> {
                            u.field("createdAt");
                            if (startTime != null) u.gte(JsonData.of(startTime));
                            if (endTime != null) u.lte(JsonData.of(endTime));
                            return u;
                        })));
                    }
                    s.aggregations("daily_trend", a -> a
                            .dateHistogram(d -> d
                                    .field("createdAt")
                                    .calendarInterval(co.elastic.clients.elasticsearch._types.aggregations.CalendarInterval.Day)
                            )
                    );
                    return s;
                },
                Void.class
        ));

        return response.aggregations().get("daily_trend")
                .dateHistogram().buckets().array().stream()
                .map(bucket -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("date", bucket.keyAsString());
                    item.put("count", bucket.docCount());
                    return item;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> statsByUser(Long userId) {
        SearchResponse<Void> response = executeQuery(() -> elasticsearchClient.search(s -> s
                        .index(INDEX_NAME)
                        .size(0)
                        .query(q -> q.term(t -> t.field("userId").value(userId)))
                        .aggregations("by_module", a -> a
                                .terms(t -> t.field("module").size(20))
                        )
                        .aggregations("by_operation", a -> a
                                .terms(t -> t.field("operation").size(50))
                        ),
                Void.class
        ));

        Map<String, Object> result = new HashMap<>();
        result.put("total", response.hits().total() != null ? response.hits().total().value() : 0);
        result.put("byModule", extractTermBuckets(response, "by_module", "module"));
        result.put("byOperation", extractTermBuckets(response, "by_operation", "operation"));
        return result;
    }

    @Override
    public List<Map<String, Object>> statsTopUsers(int limit) {
        SearchResponse<Void> response = executeQuery(() -> elasticsearchClient.search(s -> s
                        .index(INDEX_NAME)
                        .size(0)
                        .aggregations("top_users", a -> a
                                .terms(t -> t.field("userId").size(limit))
                        ),
                Void.class
        ));

        return response.aggregations().get("top_users")
                .lterms().buckets().array().stream()
                .map(bucket -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("userId", bucket.key());
                    item.put("count", bucket.docCount());
                    return item;
                })
                .collect(Collectors.toList());
    }

    // ==================== 工具方法 ====================

    private void doFlushWithLock() {
        synchronized (buffer) {
            if (!buffer.isEmpty()) {
                doFlush();
            }
        }
    }

    private void doFlush() {
        try {
            repository.saveAll(buffer);
            log.debug("批量写入 {} 条操作日志到 ES", buffer.size());
            buffer.clear();
        } catch (Exception e) {
            log.error("批量写入操作日志到 ES 失败: {}", e.getMessage(), e);
            buffer.clear();
        }
    }

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
        doc.setCreatedAt(dto.getCreatedAt() != null ? dto.getCreatedAt().format(FORMATTER) : null);
        return doc;
    }

    /**
     * 提取 term 聚合桶结果
     */
    private <T> List<Map<String, Object>> extractTermBuckets(SearchResponse<T> response,
                                                            String aggName, String keyField) {
        return response.aggregations().get(aggName)
                .sterms().buckets().array().stream()
                .map(bucket -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put(keyField, bucket.key().stringValue());
                    item.put("count", bucket.docCount());
                    return item;
                })
                .collect(Collectors.toList());
    }
}
