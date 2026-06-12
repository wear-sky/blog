package com.wearsky.demo.log.controller;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.json.JsonData;
import com.wearsky.demo.common.domain.vo.ApiResponse;
import com.wearsky.demo.log.document.OperationLogDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 操作日志查询/聚合 API
 */
@Slf4j
@RestController
@RequestMapping("/log-service/log")
@RequiredArgsConstructor
public class OperationLogController {

    private final ElasticsearchOperations elasticsearchOperations;
    private final ElasticsearchClient elasticsearchClient;

    /**
     * 分页查询日志列表
     */
    @GetMapping("/page")
    public ApiResponse<Map<String, Object>> page(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String operation,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime
    ) {
        Criteria criteria = new Criteria();
        if (userId != null) {
            criteria.and("userId").is(userId);
        }
        if (module != null && !module.isEmpty()) {
            criteria.and("module").is(module);
        }
        if (operation != null && !operation.isEmpty()) {
            criteria.and("operation").is(operation);
        }
        if (startTime != null && !startTime.isEmpty()) {
            criteria.and("createdAt").greaterThanEqual(LocalDateTime.parse(startTime, DateTimeFormatter.ISO_DATE_TIME));
        }
        if (endTime != null && !endTime.isEmpty()) {
            criteria.and("createdAt").lessThanEqual(LocalDateTime.parse(endTime, DateTimeFormatter.ISO_DATE_TIME));
        }

        CriteriaQuery query = new CriteriaQuery(criteria)
                .setPageable(PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "createdAt")));

        SearchHits<OperationLogDocument> hits = elasticsearchOperations.search(query, OperationLogDocument.class);

        Map<String, Object> result = new HashMap<>();
        result.put("total", hits.getTotalHits());
        result.put("list", hits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .toList());

        return ApiResponse.success(result);
    }

    /**
     * 按模块统计操作次数
     */
    @GetMapping("/stats/module")
    public ApiResponse<List<Map<String, Object>>> statsByModule() throws Exception {
        SearchResponse<Void> response = elasticsearchClient.search(s -> s
                        .index("operation-log")
                        .size(0)
                        .aggregations("by_module", a -> a
                                .terms(t -> t.field("module").size(20))
                        ),
                Void.class
        );

        List<Map<String, Object>> stats = response.aggregations().get("by_module")
                .sterms().buckets().array().stream()
                .map(bucket -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("module", bucket.key().stringValue());
                    item.put("count", bucket.docCount());
                    return item;
                })
                .collect(Collectors.toList());

        return ApiResponse.success(stats);
    }

    /**
     * 按操作类型统计
     */
    @GetMapping("/stats/operation")
    public ApiResponse<List<Map<String, Object>>> statsByOperation() throws Exception {
        SearchResponse<Void> response = elasticsearchClient.search(s -> s
                        .index("operation-log")
                        .size(0)
                        .aggregations("by_operation", a -> a
                                .terms(t -> t.field("operation").size(50))
                        ),
                Void.class
        );

        List<Map<String, Object>> stats = response.aggregations().get("by_operation")
                .sterms().buckets().array().stream()
                .map(bucket -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("operation", bucket.key().stringValue());
                    item.put("count", bucket.docCount());
                    return item;
                })
                .collect(Collectors.toList());

        return ApiResponse.success(stats);
    }

    /**
     * 按时间趋势统计（按天）
     */
    @GetMapping("/stats/trend")
    public ApiResponse<List<Map<String, Object>>> statsByTrend(
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime
    ) throws Exception {
        SearchResponse<Void> response = elasticsearchClient.search(s -> {
                    s.index("operation-log").size(0);
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
        );

        List<Map<String, Object>> trend = response.aggregations().get("daily_trend")
                .dateHistogram().buckets().array().stream()
                .map(bucket -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("date", bucket.keyAsString());
                    item.put("count", bucket.docCount());
                    return item;
                })
                .collect(Collectors.toList());

        return ApiResponse.success(trend);
    }

    /**
     * 某用户的操作统计
     */
    @GetMapping("/stats/user/{userId}")
    public ApiResponse<Map<String, Object>> statsByUser(@PathVariable Long userId) throws Exception {
        SearchResponse<Void> response = elasticsearchClient.search(s -> s
                        .index("operation-log")
                        .size(0)
                        .query(q -> q.term(t -> t.field("userId").value(userId)))
                        .aggregations("by_module", a -> a
                                .terms(t -> t.field("module").size(20))
                        )
                        .aggregations("by_operation", a -> a
                                .terms(t -> t.field("operation").size(50))
                        ),
                Void.class
        );

        Map<String, Object> result = new HashMap<>();
        result.put("total", response.hits().total() != null ? response.hits().total().value() : 0);

        List<Map<String, Object>> byModule = response.aggregations().get("by_module")
                .sterms().buckets().array().stream()
                .map(bucket -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("module", bucket.key().stringValue());
                    item.put("count", bucket.docCount());
                    return item;
                })
                .collect(Collectors.toList());
        result.put("byModule", byModule);

        List<Map<String, Object>> byOperation = response.aggregations().get("by_operation")
                .sterms().buckets().array().stream()
                .map(bucket -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("operation", bucket.key().stringValue());
                    item.put("count", bucket.docCount());
                    return item;
                })
                .collect(Collectors.toList());
        result.put("byOperation", byOperation);

        return ApiResponse.success(result);
    }

    /**
     * 最活跃用户 Top N
     */
    @GetMapping("/stats/top-users")
    public ApiResponse<List<Map<String, Object>>> statsTopUsers(
            @RequestParam(defaultValue = "10") int limit
    ) throws Exception {
        SearchResponse<Void> response = elasticsearchClient.search(s -> s
                        .index("operation-log")
                        .size(0)
                        .aggregations("top_users", a -> a
                                .terms(t -> t.field("userId").size(limit))
                        ),
                Void.class
        );

        List<Map<String, Object>> topUsers = response.aggregations().get("top_users")
                .lterms().buckets().array().stream()
                .map(bucket -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("userId", bucket.key());
                    item.put("count", bucket.docCount());
                    return item;
                })
                .collect(Collectors.toList());

        return ApiResponse.success(topUsers);
    }
}
