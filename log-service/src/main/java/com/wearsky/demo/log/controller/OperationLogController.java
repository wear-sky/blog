package com.wearsky.demo.log.controller;

import com.wearsky.demo.common.domain.vo.ApiResponse;
import com.wearsky.demo.log.service.OperationLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 操作日志查询/聚合 API
 */
@Slf4j
@RestController
@RequestMapping("/log-service/log")
@RequiredArgsConstructor
public class OperationLogController {

    private final OperationLogService operationLogService;

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
        return ApiResponse.success(operationLogService.page(pageNum, pageSize, userId, module, operation, startTime, endTime));
    }

    @GetMapping("/stats/module")
    public ApiResponse<List<Map<String, Object>>> statsByModule() {
        return ApiResponse.success(operationLogService.statsByModule());
    }

    @GetMapping("/stats/operation")
    public ApiResponse<List<Map<String, Object>>> statsByOperation() {
        return ApiResponse.success(operationLogService.statsByOperation());
    }

    @GetMapping("/stats/trend")
    public ApiResponse<List<Map<String, Object>>> statsByTrend(
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime
    ) {
        return ApiResponse.success(operationLogService.statsByTrend(startTime, endTime));
    }

    @GetMapping("/stats/user/{userId}")
    public ApiResponse<Map<String, Object>> statsByUser(@PathVariable Long userId) {
        return ApiResponse.success(operationLogService.statsByUser(userId));
    }

    @GetMapping("/stats/top-users")
    public ApiResponse<List<Map<String, Object>>> statsTopUsers(
            @RequestParam(defaultValue = "10") int limit
    ) {
        return ApiResponse.success(operationLogService.statsTopUsers(limit));
    }
}
