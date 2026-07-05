package com.wearsky.demo.log.service;

import com.wearsky.demo.common.dto.OperationLogDTO;

import java.util.List;
import java.util.Map;

/**
 * 操作日志服务接口
 */
public interface OperationLogService {

    /**
     * 保存操作日志
     */
    void saveLog(OperationLogDTO dto);

    /**
     * 分页查询日志列表
     */
    Map<String, Object> page(int pageNum, int pageSize, Long userId,
                             String module, String operation,
                             String startTime, String endTime);

    /**
     * 按模块统计操作次数
     */
    List<Map<String, Object>> statsByModule();

    /**
     * 按操作类型统计
     */
    List<Map<String, Object>> statsByOperation();

    /**
     * 按时间趋势统计（按天）
     */
    List<Map<String, Object>> statsByTrend(String startTime, String endTime);

    /**
     * 某用户的操作统计
     */
    Map<String, Object> statsByUser(Long userId);

    /**
     * 最活跃用户 Top N
     */
    List<Map<String, Object>> statsTopUsers(int limit);
}
