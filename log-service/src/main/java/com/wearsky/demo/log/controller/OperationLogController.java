package com.wearsky.demo.log.controller;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;
import com.wearsky.demo.common.domain.vo.ApiResponse;
import com.wearsky.demo.log.document.OperationLogDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 操作日志查询/聚合 API
 */
@Slf4j
@RestController
@RequestMapping("/log-service/log")
@RequiredArgsConstructor
public class OperationLogController {

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
    ) throws Exception {
        SearchResponse<OperationLogDocument> response = elasticsearchClient.search(s -> {
                    s.index("operation-log")
                            .from((pageNum - 1) * pageSize)
                            .size(pageSize)
                            .sort(so -> so.field(f -> f.field("createdAt").order(SortOrder.Desc)));

                    // 构建 bool query
                    s.query(q -> q.bool(b -> {
                        // term 精确匹配条件
                        if (userId != null) {
                            b.filter(f -> f.term(t -> t.field("userId").value(userId)));
                        }
                        if (module != null && !module.isEmpty()) {
                            b.filter(f -> f.term(t -> t.field("module").value(module)));
                        }
                        if (operation != null && !operation.isEmpty()) {
                            b.filter(f -> f.term(t -> t.field("operation").value(operation)));
                        }
                        // 时间范围条件
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
        );

        Map<String, Object> result = new HashMap<>();
        result.put("total", response.hits().total() != null ? response.hits().total().value() : 0);
        result.put("list", response.hits().hits().stream()
                .map(Hit::source)
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
